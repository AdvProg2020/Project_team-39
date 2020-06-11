package view.UserPanel.AdminPanel.Products;

import controller.AdminController;
import controller.ExceptionsLibrary;
import controller.SellerController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.Account;
import model.Product;
import view.AlertBox.ErrorBox.ErrorBoxStart;
import view.AlertBox.MessageBox.AlertBoxStart;
import view.RegisterAndLoginStage.Register.RegisterScene;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Products implements Initializable {

    @FXML
    private TableView table;
    @FXML
    private Button remove;

    ObservableList<Product> products = null;
    TableColumn<Product,Integer> productId = new TableColumn<>("Product ID");
    TableColumn<Product,String> name = new TableColumn<>("Name");
    TableColumn<Product,String> company = new TableColumn<>("Company");
    TableColumn<Product,Double> price = new TableColumn<>("Price");
    TableColumn<Product,Integer> quantity = new TableColumn<>("Quantity");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productId.setStyle("-fx-alignment: CENTER");
        name.setStyle("-fx-alignment: CENTER");
        company.setStyle("-fx-alignment: CENTER");
        price.setStyle("-fx-alignment: CENTER");
        quantity.setStyle("-fx-alignment: CENTER");
        table.getColumns().addAll(productId, name, company,price, quantity);
        updateTable();
    }

    public void removeButtonClicked() throws IOException {
        try {
            if (table.getSelectionModel().getSelectedItem() == null){
                AlertBoxStart.messageRun("Notice!","You should select a product from the table first!");
                return;
            }
            Product product = (Product) table.getSelectionModel().getSelectedItem();
            AdminController.deleteProduct(product.getProductId());
            AlertBoxStart.messageRun("Message", "Product deleted!");
            updateTable();
        } catch (ExceptionsLibrary.NoProductException | ExceptionsLibrary.NoAccountException e) {
            ErrorBoxStart.errorRun(e);
        }
    }

    private void updateTable() {
        try {
            products = FXCollections.observableArrayList(AdminController.getAllProducts());
        } catch (ExceptionsLibrary.NoProductException e) {
            try {
                ErrorBoxStart.errorRun(e);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        productId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        company.setCellValueFactory(new PropertyValueFactory<>("company"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("availability"));
        table.setItems(products);
    }

    public void close() {
        Stage stage = (Stage) table.getScene().getWindow();
        stage.close();
    }

}
