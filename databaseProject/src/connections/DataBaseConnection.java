package connections;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import models.User ;

public class DataBaseConnection {
    String url = "jdbc:mysql://localhost:3306/bookstore";
    String dbUser = "test";
    String password = "test";

    public Boolean sign_up (User user) {
        try{
            System.out.println("okok");
            Connection connection = DriverManager.getConnection(url, dbUser, password);
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

    public Boolean sign_in (User user){
        try{
            Connection connection = DriverManager.getConnection(url, dbUser, password);
            CallableStatement statement = connection.prepareCall("{call signIn(?, ?, ?)}");
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.execute();
            user.setIsManager(statement.getBoolean(3));
        } catch (SQLException e) {
            return false;
        }
        return true;
    }




}
