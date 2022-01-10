CREATE SCHEMA IF NOT EXISTS BOOKSTORE;

USE BOOKSTORE;

#one to many relation with BOOK table
CREATE TABLE IF NOT EXISTS PUBLISHER
(
    Name         VARCHAR(50) UNIQUE NOT NULL PRIMARY KEY,
    Address      VARCHAR(100),
    Phone_Number VARCHAR(20) UNIQUE
);


CREATE TABLE IF NOT EXISTS BOOK
(
    ISBN             INT UNIQUE          NOT NULL PRIMARY KEY,
    Title            VARCHAR(100) UNIQUE NOT NULL,
    Publisher_Name   VARCHAR(50)         NOT NULL,
    Category         VARCHAR(10)         NOT NULL,
    Selling_Price    DECIMAL(6, 2)       NOT NULL,
    Publication_Year YEAR                NOT NULL,
    No_of_Copies     INT                          DEFAULT 0,
    Threshold        INT                 NOT NULL DEFAULT 0,
    #indices for ISBN,Title and Category as this books' info can be
    #retrieved by users 150,000 time per day check
    #so using index will make the Select operation faster
	UNIQUE INDEX `ISBN_index` (`ISBN`) VISIBLE,
    INDEX `Title_index` (`Title`) VISIBLE,
    INDEX `Category_index` (`Category`) VISIBLE,
    FOREIGN KEY (Publisher_Name)
        REFERENCES PUBLISHER (Name),
	# check constrain for required categories
    CHECK (Category = 'Science' OR Category = 'Art'
        OR Category = 'Religion'
        OR Category = 'History'
        OR Category = 'Geography')
);

#one author for many books (one to many) 
CREATE TABLE IF NOT EXISTS AUTHOR
(
    Name VARCHAR(50) NOT NULL,
    ISBN INT         NOT NULL,
    FOREIGN KEY (ISBN)
        REFERENCES BOOK (ISBN) ON UPDATE CASCADE,
    PRIMARY KEY (Name, ISBN)
);


CREATE TABLE IF NOT EXISTS BOOK_ORDER
(
    Order_No INT AUTO_INCREMENT UNIQUE NOT NULL PRIMARY KEY,
    ISBN     INT UNIQUE                NOT NULL,
    Quantity INT                       NOT NULL DEFAULT 1,
    FOREIGN KEY (ISBN)
    #on update cascade, so when the book exceeds a threshold 
    #we place order in this table so it will be updated also in Book table
        REFERENCES BOOK (ISBN) ON UPDATE CASCADE
);
#Assumption: No index as the customer table use many insert or update
#which take more time in case of indices as the indices must be edites as
#the tuples edited
CREATE TABLE IF NOT EXISTS CUSTOMER
(
    User_Name        VARCHAR(25) UNIQUE  NOT NULL PRIMARY KEY,
    Password         VARCHAR(16)         NOT NULL,
    First_Name       VARCHAR(25)         NOT NULL,
    Last_Name        VARCHAR(25)         NOT NULL,
    Email_Address    VARCHAR(100) UNIQUE NOT NULL,
    Phone_Number     VARCHAR(20) UNIQUE,
    Shipping_Address VARCHAR(100)        NOT NULL,
    Is_Manager       BOOLEAN             NOT NULL
);

CREATE TABLE IF NOT EXISTS SHOPPING_CART
(
    User_Name    VARCHAR(25) NOT NULL,
    ISBN         INT         NOT NULL,
    No_of_Copies INT DEFAULT 1,
    PRIMARY KEY (User_Name, ISBN),
    FOREIGN KEY (User_Name)
        REFERENCES CUSTOMER (User_Name) ON UPDATE CASCADE,
    FOREIGN KEY (ISBN)
        REFERENCES BOOK (ISBN) ON UPDATE CASCADE
);
#Assumption: No index as the customer table use many insert or update
#which take more time in case of indices as the indices must be edites as
#the tuples edited
CREATE TABLE IF NOT EXISTS SALE
(
    Sale_ID      INT AUTO_INCREMENT UNIQUE NOT NULL PRIMARY KEY,
    User_Name    VARCHAR(25)               NOT NULL,
    ISBN         INT                       NOT NULL,
    No_of_Copies INT DEFAULT 1,
    Date         DATE                     NOT NULL,
    FOREIGN KEY (User_Name)
        REFERENCES CUSTOMER (User_Name) ON UPDATE CASCADE,
    FOREIGN KEY (ISBN)
        REFERENCES BOOK (ISBN) ON UPDATE CASCADE
);


