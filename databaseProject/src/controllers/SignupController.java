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

public class SignupController {

    public TextField firstnameField;
    public TextField lastnameField;
    public TextField usernameField;
    public TextField emailField;
    public PasswordField passwordField;
    public TextField shippingAdressField;
    public TextField phoneNumberFiled;

    public void changeToLogin(ActionEvent event) throws IOException {
        Parent signupSceneParent = FXMLLoader.load(getClass().getResource("../scenes/loginScreen.fxml"));
        Scene signupScene = new Scene(signupSceneParent);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow() ;

        window.setScene(signupScene);
        window.show();
    }

    public void signup(ActionEvent event){
        User newUser = new User(this.usernameField.getText(),this.passwordField.getText(),this.firstnameField.getText(),
                this.lastnameField.getText(),this.emailField.getText(),this.phoneNumberFiled.getText(),this.shippingAdressField.getText());

        DataBaseConnection DBconnection = new DataBaseConnection();
        if (DBconnection.sign_up(newUser)){
            System.out.println("succefully created");
        }
        else{
            System.out.println("failed to creat this user");
        }
    }

    public void exit(ActionEvent event){
        System.exit(0);
    }

}
