package Client.ClientView.UserPanel.AdminPanel.Users;

import Client.Client;
import Client.ClientController.AdminController;
import Client.ClientView.AlertBox.ErrorBox.ErrorBoxStart;
import Client.ClientView.AlertBox.MessageBox.AlertBoxStart;
import Client.ClientView.RegisterAndLoginStage.Register.RegisterScene;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Account;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import LocalExceptions.ExceptionsLibrary;
public class Users implements Initializable {

    @FXML
    private TableView table;
    @FXML
    private Button remove;

    ObservableList<Account> accounts = null;
    TableColumn<Account,String> username = new TableColumn<>("Username");
    TableColumn<Account,String> role= new TableColumn<>("Role");
    TableColumn<Account,String> firstName= new TableColumn<>("First Name");
    TableColumn<Account,String> lastName= new TableColumn<>("Last Name");
    TableColumn<Account,String> email = new TableColumn<>("E-Mail");
    TableColumn<Account,String> phoneNumber = new TableColumn<>("Phone Number");
    TableColumn<Account,Double> credit = new TableColumn<>("Credit");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        username.setStyle("-fx-alignment: CENTER");
        role.setStyle("-fx-alignment: CENTER");
        firstName.setStyle("-fx-alignment: CENTER");
        lastName.setStyle("-fx-alignment: CENTER");
        email.setStyle("-fx-alignment: CENTER");
        phoneNumber.setStyle("-fx-alignment: CENTER");
        credit.setStyle("-fx-alignment: CENTER");
        table.getColumns().addAll(username,role,firstName,lastName,email,phoneNumber,credit);
        updateTable();
    }

    public void removeButtonClicked(){
        try {
            Account account = (Account) table.getSelectionModel().getSelectedItem();
            AdminController.deleteUser(account.getUsername());
            AlertBoxStart.messageRun("Message", "User deleted!");
            updateTable();
        } catch (ExceptionsLibrary.NoAccountException e) {
            ErrorBoxStart.errorRun(e);
        }
    }

    public void addAdminButtonClicked() throws IOException {
        RegisterScene.setAdmin(true);
        Stage stage = new Stage();
        File file = new File("src/main/java/Client/ClientView/RegisterAndLoginStage/Register/RegisterScene.fxml");
        URL url = file.toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.showAndWait();
        updateTable();
    }

    private void updateTable() {
        try {
            accounts = FXCollections.observableArrayList(AdminController.showAllUsers());
        } catch (ExceptionsLibrary.NoAccountException e) {
            ErrorBoxStart.errorRun(e);
        }
        String func = "Get Online Users List";
        Client.sendMessage(func);
        Object response = Client.receiveObject();
        ArrayList<Account> onlineAccounts = (ArrayList<Account>) response;
        username.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Account, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Account, String> p) {

                String formatted = p.getValue().getUsername();
                boolean isOnline = false;
                for (Account i : onlineAccounts){
                    if (i.getUsername().equals(formatted)){
                        isOnline = true;
                    }
                }
                if (isOnline){
                    formatted = formatted + " (Online)";
                }
                else {
                    formatted = formatted + " (Offline)";
                }
                return new SimpleStringProperty(formatted);
            }
        });
        //username.setCellValueFactory(new PropertyValueFactory<>("username"));
        role.setCellValueFactory(new PropertyValueFactory<>("role"));
        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        credit.setCellValueFactory(new PropertyValueFactory<>("credit"));
        table.setItems(accounts);
    }

    public void close() {
        Stage stage = (Stage) table.getScene().getWindow();
        stage.close();
    }

}