#Place order trigger
DELIMITER $$
CREATE
    TRIGGER Place_Order
    AFTER UPDATE
    ON BOOK
    FOR EACH ROW
BEGIN
    IF (OLD.No_of_Copies >= OLD.Threshold AND NEW.No_of_Copies < NEW.Threshold) THEN
        INSERT INTO BOOK_ORDER (ISBN,Quantity) VALUES (OLD.ISBN,OLD.Threshold);
    END IF;
END$$
DELIMITER ;

DELIMITER $$
CREATE
    TRIGGER Confirm_Order
    BEFORE DELETE
    ON BOOK_ORDER
    FOR EACH ROW
BEGIN
    UPDATE BOOK
    SET No_of_Copies = No_of_Copies + OLD.Quantity
    WHERE ISBN = OLD.ISBN;
END $$
DELIMITER ;

DELIMITER $$
CREATE
    TRIGGER Non_Zero_Book_Quantity
    BEFORE UPDATE
    ON BOOK
    FOR EACH ROW
BEGIN
    IF (NEW.No_of_Copies < 0) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Sold Out'; 
    END IF;
END $$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE signIn(IN UserName VARCHAR(25), IN Pass VARCHAR(16),out FName VARCHAR(25),
                              out LName VARCHAR(25),out Email VARCHAR(100),out PhoneNo VARCHAR(20),
                              out ShipAdd VARCHAR(100), OUT IsManager BOOLEAN)
BEGIN
	IF NOT EXISTS(SELECT *
                  FROM CUSTOMER
                  WHERE User_Name = UserName
                    AND Password = Pass) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid Username or Password !!';
    END IF;
	SELECT First_Name, Last_Name, Email_Address, Phone_Number, Shippping_Address, Is_Manager
	INTO FName , LName , Email , PhoneNo , ShipAdd , IsManager
	FROM CUSTOMER
	WHERE User_Name = UserName;
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE signUp(IN UserName VARCHAR(25), IN Pass VARCHAR(16), IN FnameP VARCHAR(25), IN LnameP VARCHAR(25),
                        IN Email VARCHAR(100), IN PhoneNo VARCHAR(20), IN ShipAdd VARCHAR(100))
BEGIN
    INSERT INTO CUSTOMER VALUES (UserName, Pass, FnameP, LnameP, Email, PhoneNo, ShipAdd, 0);
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE addPublisher(IN Name VARCHAR(50), IN Address VARCHAR(100), IN PhoneNo VARCHAR(20))
BEGIN
    INSERT INTO PUBLISHER VALUES (Name, Address, PhoneNo);
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE addBook(IN ISBN INT, IN Title VARCHAR(100), IN PubName VARCHAR(50), IN Category VARCHAR(10),
                         IN Price DECIMAL(6, 2), IN PubYear YEAR, IN Threshold INT)
BEGIN
    INSERT INTO BOOK (ISBN, Title, Publisher_Name, Category, Selling_Price, Publication_Year, Threshold)
    VALUES (ISBN, Title, PubName, Category, Price, PubYear, Threshold);
    INSERT INTO BOOK_ORDER (ISBN) VALUES (ISBN);
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE addAuthor(IN ISBN INT, IN Name VARCHAR(50))
BEGIN
    INSERT INTO AUTHOR VALUES (Name, ISBN);
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE removeAuthors(IN ISBNP INT)
BEGIN
    DELETE
    FROM AUTHOR
    WHERE ISBN = ISBNP;
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE modifyBook(IN ISBNP INT, IN NewISBN INT, IN TitleP VARCHAR(100), IN PubName VARCHAR(50),
                            IN CategoryP VARCHAR(10), IN Price DECIMAL(6, 2), IN PubYear YEAR, IN ThresholdP INT)
