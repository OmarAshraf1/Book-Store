package controllers;

import connections.DataBaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.Main;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CartController implements Initializable {
    public Label usernameToShow;
    public TextField ISBNbookToRemove;
    public TextField ISBNbookToAdd;
    public TextField numberOfCopies;
    public Label cost;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.usernameToShow.setText(Main.currentUser.getUsername());
    }

    public void backToHome(ActionEvent event) throws IOException {
        Parent HomeSceneParent = FXMLLoader.load(getClass().getResource("../scenes/homeScreen.fxml"));
        Scene HomeScene = new Scene(HomeSceneParent);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow() ;

        window.setScene(HomeScene);
        window.show();
    }

    public void AddBooksToCart(ActionEvent event) {
        System.out.println(this.ISBNbookToAdd.getText()+" "+this.numberOfCopies.getText());
    }

    public void checkout(ActionEvent event) throws SQLException {
        DataBaseConnection DBconnection = new DataBaseConnection();
        System.out.println(DBconnection.GetCart(Main.currentUser.getUsername()).get(0).getPrice());
    }

    public void removeBookFromCart(ActionEvent event) {
        System.out.println(this.ISBNbookToRemove.getText());
    }

    public void clearCart(ActionEvent event) {
    }

    public void exit(ActionEvent event) {
        System.exit(0);
    }
}
