package connections;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import models.Book;
import models.User ;
import models.ViewCart ;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import jasper.JasperManager;
import jasper.JasperManager.Template;
import jasper.JasperManagerCSV;


public class DataBaseConnection {
    String url = "jdbc:mysql://localhost:3306/bookstore";
    String dbUser = "test";
    String password = "test";
    
    jasper.JasperManager jasper = new JasperManagerCSV() ;

    public static User active_user;

    public User getActive_user() {
        return active_user;
    }

    public void setActive_user(User active_user) {
        this.active_user = active_user;
    }

    //calling procedures cite: https://www.mysqltutorial.org/calling-mysql-stored-procedures-from-jdbc/

    // Method to Sign Up
    public Boolean sign_up (User user) {
        try{
            // Connect to the database
            Connection connection = DriverManager.getConnection(url, dbUser, password);
            // Call the sql required procedure to Sign Up
            CallableStatement statement = connection.prepareCall("{call signUp(?, ?, ?, ?, ?, ?, ?)}");
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getFirstName());
            statement.setString(4, user.getLastName());
            statement.setString(5, user.getEmailAddress());
            statement.setString(6, user.getPhoneNumber());
            statement.setString(7, user.getShippingAddress());
            statement.execute();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Method to Sign in
    public Boolean sign_in (User user){
        try{
            // Connect to the database
            Connection connection = DriverManager.getConnection(url, dbUser, password);
            // Call the sql required procedure to sign in
            CallableStatement statement = connection.prepareCall("{call signIn(?, ?, ?, ?, ?, ?, ?, ?)}");
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.execute();

            /* Store the current active user */
            User newUser = new User(user.getUsername(), user.getPassword(), statement.getString(3),
                                     statement.getString(4), statement.getString(5),
                                      statement.getString(6), statement.getString(7), statement.getBoolean(8));
            setActive_user(newUser);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Method to add a book
    public Boolean add_book (Book book){
        try{
            // String arrOfAuthors[] = authors.split(",");
            // Connect to the database
            Connection connection = DriverManager.getConnection(url, dbUser, password);
            // Call the sql required procedure to add the book
            CallableStatement statement1 = connection.prepareCall("{call addBook(?, ?, ?, ?, ?, ?, ?)}");
            statement1.setInt(1, book.getISBN());
            statement1.setString(2, book.getTitle());
            statement1.setString(3,book.getPublisher());
            statement1.setString(4, book.getCategory());
            statement1.setDouble(5, book.getSellingPrice());
            statement1.setInt(6, book.getPublicationYear());
            statement1.setInt(7, book.getThreshold());
            statement1.execute();

            // Call the sql required procedure to add the authors
            CallableStatement statement2 = connection.prepareCall("{call addAuthor(?, ?)}");
            for (String author : book.getAuthors()){
                statement2.setInt(1, book.getISBN());
                statement2.setString(2,author);
                statement2.execute();
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Method to edit the user info
    public Boolean edit_user_info (User user) {
        try {
            // Connect to the database
            Connection connection = DriverManager.getConnection(url, dbUser, password);
            // Call the sql required procedure to edit the user info
            CallableStatement statement = conn.prepareCall("{call editUserInfo(?, ?, ?, ?, ?, ?, ?, ?)}");
            statement.setString(1, getActive_user().getUsername());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getFirstName());
            statement.setString(4, user.getLastName());
            statement.setString(5, user.getEmailAddress());
            statement.setString(6, user.getPhoneNumber());
            statement.setString(7, user.getShippingAddress());
            statement.setString(8, user.getPassword());
            statement.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Method to search with any criteria
    public ArrayList<Book> search (String category, String search_text){
        ArrayList<java.awt.print.Book> result = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(url, dbUser, password);
            CallableStatement statement;
            if ( category.equals("ISBN") ){
                int ISBN = Integer.parseInt(search_text);
                statement = search_ISBN(ISBN, connection);
            }
            else if ( category.equals("Title") ){
                statement = search_Title(search_text, connection);
            }
            else if ( category.equals("Category") ){
                statement = search_Category(search_text, connection);
            }
            else if ( category.equals("Author") ){
                statement = search_Author(search_text, connection);
            }
            else {
                statement = search_Publisher(search_text, connection);
            }
            statement.execute();
            ResultSet r = statement.getResultSet();

            while (r.next()) {
                Book book = new Book(r.getString(1), r.getString(2), r.getString(3), r.getString(6), r.getString(5), r.getString(4), r.getString(7));
                result.add(book);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public CallableStatement search_ISBN (int ISBN, Connection connection) throws Exception {
        CallableStatement statement = connection.prepareCall("{call searchByISBN(?)}");
        statement.setInt(1, ISBN);
        return statement;
    }

    public CallableStatement search_Title (String search_text, Connection connection) throws Exception {
        CallableStatement statement = connection.prepareCall("{call searchByTitle(?)}");
        statement.setString(1, search_text);
        return statement;
    }

    public CallableStatement search_Category (String search_text, Connection connection) throws Exception {
        CallableStatement statement = connection.prepareCall("{call searchByCategory(?)}");
        statement.setString(1, search_text);
        return statement;
    }

    public CallableStatement search_Author (String search_text, Connection connection) throws Exception {
        CallableStatement statement = connection.prepareCall("{call searchByAuthor(?)}");
        statement.setString(1, search_text);
        return statement;
    }

    public CallableStatement search_Publisher (String search_text, Connection connection) throws Exception {
        CallableStatement statement = connection.prepareCall("{call searchByPublisher(?)}");
        statement.setString(1, search_text);
        return statement;
    }

    
    // Method to get cart
    public void AddtoCart(int ISBN,int Copies) throws SQLException {

        Connection connection = DriverManager.getConnection(url, dbUser, password);
        String query = "{call AddtoCart(?,?,?)}" ;

        CallableStatement  callableStatement =  connection.prepareCall(query) ;
        //set query vars
        callableStatement.setString(1, active_user.getUsername());
        callableStatement.setInt(2, ISBN);
        callableStatement.setInt(3, Copies);
        callableStatement.execute() ;

    }

    // Method to add to cart a book
    public ArrayList<ViewCart> GetCart(String UserName) throws SQLException {
        ArrayList<ViewCart> viewCarts = new ArrayList<ViewCart>() ;
        Connection connection = DriverManager.getConnection(url, dbUser, password);
        String query = "{call ViewCart(?)}" ;

        CallableStatement  callableStatement =  connection.prepareCall(query) ;
        callableStatement.setString(1, UserName);
        callableStatement.execute();
        ResultSet result = callableStatement.getResultSet() ;

        //Parse and add tuples to the array list
        while(result.next()) {
            ViewCart viewCart = new ViewCart(result.getString(1), result.getString(2), result.getString(3),
                    result.getString(4), result.getString(5), result.getString(6), result.getString(7), result.getString(8));
            viewCarts.add(viewCart);
        }


        return viewCarts ;
    }

    //Method to remove from cart
    public void RemovefromCart(int ISBN) throws SQLException {
    	Connection connection = DriverManager.getConnection(url, dbUser, password);
    	String query = "{call RemovefromCart(?,?)}" ;
    	
    	
    	CallableStatement  callableStatement =  connection.prepareCall(query) ;
    	callableStatement.setString(1,active_user.getUsername());
    	callableStatement.setInt(2, ISBN);
    	callableStatement.execute(); 
    }
    //Method to check out

    public void CheckOut()  {
    	Connection connection = null;
		try {
			String query = "{call checkout(?)}" ;
			connection = DriverManager.getConnection(url, dbUser, password);
			CallableStatement  callableStatement =  connection.prepareCall(query) ;
	    	callableStatement.setString(1,active_user.getUsername());
	    	//user in real life can check out in same time so 
	    	//need to synchronize this process and make sure only 1 user checks
	    	
	    	connection.setAutoCommit(false);
	    	
	    	callableStatement.execute(); 
	    	connection.commit();
		} catch (SQLException e) {
			//if connection cant commit return to latest point
			 try {
	                assert connection != null;
	                connection.rollback();
	            } catch (SQLException e2) {
	                e2.printStackTrace();
	            }
		}
    	//commit again
		 try {
	            connection.setAutoCommit(true);
	        } catch (SQLException e) {
	            e.printStackTrace();
	       }	
    	
    	
    }
    
    //Method to log out
    public void LogOut() throws SQLException {
    	Connection connection = DriverManager.getConnection(url, dbUser, password);
    	String query = "{call logout(?)}" ;
    	
    	
    	CallableStatement  callableStatement =  connection.prepareCall(query) ;
    	callableStatement.setString(1,active_user.getUsername());
    	
    	callableStatement.execute();
    }
    
    //Method to promote user
    public void Promote(String username) throws SQLException {
    	Connection connection = DriverManager.getConnection(url, dbUser, password);
    	String query = "{call promote(?)}" ;
    	
    	
    	CallableStatement  callableStatement =  connection.prepareCall(query) ;
    	callableStatement.setString(1,username);
    	
    	callableStatement.execute(); 
    	
    }

    //Generate last month report
    public void GenerateLastMonthReport() throws SQLException, IOException {
    	Connection connection = DriverManager.getConnection(url, dbUser, password);
    	String query = "{totalSalesPrevMonthReport()}" ;
    	
    	
    	CallableStatement  callableStatement =  connection.prepareCall(query) ;
    	
    	callableStatement.execute(); 
    	FileWriter myWriter = new FileWriter("totalSalesPrevMonthReport.csv");
        ResultSet resultSet = callableStatement.getResultSet();
    	//write csv file 
        while (resultSet.next()) {
            for (int i = 1; i < 9; ++i) {
                myWriter.append(resultSet.getString(i));
                //if not the last column
                if (i != 8) {
                    myWriter.append(",");
                }
            }
            myWriter.append("\n");
        }
        myWriter.close();
        jasper.generatePDF("totalSalesPrevMonthReport.csv", Template.SALES_REPORT) ;
    }

    public void TopFivePurchase() throws SQLException, IOException {
    	Connection connection = DriverManager.getConnection(url, dbUser, password);
    	String query = "{topFiveCustomersReport()}" ;
    	
    	
    	CallableStatement  callableStatement =  connection.prepareCall(query) ;
    	
    	callableStatement.execute(); 
    	FileWriter myWriter = new FileWriter("TopFivePurchase.csv");
        ResultSet resultSet = callableStatement.getResultSet();
    	//write csv file 
        while (resultSet.next()) {
            for (int i = 1; i < 5; ++i) {
                myWriter.append(resultSet.getString(i));
                //if not the last column
                if (i != 4) {
                    myWriter.append(",");
                }
            }
            myWriter.append("\n");
        }
        myWriter.close();
        jasper.generatePDF("TopFivePurchase.csv", Template.TOP_CUSTOMERS) ;
        
        
    }

    public void TopTenBooks() throws SQLException, IOException {
    	Connection connection = DriverManager.getConnection(url, dbUser, password);
    	String query = "{topTenBestSellersReport()}" ;
    	
    	
    	CallableStatement  callableStatement =  connection.prepareCall(query) ;
    	
    	callableStatement.execute(); 
    	FileWriter myWriter = new FileWriter("TopTenBooks.csv");
        ResultSet resultSet = callableStatement.getResultSet();
    	//write csv file 
        while (resultSet.next()) {
            for (int i = 1; i < 4; ++i) {
                myWriter.append(resultSet.getString(i));
                //if not the last column
                if (i != 3) {
                    myWriter.append(",");
                }
            }
            myWriter.append("\n");
        }
        myWriter.close();
        jasper.generatePDF("TopTenBooks.csv", Template.BEST_SELLERS) ;
        
        
    }

}