BEGIN
    UPDATE BOOK
    SET ISBN             = NewISBN,
        Title            = TitleP,
        Publisher_Name   = PubName,
        Category         = CategoryP,
        Selling_Price    = Price,
        Publication_Year = PubYear,
        Threshold        = ThresholdP
    WHERE ISBN = ISBNP;
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE confirmOrder(IN OrderNo INT)
BEGIN
    DELETE
    FROM BOOK_ORDER
    WHERE Order_No = OrderNo;
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE createOrder(IN OrderNo INT, IN ISBN INT, IN QUANTITY INT)
BEGIN
    INSERT INTO BOOK_ORDER VALUES (OrderNo, ISBN, QUANTITY);
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE searchByISBN(IN targetISBN INT)
BEGIN
    SELECT ISBN,
           Title,
           Publisher_Name   AS 'Publisher Name',
           Category,
           Selling_Price    AS 'Price',
           Publication_Year AS 'Publication Year',
           No_of_Copies     AS 'Number of Copies in Stock'
    FROM BOOK
    WHERE ISBN LIKE CONCAT('%', targetISBN, '%');
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE searchByTitle(IN targetTitle VARCHAR(100))
BEGIN
    SELECT ISBN,
           Title,
           Publisher_Name   AS 'Publisher Name',
           Category,
           Selling_Price    AS 'Price',
           Publication_Year AS 'Publication Year',
           No_of_Copies     AS 'Number of Copies in Stock'
    FROM BOOK
    WHERE Title LIKE CONCAT('%', targetTitle, '%');
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE searchByCategory(IN targetCategory VARCHAR(10))
BEGIN
    SELECT ISBN,
           Title,
           Publisher_Name   AS 'Publisher Name',
           Category,
           Selling_Price    AS 'Price',
           Publication_Year AS 'Publication Year',
           No_of_Copies     AS 'Number of Copies in Stock'
    FROM BOOK
    WHERE Category LIKE CONCAT('%', targetCategory, '%');
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE searchByPublisher(IN targetPublisher VARCHAR(50))
BEGIN
    SELECT ISBN,
           Title,
           Publisher_Name   AS 'Publisher Name',
           Category,
           Selling_Price    AS 'Price',
           Publication_Year AS 'Publication Year',
           No_of_Copies     AS 'Number of Copies in Stock'
    FROM BOOK
    WHERE Publisher_Name LIKE CONCAT('%', targetPublisher, '%');
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE searchByAuthor(IN targetAuthor VARCHAR(10))
BEGIN
    SELECT ISBN,
           Title,
           Publisher_Name   AS 'Publisher Name',
           Category,
           Selling_Price    AS 'Price',
           Publication_Year AS 'Publication Year',
           No_of_Copies     AS 'Number of Copies in Stock',
           Name             AS 'Author'
    FROM BOOK
             NATURAL JOIN
         AUTHOR
    WHERE AUTHOR.Name LIKE CONCAT('%', targetAuthor, '%');
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE editUserInfo(IN Username VARCHAR(25), IN NewUsername VARCHAR(25), IN FName VARCHAR(25),
                              IN LName VARCHAR(25), IN Email VARCHAR(100), IN PhoneNo VARCHAR(20), IN ShipAdd VARCHAR(100), IN Pass VARCHAR(16))
