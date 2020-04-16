package controller;

import com.google.gson.Gson;
import model.Account;
import model.Admin;
import model.Customer;
import model.Seller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GetDataFromDatabase {
    public static Account getAccount(String username) {
        String pathCustomer = "src/main/resources/Accounts/Customer/" + username + ".json";
        String pathAdmin = "src/main/resources/Accounts/Admin/" + username + ".json";
        String pathSeller = "src/main/resources/Accounts/Seller/" + username + ".json";
        File fileCustomer = new File(pathCustomer);
        File fileAdmin = new File(pathAdmin);
        File fileSeller = new File(pathSeller);
        if (fileCustomer.exists()&&!fileAdmin.exists()&&!fileSeller.exists()){
            try {
                Scanner scanner;
                scanner = new Scanner(fileCustomer);
                scanner.useDelimiter("\\z");
                String fileData = scanner.next();
                Gson gson = new Gson();
                Customer account = gson.fromJson(fileData, Customer.class);
                scanner.close();
                return account;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
        else if (!fileCustomer.exists()&&fileAdmin.exists()&&!fileSeller.exists()){
            try {
                Scanner scanner;
                scanner = new Scanner(fileAdmin);
                scanner.useDelimiter("\\z");
                String fileData = scanner.next();
                Gson gson = new Gson();
                Admin account = gson.fromJson(fileData, Admin.class);
                scanner.close();
                return account;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if (!fileCustomer.exists()&&!fileAdmin.exists()&&fileSeller.exists()){
            try {
                Scanner scanner;
                scanner = new Scanner(fileSeller);
                scanner.useDelimiter("\\z");
                String fileData = scanner.next();
                Gson gson = new Gson();
                Seller account = gson.fromJson(fileData, Seller.class);
                scanner.close();
                return account;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if (!fileCustomer.exists()&&!fileAdmin.exists()&&!fileSeller.exists()){
            view.MessagesLibrary.errorLibrary(1);
            return null;
        }
        return null;
    }


}
