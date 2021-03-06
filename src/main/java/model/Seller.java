package model;



import model.Account;
import model.Off;
import model.Product;
import model.Sale;
import model.SellLog;


import java.util.ArrayList;

public class Seller extends Account implements java.io.Serializable {
    public static ArrayList<model.Seller> allSellers;
    private String companyName;
    private ArrayList<Product> sellerProducts;
    private ArrayList<SellLog> sellerLogs;
    private ArrayList<Off> sellerOffs;
    private Wallet wallet;

    public Seller(String username, String password, String role, String firstName, String lastName, String email, String phoneNumber, ArrayList<Sale> saleCodes, double credit, String companyName, ArrayList<Product> sellerProducts, ArrayList<SellLog> sellerLogs, ArrayList<Off> sellerOffs) {
        super(username, password, role, firstName, lastName, email, phoneNumber, saleCodes, credit);
        this.companyName = companyName;
        this.sellerProducts = new ArrayList<>();
        this.sellerLogs = new ArrayList<>();
        this.sellerOffs = new ArrayList<>();
        this.wallet = new Wallet(this.getUsername(), credit);
    }

    public Wallet getWallet() {
        return wallet;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public ArrayList<Product> getSellerProducts() {
        return sellerProducts;
    }

    public void setSellerProducts(ArrayList<Product> sellerProducts) {
        this.sellerProducts = sellerProducts;
    }

    public ArrayList<SellLog> getSellerLogs() {
        return sellerLogs;
    }

    public void setSellerLogs(ArrayList<SellLog> sellerLogs) {
        this.sellerLogs = sellerLogs;
    }

    public ArrayList<Off> getSellerOffs() {
        return sellerOffs;
    }

    public void setSellerOffs(ArrayList<Off> sellerOffs) {
        this.sellerOffs = sellerOffs;
    }
}