BEGIN
    UPDATE CUSTOMER
    SET User_Name        = NewUsername,
        First_Name       = Fname,
        Last_Name        = LName,
        Email_Address    = Email,
        Phone_Number     = PhoneNo,
        Shipping_Address = ShipAdd,
        Password = Pass
    WHERE User_Name = Username;
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE editUserPassword(IN Username VARCHAR(25), IN Pass VARCHAR(16), IN NewPass VARCHAR(16))
BEGIN
    IF NOT EXISTS(SELECT *
                  FROM CUSTOMER
                  WHERE User_Name = Username
                    AND Password = Pass) Then
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'INCORRECT PASSWORD';
    END IF;
    UPDATE CUSTOMER SET Password = NewPass WHERE User_Name = Username;
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE AddtoCart(IN Username VARCHAR(25), IN ISBN INT, IN Copies INT)
BEGIN
	#
    INSERT INTO SHOPPING_CART VALUES (Username, ISBN, Copies);
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE ViewCart(IN Username VARCHAR(25))
BEGIN
    SELECT BOOK.ISBN,
           Title,
           Category,
           Publisher_Name                             AS 'Publisher Name',
           Publication_Year                           AS 'Publication Year',
           SHOPPING_CART.No_of_Copies                 AS 'Quantity',
           Selling_Price                              AS 'Unit Price',
           SELLING_PRICE * SHOPPING_CART.No_of_Copies AS 'Price'
    FROM BOOK
             JOIN
         SHOPPING_CART ON SHOPPING_CART.ISBN = BOOK.ISBN
    WHERE SHOPPING_CART.User_Name = Username;
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE RemovefromCart(IN Username VARCHAR(25), IN ISBNP INT)
BEGIN
    DELETE FROM SHOPPING_CART WHERE User_Name = Username AND ISBN = ISBNP;
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE checkout(IN Username VARCHAR(25))
BEGIN
	
    SAVEPOINT sp1;
    INSERT INTO SALE (User_Name, ISBN, No_of_Copies, Date)
    #get all books of this user from cart
    SELECT *,
           CURDATE()
    FROM SHOPPING_CART
    WHERE User_Name = Username;
    #update book copies after buying
    UPDATE BOOK JOIN SHOPPING_CART ON BOOK.ISBN = SHOPPING_CART.ISBN
    SET BOOK.No_of_Copies = BOOK.No_of_Copies - SHOPPING_CART.No_of_Copies
    WHERE SHOPPING_CART.User_Name = Username;
    #delete the books of that user in the cart
    DELETE FROM SHOPPING_CART WHERE User_Name = Username;
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE logout(IN Username VARCHAR(25))
BEGIN
    DELETE FROM SHOPPING_CART where User_Name = Username ;
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE promote(IN Username VARCHAR(25))
BEGIN
    UPDATE CUSTOMER SET Is_Manager = 1 WHERE User_Name = Username;
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE totalSalesPrevMonthReport()
BEGIN
    SELECT Sale_ID                                AS 'Sale ID',
           User_Name                              AS 'Customer Name',
           SALE.ISBN                              AS 'ISBN',
           BOOK.Title                             AS 'Title',
           SALE.No_of_Copies                      AS 'Qty',
           BOOK.Selling_Price                     AS 'Unit Price',
           BOOK.Selling_Price * SALE.No_of_Copies AS 'Price',
           SALE.Date                              AS 'Date'
    FROM SALE
             JOIN
         BOOK ON SALE.ISBN = BOOK.ISBN
    WHERE DATEDIFF(CURDATE(), SALE.DATE) <= 30
    ORDER BY Sale_ID ASC;
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE topFiveCustomersReport()
BEGIN
    SELECT SALE.User_Name                              AS 'Username',
           Customer.Last_Name                          AS 'Last Name',
           Customer.First_Name                         AS 'First Name',
           SUM(BOOK.Selling_Price * SALE.No_of_Copies) AS 'Total Purchase Amount'
    FROM SALE
             JOIN
         CUSTOMER ON SALE.User_Name = CUSTOMER.User_Name
             JOIN
         BOOK ON SALE.ISBN = BOOK.ISBN
    WHERE DATEDIFF(CURDATE(), SALE.DATE) <= 90
    GROUP BY SALE.User_Name
    ORDER BY SUM(BOOK.Selling_Price * SALE.No_of_Copies) DESC
    LIMIT 5;
END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE topTenBestSellersReport()
BEGIN
    SELECT SALE.ISBN,
           BOOK.Title,
           SUM(SALE.No_of_Copies) AS 'Total Sold Copies'
    FROM SALE
             JOIN
         BOOK ON SALE.ISBN = BOOK.ISBN
    WHERE DATEDIFF(CURDATE(), SALE.DATE) <= 90
    GROUP BY ISBN
    ORDER BY SUM(SALE.No_of_Copies) DESC
    LIMIT 10;
END$$
DELIMITER ;

SELECT *
FROM CUSTOMER;