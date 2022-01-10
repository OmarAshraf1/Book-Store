package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import models.User ;
import connections.DataBaseConnection;
import sample.Main;

public class LoginController {


    public TextField usernameFiled;
    public PasswordField passwordField;

    public void changeToSignup(ActionEvent event) throws IOException {
        Parent signupSceneParent = FXMLLoader.load(getClass().getResource("../scenes/signupScreen.fxml"));
        Scene signupScene = new Scene(signupSceneParent);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow() ;

        window.setScene(signupScene);
        window.show();
    }

    public void checkLogin(ActionEvent event) throws IOException {
        User newUser = new User(this.usernameFiled.getText(),this.passwordField.getText());
        DataBaseConnection DBconnection = new DataBaseConnection();

        if (DBconnection.sign_in(newUser)) {
            System.out.println("successfully login");
            Main.currentUser = newUser ;
            this.goToHomePage(event);
        }
        else
            System.out.println("invalid input");
            
    }

    public void goToHomePage(ActionEvent event) throws IOException {
        Parent HomeSceneParent = FXMLLoader.load(getClass().getResource("../scenes/homeScreen.fxml"));
        Scene HomeScene = new Scene(HomeSceneParent);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow() ;

        window.setScene(HomeScene);
        window.show();
    }

    public void exit(ActionEvent event){
        System.exit(0);
    }

}
