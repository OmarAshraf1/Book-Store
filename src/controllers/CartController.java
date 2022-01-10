package controllers;

import connections.DataBaseConnection;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.ViewCart;
import sample.Main;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CartController implements Initializable {
    public Label usernameToShow;
    public TextField ISBNbookToRemove;
    public TextField ISBNbookToAdd;
    public TextField numberOfCopies;
    public Label cost;

    //table view
    public TableView<ViewCart> cartTable;
    public TableColumn<ViewCart,String> col_ISBN;
    public TableColumn<ViewCart,String> col_title;
    public TableColumn<ViewCart,String> col_copiesNum;
    public TableColumn<ViewCart,String> col_author;
    public TableColumn<ViewCart,String> col_category;
    public TableColumn<ViewCart,String> col_sellingPrice;
    public TableColumn<ViewCart,String> col_totalPrice;
    public TableColumn<ViewCart,String> col_publisher;
    public TableColumn<ViewCart,String> col_publicationYear;

    ObservableList<ViewCart> observableList = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.usernameToShow.setText(DataBaseConnection.active_user.getUsername());

        try {
            DataBaseConnection DBconnection = new DataBaseConnection();
            ArrayList<ViewCart> result = DBconnection.GetCart(DataBaseConnection.active_user.getUsername());
            observableList.addAll(result);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //set table columns
        col_ISBN.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        col_title.setCellValueFactory(new PropertyValueFactory<>("title"));
        col_copiesNum.setCellValueFactory(new PropertyValueFactory<>("copies"));
        col_author.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        col_category.setCellValueFactory(new PropertyValueFactory<>("category"));
        col_sellingPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        col_totalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        col_publisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        col_publicationYear.setCellValueFactory(new PropertyValueFactory<>("year"));

        cartTable.setItems(observableList);

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
        DBconnection.GetCart(DataBaseConnection.active_user.getUsername());
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
