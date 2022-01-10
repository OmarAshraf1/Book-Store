package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {


    public Label usernameToShow;
    public ChoiceBox<String> searchCategories;
    public TextField searchName;
    public TextField copiesNumber;
    public TextField BookISBN;

    private final String[] allCategories = {"ISBN","Tittle","Category","author","publisher"};
    public TextField firstnameField;
    public TextField lastnameField;
    public TextField usernameField;
    public TextField emailField;
    public PasswordField passwordField;
    public TextField shippingAdressField;
    public TextField phoneNumberFiled;
    private String searchCategory ;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchCategories.getItems().addAll(allCategories);
        searchCategories.setValue("Tittle");
        searchCategories.setOnAction(this::changeCategory);
        this.usernameToShow.setText(Main.currentUser.getUsername());
    }

    public void changeCategory(ActionEvent event){
        this.searchCategory = searchCategories.getValue();
    }

    public void search(ActionEvent event) {
        System.out.println(this.searchCategory + " " + this.searchName.getText());
    }

    public void addBooksToCart(ActionEvent event) {
    }

    public void returnToLogin(ActionEvent event) throws IOException {
        Parent loginSceneParent = FXMLLoader.load(getClass().getResource("../scenes/loginScreen.fxml"));
        Scene loginScene = new Scene(loginSceneParent);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow() ;

        window.setScene(loginScene);
        window.show();
    }

    public void goToCartPage(ActionEvent event) throws IOException {
        Parent CartSceneParent = FXMLLoader.load(getClass().getResource("../scenes/cartItemsScreen.fxml"));
        Scene CartScene = new Scene(CartSceneParent);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow() ;

        window.setScene(CartScene);
        window.show();
    }

    public void editProfilePage(ActionEvent event) throws IOException {
        Parent editProfileSceneParent = FXMLLoader.load(getClass().getResource("../scenes/editProfileScreen.fxml"));
        Scene editProfileScene = new Scene(editProfileSceneParent);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow() ;

        window.setScene(editProfileScene);
        window.show();
    }

    public void exit(ActionEvent event) {
        System.exit(0);
    }
}
