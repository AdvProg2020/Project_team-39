package Server.ServerController;

import Server.ClientHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.*;
import LocalExceptions.ExceptionsLibrary;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class GetDataFromDatabase {

    public static void setResources() {
        File folder9 = new File("Resources");
        folder9.mkdir();
        File folder = new File("Resources/Accounts");
        folder.mkdir();
        File folder1 = new File("Resources/Accounts/Customer");
        folder1.mkdir();
        File folder2 = new File("Resources/Accounts/Seller");
        folder2.mkdir();
        File folder3 = new File("Resources/Accounts/Admin");
        folder3.mkdir();
        File folder4 = new File("Resources/Products");
        folder4.mkdir();
        File folder5 = new File("Resources/Requests");
        folder5.mkdir();
        File folder6 = new File("Resources/Offs");
        folder6.mkdir();
        File folder7 = new File("Resources/Sales");
        folder7.mkdir();
        File folder8 = new File("Resources/Category");
        folder8.mkdir();
    }

    public static void getAccount() throws ExceptionsLibrary.NoAccountException {

        String username = ClientHandler.receiveMessage();

        String pathCustomer = "Resources/Accounts/Customer/" + username + ".json";
        String pathAdmin = "Resources/Accounts/Admin/" + username + ".json";
        String pathSeller = "Resources/Accounts/Seller/" + username + ".json";
        File fileCustomer = new File(pathCustomer);
        File fileAdmin = new File(pathAdmin);
        File fileSeller = new File(pathSeller);
        if (fileCustomer.exists() && !fileAdmin.exists() && !fileSeller.exists()) {
            try {
                String fileData = "";
                fileData = new String(Files.readAllBytes(Paths.get(pathCustomer)));
                Gson gson = new GsonBuilder().serializeNulls().create();
                Customer account = gson.fromJson(fileData, Customer.class);
                ClientHandler.sendObject(account);
            } catch (FileNotFoundException e) {
                ClientHandler.sendObject(new ExceptionsLibrary.NoAccountException());
                return;
            } catch (IOException e) {
                ClientHandler.sendObject(new ExceptionsLibrary.NoAccountException());
                return;
            }

        } else if (!fileCustomer.exists() && fileAdmin.exists() && !fileSeller.exists()) {
            try {
                String fileData = "";
                fileData = new String(Files.readAllBytes(Paths.get(pathAdmin)));
                Gson gson = new GsonBuilder().serializeNulls().create();
                Admin account = gson.fromJson(fileData, Admin.class);
                ClientHandler.sendObject(account);
            } catch (IOException e) {
                ClientHandler.sendObject(new ExceptionsLibrary.NoAccountException());
            }
        } else if (!fileCustomer.exists() && !fileAdmin.exists() && fileSeller.exists()) {
            try {
                String fileData = "";
                fileData = new String(Files.readAllBytes(Paths.get(pathSeller)));
                Gson gson = new GsonBuilder().serializeNulls().create();
                Seller account = gson.fromJson(fileData, Seller.class);
                ClientHandler.sendObject(account);
            } catch (FileNotFoundException e) {
                ClientHandler.sendObject(new ExceptionsLibrary.NoAccountException());
                return;
            } catch (IOException e) {
                ClientHandler.sendObject(new ExceptionsLibrary.NoAccountException());
                return;
            }
        } else if (!fileCustomer.exists() && !fileAdmin.exists() && !fileSeller.exists()) {
            ClientHandler.sendObject(new ExceptionsLibrary.NoAccountException());
        }

    }

    public static void getProduct() throws ExceptionsLibrary.NoProductException {

        int productId = (int) ClientHandler.receiveObject();

        String productPath = "Resources/Products/" + (productId) + ".json";
        File fileProduct = new File(productPath);
        try {
            String fileData = "";
            fileData = new String(Files.readAllBytes(Paths.get(productPath)));
            Gson gson = new GsonBuilder().serializeNulls().create();
            Product product = gson.fromJson(fileData, Product.class);
            ClientHandler.sendObject(product);
        } catch (FileNotFoundException e) {
            ClientHandler.sendObject(new ExceptionsLibrary.NoProductException());
        } catch (IOException e) {
            ClientHandler.sendObject(new ExceptionsLibrary.NoProductException());
        }

        //ClientHandler.sendMessage("Success!");
    }


    public static void getOff() throws ExceptionsLibrary.NoOffException {

        int offId = (int) ClientHandler.receiveObject();
        if (!Files.exists(Paths.get("Resources/Offs"))) {
            File folder = new File("Resources/Offs");
            folder.mkdir();
        }
        String offPath = "Resources/Offs/" + (offId) + ".json";
        File fileProduct = new File(offPath);
        try {
            String fileData = "";
            fileData = new String(Files.readAllBytes(Paths.get(offPath)));
            Gson gson = new GsonBuilder().serializeNulls().create();
            Off off = gson.fromJson(fileData, Off.class);
            ClientHandler.sendObject(off);
            return;
        } catch (FileNotFoundException e) {
            ClientHandler.sendObject(new ExceptionsLibrary.NoOffException());
            return;
        } catch (IOException e) {
            ClientHandler.sendObject(new ExceptionsLibrary.NoOffException());
            return;
        }
    }

    public static void getRequest() throws ExceptionsLibrary.NoRequestException {

        int requestId = (int) ClientHandler.receiveObject();

        if (!Files.exists(Paths.get("Resources/Requests"))) {
            File folder = new File("Resources/Requests");
            folder.mkdir();
        }
        String requestPath = "Resources/Requests/" + (requestId) + ".json";
        File fileProduct = new File(requestPath);
        try {
            String fileData = "";
            fileData = new String(Files.readAllBytes(Paths.get(requestPath)));
            Gson gson = new GsonBuilder().serializeNulls().create();
            Request request = gson.fromJson(fileData, Request.class);
            ClientHandler.sendObject(request);
            return;
        } catch (FileNotFoundException e) {
            ClientHandler.sendObject(new ExceptionsLibrary.NoRequestException());
            return;
        } catch (IOException e) {
            ClientHandler.sendObject(new ExceptionsLibrary.NoRequestException());
            return;
        }
    }

    public static void getSale() throws ExceptionsLibrary.NoSaleException {

        String saleCode = ClientHandler.receiveMessage();

        if (!Files.exists(Paths.get("Resources/Sales"))) {
            File folder = new File("Resources/Sales");
            folder.mkdir();
        }
        String salePath = "Resources/Sales/" + (saleCode) + ".json";
        File fileProduct = new File(salePath);
        try {
            String fileData = "";
            fileData = new String(Files.readAllBytes(Paths.get(salePath)));
            Gson gson = new GsonBuilder().serializeNulls().create();
            Sale sale = gson.fromJson(fileData, Sale.class);
            ClientHandler.sendObject(sale);
        } catch (IOException e) {
            ClientHandler.sendObject(new ExceptionsLibrary.NoSaleException());
        }
    }

    public static void getCategory() throws ExceptionsLibrary.NoCategoryException {

        String categoryName = ClientHandler.receiveMessage();

        if (!Files.exists(Paths.get("Resources/Category"))) {
            File folder = new File("Resources/Category");
            folder.mkdir();
        }
        String categoryPath = "Resources/Category/" + (categoryName) + ".json";
        File fileCategory = new File(categoryPath);
        try {
            String fileData = "";
            fileData = new String(Files.readAllBytes(Paths.get(categoryPath)));
            Gson gson = new GsonBuilder().serializeNulls().create();
            Category category = gson.fromJson(fileData, Category.class);
            ClientHandler.sendObject(category);
            return;
        } catch (IOException e) {
            ClientHandler.sendObject(new ExceptionsLibrary.NoCategoryException());
            return;
        }
    }

    public static void checkIfAnyAdminExists() {
        ClientHandler.sendObject(new File("Resources/Accounts/Admin").listFiles().length == 0);
    }

    public static void findSellersFromProductId() throws ExceptionsLibrary.NoProductException {

        int productId = Integer.parseInt((String) ClientHandler.receiveObject());

        ProductPageController.setProduct(GetDataFromDatabaseServerSide.getProduct(productId));

        File folder = new File("Resources/Accounts/Seller");
        ArrayList<Seller> sellers =new ArrayList<>();
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.getName().endsWith(".json")) {
                    return true;
                }
                return false;
            }
        };
        for (File i : folder.listFiles(fileFilter)) {
            try {
                String fileData = "";
                fileData = new String(Files.readAllBytes(Paths.get(i.getPath())));
                Gson gson = new GsonBuilder().serializeNulls().create();
                Seller seller = gson.fromJson(fileData, Seller.class);
                for (Product j : seller.getSellerProducts()){
                    if (j.getProductId() == productId){
                        sellers.add(seller);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ClientHandler.sendObject(sellers);
    }
}
