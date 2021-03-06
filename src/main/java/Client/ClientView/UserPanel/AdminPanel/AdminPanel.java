package Client.ClientView.UserPanel.AdminPanel;

import Client.ClientController.AdminController;
import Client.ClientController.CustomerController;
import Client.ClientController.SellerController;
import Client.ClientView.HelpWindow.Help;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Admin;
import Client.ClientView.MainMenuStage.Main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminPanel implements Initializable {
    @FXML
    private Button back;
    @FXML
    private Label name;
    @FXML
    private Label username;
    @FXML
    private Label role;
    @FXML
    private Label email;
    @FXML
    private Label phoneNumber;
    @FXML
    private Label password;
    @FXML
    private Label balance;

    public void back() throws IOException {
        Stage stage = (Stage) back.getScene().getWindow();
        File file = new File("src/main/java/Client/ClientView/MainMenuStage/MainMenu.fxml");
        URL url = file.toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Admin admin = AdminController.getAdmin();
        name.setText("Welcome " + admin.getFirstName() + " " + admin.getLastName() + "!");
        username.setText(admin.getUsername());
        role.setText(admin.getRole());
        email.setText(admin.getEmail());
        phoneNumber.setText(admin.getPhoneNumber());
        password.setText(admin.getPassword());
        balance.setText("Your current balance is: " + admin.getCredit());
    }

    public void helpButtonClicked() throws IOException {
        Help.setMessage("You can manage users, products and categories,\nhandle requests and sales and edit your info.");
        Stage stage = new Stage();
        File file = new File("src/main/java/Client/ClientView/HelpWindow/Help.fxml");
        URL url = file.toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
    }

    public void logout() throws IOException {
        if (Main.checkLoggedIn() != null) {
            if (CustomerController.getCustomer() != null) {
                CustomerController.setCustomer(null);
            } else if (SellerController.getSeller() != null) {
                SellerController.setSeller(null);
            } else if (AdminController.getAdmin() != null) {
                AdminController.setAdmin(null);
            }
            Main.setStatus(null);
            back();
        }
    }

    private void updateScene() {
        Admin admin = AdminController.getAdmin();
        name.setText("Welcome " + admin.getFirstName() + " " + admin.getLastName() + "!");
        username.setText(admin.getUsername());
        role.setText(admin.getRole());
        email.setText(admin.getEmail());
        phoneNumber.setText(admin.getPhoneNumber());
        password.setText(admin.getPassword());
        balance.setText("Your current balance is: " + admin.getCredit());
    }

    public void editInfoButtonClicked() throws IOException {
        Stage stage = new Stage();
        File file = new File("src/main/java/Client/ClientView/UserPanel/EditInfo/EditInfo.fxml");
        URL url = file.toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.showAndWait();
        updateScene();
    }

    public void usersButtonClicked() throws IOException {
        Stage stage = new Stage();
        File file = new File("src/main/java/Client/ClientView/UserPanel/AdminPanel/Users/Users.fxml");
        URL url = file.toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.showAndWait();
    }

    public void salesButtonClicked() throws IOException {
        Stage stage = new Stage();
        File file = new File("src/main/java/Client/ClientView/UserPanel/AdminPanel/Sales/Sales.fxml");
        URL url = file.toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.showAndWait();
        updateScene();
    }

    public void requestsButtonClicked() throws IOException {
        Stage stage = new Stage();
        File file = new File("src/main/java/Client/ClientView/UserPanel/AdminPanel/Requests/Requests.fxml");
        URL url = file.toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.showAndWait();
        updateScene();
    }

    public void categoriesButtonClicked() throws IOException {
        Stage stage = new Stage();
        File file = new File("src/main/java/Client/ClientView/UserPanel/AdminPanel/Categories/Categories.fxml");
        URL url = file.toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.showAndWait();
        updateScene();
    }

    public void productsButtonClicked() throws IOException {
        Stage stage = new Stage();
        File file = new File("src/main/java/Client/ClientView/UserPanel/AdminPanel/Products/Products.fxml");
        URL url = file.toURI().toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.showAndWait();
        updateScene();
    }

}
