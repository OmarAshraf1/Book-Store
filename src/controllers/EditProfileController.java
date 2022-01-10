package controllers;

import connections.DataBaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditProfileController implements Initializable {

    public Label usernameToShow;

    public TextField firstnameField;
    public TextField lastnameField;
    public TextField usernameField;
    public TextField emailField;
    public PasswordField passwordField;
    public TextField shippingAdressField;
    public TextField phoneNumberFiled;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.firstnameField.setText(DataBaseConnection.active_user.getFirstName());
        this.lastnameField.setText(DataBaseConnection.active_user.getLastName());
        this.usernameField.setText(DataBaseConnection.active_user.getUsername());
        this.emailField.setText(DataBaseConnection.active_user.getEmailAddress());
        this.passwordField.setText(DataBaseConnection.active_user.getPassword());
        this.shippingAdressField.setText(DataBaseConnection.active_user.getShippingAddress());
        this.phoneNumberFiled.setText(DataBaseConnection.active_user.getPhoneNumber());

    }

    public void setTheNewData(ActionEvent event) {
    }

    public void backToHome(ActionEvent event) throws IOException {
        Parent HomeSceneParent = FXMLLoader.load(getClass().getResource("../scenes/homeScreen.fxml"));
        Scene HomeScene = new Scene(HomeSceneParent);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow() ;

        window.setScene(HomeScene);
        window.show();
    }

    public void returnToLogin(ActionEvent event) throws IOException {
        Parent loginSceneParent = FXMLLoader.load(getClass().getResource("../scenes/loginScreen.fxml"));
        Scene loginScene = new Scene(loginSceneParent);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow() ;

        window.setScene(loginScene);
        window.show();
    }
}
