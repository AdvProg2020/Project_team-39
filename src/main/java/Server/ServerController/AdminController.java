package Server.ServerController;

//import Client.Client;
import Server.ClientHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.*;
import LocalExceptions.ExceptionsLibrary;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class AdminController {
    private static Admin admin;


    public AdminController(Admin admin) {
        this.admin = admin;
    }

    public static Admin getAdmin() {
        return admin;
    }


    public static void setAdmin(Admin admin) {
        AdminController.admin = admin;
    }

    public static void showAdminInfo() throws ExceptionsLibrary.NoAccountException {
        Gson gson = new GsonBuilder().serializeNulls().create();
        if (getAdmin() == null) {
            ClientHandler.sendObject(new ExceptionsLibrary.NoAccountException());
        }
        String username = ClientHandler.receiveMessage();
        Admin admin = (Admin) GetDataFromDatabaseServerSide.getAccount(username);
        setAdmin(admin);
        String data = gson.toJson(admin);
        ClientHandler.sendMessage(data);
    }

    public static void editAdminInfo() throws ExceptionsLibrary.NoFeatureWithThisName, ExceptionsLibrary.NoAccountException, ExceptionsLibrary.ChangeUsernameException {
        Object[] receivedItems = (Object[])ClientHandler.receiveObject();

        HashMap<String, String> dataToEdit = (HashMap<String, String>) receivedItems[1];
        Admin admin = (Admin) GetDataFromDatabaseServerSide.getAccount((String)receivedItems[0]);


        for (String i : dataToEdit.keySet()) {
            if (i.equals("username")){
                ClientHandler.sendObject(new ExceptionsLibrary.ChangeUsernameException());
            }
            try {
                Field field = Admin.class.getSuperclass().getDeclaredField(i);
                field.setAccessible(true);
                field.set(admin, dataToEdit.get(i));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                ClientHandler.sendObject(new ExceptionsLibrary.NoFeatureWithThisName());

            }
        }
        Gson gson = new GsonBuilder().serializeNulls().create();
        setAdmin(admin);
        SetDataToDatabase.setAccount(getAdmin());
        ClientHandler.sendMessage("Success!");
    }

    public static void showAdminRequests() throws ExceptionsLibrary.NoRequestException {
        ArrayList<Request> allRequests = new ArrayList<>();
        String path = "Resources/Requests";
        File file = new File(path);
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file1) {
                if (file1.getName().endsWith(".json")) {
                    return true;
                }
                return false;
            }
        };
        for (File i : file.listFiles(fileFilter)) {
            String fileName = i.getName();
            String requestId = fileName.replace(".json", "");
            Request request = GetDataFromDatabaseServerSide.getRequest(Integer.parseInt(requestId));
            if (request.getRequestCondition().equals(RequestOrCommentCondition.PENDING_TO_ACCEPT)) {
                allRequests.add(request);
            }
        }
        ClientHandler.sendObject(allRequests);
    }

    public static void showRequest() throws ExceptionsLibrary.NoRequestException {

        int requestId = Integer.parseInt(ClientHandler.receiveMessage());
        Request request = GetDataFromDatabaseServerSide.getRequest(requestId);
        if (request != null) {
            if (request.getRequestCondition().equals(RequestOrCommentCondition.PENDING_TO_ACCEPT)) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                String reuestData = gson.toJson(request);
                ClientHandler.sendMessage(reuestData);
            }
            else {
                ClientHandler.sendObject(new ExceptionsLibrary.NoRequestException());
            }
        } else {
            ClientHandler.sendObject(new ExceptionsLibrary.NoRequestException());
        }
    }

    public static void processRequest() throws ExceptionsLibrary.NoRequestException, ExceptionsLibrary.NoAccountException, ExceptionsLibrary.UsernameAlreadyExists, ExceptionsLibrary.NoProductException {
        Object[] receivedArray = (Object[]) ClientHandler.receiveObject();
        int requestId = (int) receivedArray[0];
        boolean acceptStatus = (boolean) receivedArray[1];
        Request request = GetDataFromDatabaseServerSide.getRequest(requestId);
        Gson gson = new GsonBuilder().serializeNulls().create();
        switch (request.getRequestType()) {
            case ADD_OFF:
                if (acceptStatus) {
                    Off off = gson.fromJson(request.getRequestDescription(), Off.class);
                    off.setOffCondition(ProductOrOffCondition.ACCEPTED);
                    while (checkIfOffExist(off.getOffId())) {
                        Random random = new Random();
                        off.setOffId(random.nextInt(1000000));
                    }
                    String offDetails = gson.toJson(off);
                    Seller seller = (Seller) GetDataFromDatabaseServerSide.getAccount(request.getRequestSeller());
                    seller.getSellerOffs().add(off);
                    try {
                        String offPath = "Resources/Offs/" + off.getOffId() + ".json";
                        File file = new File(offPath);
                        file.createNewFile();
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(offDetails);
                        fileWriter.close();
                        SetDataToDatabase.setAccount(seller);
                        for (String i :off.getOffProducts()){
                            Product temp = GetDataFromDatabaseServerSide.getProduct(Integer.parseInt(i));
                            temp.setPriceWithOff(temp.getPrice()-off.getOffAmount());
                            SetDataToDatabase.setProduct(temp);
                            SetDataToDatabase.updateSellerOfProduct(temp,0);
                        }
                        request.setRequestCondition(RequestOrCommentCondition.ACCEPTED);
                        SetDataToDatabase.setRequest(request);
                    } catch (IOException e) {
                        request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
                        SetDataToDatabase.setRequest(request);
                        ClientHandler.sendObject(new ExceptionsLibrary.NoAccountException());
                    }
                } else {
                    request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
                    SetDataToDatabase.setRequest(request);

                }
                break;
            case EDIT_OFF:
                if (acceptStatus) {
                    Off off = gson.fromJson(request.getRequestDescription(), Off.class);
                    off.setOffCondition(ProductOrOffCondition.ACCEPTED);
                    String offDetails = gson.toJson(off);
                    Seller seller = (Seller) GetDataFromDatabaseServerSide.getAccount(request.getRequestSeller());
                    Iterator iterator = seller.getSellerOffs().iterator();
                    while (iterator.hasNext()) {
                        Off tempOff = (Off) iterator.next();
                        if (tempOff.getOffId() == off.getOffId()) {
                            iterator.remove();
                        }
                    }
                    seller.getSellerOffs().add(off);
                    SetDataToDatabase.setAccount(seller);
                    for (String i :off.getOffProducts()){
                        Product temp = GetDataFromDatabaseServerSide.getProduct(Integer.parseInt(i));
                        temp.setPriceWithOff(temp.getPrice()-off.getOffAmount());
                        SetDataToDatabase.setProduct(temp);
                        SetDataToDatabase.updateSellerOfProduct(temp,0);
                    }
                    try {
                        String offPath = "Resources/Offs/" + off.getOffId() + ".json";
                        String sellerPath = "Resources/Accounts/Seller/" + seller.getUsername() + ".json";
                        FileWriter fileWriter = new FileWriter(offPath);
                        fileWriter.write(offDetails);
                        fileWriter.close();
                        request.setRequestCondition(RequestOrCommentCondition.ACCEPTED);
                        SetDataToDatabase.setRequest(request);
                    } catch (IOException e) {
                        request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
                        SetDataToDatabase.setRequest(request);
                        ClientHandler.sendObject(new ExceptionsLibrary.NoAccountException());
                    }
                } else {
                    request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
                    SetDataToDatabase.setRequest(request);
                }
                break;
            case ADD_PRODUCT:
                if (acceptStatus) {
                    Product product = gson.fromJson(request.getRequestDescription(), Product.class);
                    product.setProductCondition(ProductOrOffCondition.ACCEPTED);
                    while (checkIfProductExist(product.getProductId())) {
                        Random random = new Random();
                        product.setProductId(random.nextInt(1000000));
                    }
                    String productDetails = gson.toJson(product);
                    Seller seller = (Seller) GetDataFromDatabaseServerSide.getAccount(request.getRequestSeller());
                    seller.getSellerProducts().add(product);
                    try {
                        String productPath = "Resources/Products/" + product.getProductId() + ".json";
                        File file = new File(productPath);
                        file.createNewFile();
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(productDetails);
                        fileWriter.close();
                        SetDataToDatabase.setAccount(seller);
                        request.setRequestCondition(RequestOrCommentCondition.ACCEPTED);
                        SetDataToDatabase.setRequest(request);
                    } catch (IOException e) {
                        request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
                        SetDataToDatabase.setRequest(request);
                        ClientHandler.sendObject(new ExceptionsLibrary.NoAccountException());
                    }
                } else {
                    request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
                    SetDataToDatabase.setRequest(request);
                }
                break;
            case EDIT_PRODUCT:
                if (acceptStatus) {
                    Product product = gson.fromJson(request.getRequestDescription(), Product.class);
                    product.setProductCondition(ProductOrOffCondition.ACCEPTED);
                    String productDetails = gson.toJson(product);
                    Seller seller = (Seller) GetDataFromDatabaseServerSide.getAccount(request.getRequestSeller());
                    Iterator iterator = seller.getSellerProducts().iterator();
                    while (iterator.hasNext()) {
                        Product tempProduct = (Product) iterator.next();
                        if (tempProduct.getProductId() == product.getProductId()) {
                            iterator.remove();
                        }
                    }
                    seller.getSellerProducts().add(product);
                    try {
                        String productPath = "Resources/Products/" + product.getProductId() + ".json";
                        String sellerPath = "Resources/Accounts/Seller/" + seller.getUsername() + ".json";
                        FileWriter fileWriter = new FileWriter(productPath);
                        fileWriter.write(productDetails);
                        fileWriter.close();
                        SetDataToDatabase.setAccount(seller);
                        SetDataToDatabase.updateSellerOfProduct(product,0);
                        request.setRequestCondition(RequestOrCommentCondition.ACCEPTED);
                        SetDataToDatabase.setRequest(request);
                    } catch (IOException e) {
                        request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
                        SetDataToDatabase.setRequest(request);
                        ClientHandler.sendObject(new ExceptionsLibrary.NoAccountException());
                    }
                } else {
                    request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
                    SetDataToDatabase.setRequest(request);
                }
                break;
            case REGISTER_SELLER:
                if (acceptStatus) {
                    Seller seller = gson.fromJson(request.getRequestDescription(),Seller.class);
                    if (RegisterAndLogin.checkUsername(seller.getUsername())) {
                        try {
                            String sellerPath = "Resources/Accounts/Seller/" + seller.getUsername() + ".json";
                            File file = new File(sellerPath);
                            file.createNewFile();
                            FileWriter fileWriterSeller = new FileWriter(sellerPath);
                            Gson gsonSeller = new GsonBuilder().serializeNulls().create();
                            String sellerData = gsonSeller.toJson(seller);
                            fileWriterSeller.write(sellerData);
                            fileWriterSeller.close();
                            request.setRequestCondition(RequestOrCommentCondition.ACCEPTED);
                            SetDataToDatabase.setRequest(request);
                        } catch (IOException e) {
                            ClientHandler.sendObject(new ExceptionsLibrary.NoAccountException());
                        }
                    } else {
                        request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
                        SetDataToDatabase.setRequest(request);
                        ClientHandler.sendObject(new ExceptionsLibrary.UsernameAlreadyExists());
                    }
                } else {
                    request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
                    SetDataToDatabase.setRequest(request);
                }
                break;
            case REMOVE_PRODUCT:
                if (acceptStatus) {
                    Product product = gson.fromJson(request.getRequestDescription(),Product.class);
                    String path = "Resources/Products/" + product.getProductId() + ".json";
                    SetDataToDatabase.updateSellerOfProduct(product,1);
                    File file = new File(path);
                    file.delete();
                    request.setRequestCondition(RequestOrCommentCondition.ACCEPTED);
                    SetDataToDatabase.setRequest(request);
                } else {
                    request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
                    SetDataToDatabase.setRequest(request);
                }
                break;
        }
    }

    private static void checkIfOffExist() {
        int offId = Integer.parseInt(ClientHandler.receiveMessage());
        String path = "Resources/Offs/" + offId + ".json";
        File file = new File(path);
        if (!file.exists()) {
            ClientHandler.sendMessage("false");
        } else {
            ClientHandler.sendMessage("true");
        }
    }

    private static boolean checkIfOffExist(int offId) {
        String path = "Resources/Offs/" + offId + ".json";
        File file = new File(path);
        if (!file.exists()) {
            return false;
        } else {
            return true;
        }
    }

    public static void showSales() throws ExceptionsLibrary.NoSaleException {
        ArrayList<Sale> allSales = new ArrayList<>();
        String path = "Resources/Sales";
        File file = new File(path);
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.getName().endsWith(".json")) {
                    return true;
                }
                return false;
            }
        };
        for (File i : file.listFiles(fileFilter)) {
            String fileName = i.getName();
            String saleCode = fileName.replace(".json", "");
            Sale sale = GetDataFromDatabaseServerSide.getSale(saleCode);
            allSales.add(sale);
        }
        ClientHandler.sendObject(allSales);
    }

    public static void editSaleInfo() throws ExceptionsLibrary.NoSaleException, ExceptionsLibrary.NoFeatureWithThisName, ExceptionsLibrary.CannotChangeThisFeature {
        Object[] receivedData = (Object[]) ClientHandler.receiveObject();
        String saleCode = (String)receivedData[0];
        HashMap<String, String> dataToEdit = (HashMap)receivedData[0];
        Sale sale = GetDataFromDatabaseServerSide.getSale(saleCode);
        for (String i : dataToEdit.keySet()) {
            try {
                if (i.equalsIgnoreCase("saleId")){
                    ClientHandler.sendObject(new ExceptionsLibrary.CannotChangeThisFeature());
                }
                Field field = Sale.class.getDeclaredField(i);
                if (i.equals("salePercent")) {
                    field.setAccessible(true);
                    field.set(sale, Double.parseDouble(dataToEdit.get(i)));
                } else if (i.equals("saleMaxAmount")) {
                    field.setAccessible(true);
                    field.set(sale, Double.parseDouble(dataToEdit.get(i)));
                } else if (i.equals("validTimes")) {
                    field.setAccessible(true);
                    field.set(sale, Integer.parseInt(dataToEdit.get(i)));
                } else {
                    field.setAccessible(true);
                    field.set(sale, dataToEdit.get(i));
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                ClientHandler.sendObject(new ExceptionsLibrary.NoFeatureWithThisName());
            }
        }
        File customerFolder = new File("Resources/Accounts/Customer");
        File sellerFolder = new File("Resources/Accounts/Seller");
        File adminFolder = new File("Resources/Accounts/Admin");

        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file1) {
                if (file1.getName().endsWith(".json")) {
                    return true;
                }
                return false;
            }
        };

        for (File i : customerFolder.listFiles(fileFilter)) {
            Gson gson = new GsonBuilder().serializeNulls().create();
            try {
                String fileData = "";
                fileData = new String(Files.readAllBytes(Paths.get(i.getPath())));
                Customer customer = gson.fromJson(fileData, Customer.class);
                Iterator<Sale> iterator = customer.getSaleCodes().iterator();
                while (iterator.hasNext()) {
                    Sale tempSale = iterator.next();
                    if (tempSale.getSaleCode().equalsIgnoreCase(sale.getSaleCode())) {
                        iterator.remove();
                    }
                }
                customer.getSaleCodes().add(sale);
                SetDataToDatabase.setAccount(customer);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (File i : sellerFolder.listFiles(fileFilter)) {
            Gson gson = new GsonBuilder().serializeNulls().create();
            try {
                String fileData = "";
                fileData = new String(Files.readAllBytes(Paths.get(i.getPath())));
                Seller seller = gson.fromJson(fileData, Seller.class);

                Iterator<Sale> iterator = seller.getSaleCodes().iterator();
                while (iterator.hasNext()) {
                    Sale tempSale = iterator.next();
                    if (tempSale.getSaleCode().equalsIgnoreCase(sale.getSaleCode())) {
                        iterator.remove();
                    }
                }
                seller.getSaleCodes().add(sale);
                SetDataToDatabase.setAccount(seller);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (File i : adminFolder.listFiles(fileFilter)) {
            Gson gson = new GsonBuilder().serializeNulls().create();
            try {
                String fileData = "";
                fileData = new String(Files.readAllBytes(Paths.get(i.getPath())));
                Admin admin = gson.fromJson(fileData, Admin.class);
                Iterator<Sale> iterator = admin.getSaleCodes().iterator();
                while (iterator.hasNext()) {
                    Sale tempSale = iterator.next();
                    if (tempSale.getSaleCode().equalsIgnoreCase(sale.getSaleCode())) {
                        iterator.remove();
                    }
                }
                admin.getSaleCodes().add(sale);
                SetDataToDatabase.setAccount(admin);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Gson gson = new GsonBuilder().serializeNulls().create();
        String editedDetails = gson.toJson(sale);
        try {
            String path = "Resources/Sales/" + sale.getSaleCode() + ".json";
            FileWriter fileWriter = new FileWriter(path);
            fileWriter.write(editedDetails);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addSale() {
        Sale sale = (Sale) ClientHandler.receiveObject();
        while (checkSaleCode(sale.getSaleCode())) {
            sale.setSaleCode(Sale.getRandomSaleCode());
        }
        if (sale.getSaleAccounts().size() == 0){
            try {
                sale.getSaleAccounts().clear();
                sale.getSaleAccounts().addAll(AdminController.showAllUsersLocal());
            } catch (ExceptionsLibrary.NoAccountException e) {
                e.printStackTrace();
            }
        }
        for (Account i : sale.getSaleAccounts()) {
            if (i.getSaleCodes() == null) {
                i.setSaleCodes(new ArrayList<>());
            }
            i.getSaleCodes().add(sale);
            SetDataToDatabase.setAccount(i);
        }

        SetDataToDatabase.setSale(sale);
    }

    public static void addSale(Sale sale) {
        while (checkSaleCode(sale.getSaleCode())) {
            sale.setSaleCode(Sale.getRandomSaleCode());
        }
        if (sale.getSaleAccounts().size() == 0){
            try {
                sale.getSaleAccounts().clear();
                sale.getSaleAccounts().addAll(AdminController.showAllUsersLocal());
            } catch (ExceptionsLibrary.NoAccountException e) {
                e.printStackTrace();
            }
        }
        for (Account i : sale.getSaleAccounts()) {
            if (i.getSaleCodes() == null) {
                i.setSaleCodes(new ArrayList<>());
            }
            i.getSaleCodes().add(sale);
            SetDataToDatabase.setAccount(i);
        }

        SetDataToDatabase.setSale(sale);
    }

    public static void showAllUsers() throws ExceptionsLibrary.NoAccountException {
        String customerPath = "Resources/Accounts/Customer";
        String sellerPath = "Resources/Accounts/Seller";
        String adminPath = "Resources/Accounts/Admin";
        ArrayList<Account> list = new ArrayList<>();
        File customerFolder = new File(customerPath);
        File sellerFolder = new File(sellerPath);
        File adminFolder = new File(adminPath);
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.getName().endsWith(".json")) {
                    return true;
                }
                return false;
            }
        };
        for (File i : customerFolder.listFiles(fileFilter)) {
            String fileName = i.getName();
            String username = fileName.replace(".json", "");
            Account account = GetDataFromDatabaseServerSide.getAccount(username);
            list.add(account);
        }
        for (File i : sellerFolder.listFiles(fileFilter)) {
            String fileName = i.getName();
            String username = fileName.replace(".json", "");
            Account account = GetDataFromDatabaseServerSide.getAccount(username);
            list.add(account);
        }

        for (File i : adminFolder.listFiles(fileFilter)) {
            String fileName = i.getName();
            String username = fileName.replace(".json", "");
            Account account = GetDataFromDatabaseServerSide.getAccount(username);
            list.add(account);
        }
        ClientHandler.sendObject(list);
    }

    public static ArrayList<Account> showAllUsersLocal() throws ExceptionsLibrary.NoAccountException {
        String customerPath = "Resources/Accounts/Customer";
        String sellerPath = "Resources/Accounts/Seller";
        String adminPath = "Resources/Accounts/Admin";
        ArrayList<Account> list = new ArrayList<>();
        File customerFolder = new File(customerPath);
        File sellerFolder = new File(sellerPath);
        File adminFolder = new File(adminPath);
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.getName().endsWith(".json")) {
                    return true;
                }
                return false;
            }
        };
        for (File i : customerFolder.listFiles(fileFilter)) {
            String fileName = i.getName();
            String username = fileName.replace(".json", "");
            Account account = GetDataFromDatabaseServerSide.getAccount(username);
            list.add(account);
        }
        for (File i : sellerFolder.listFiles(fileFilter)) {
            String fileName = i.getName();
            String username = fileName.replace(".json", "");
            Account account = GetDataFromDatabaseServerSide.getAccount(username);
            list.add(account);
        }

        for (File i : adminFolder.listFiles(fileFilter)) {
            String fileName = i.getName();
            String username = fileName.replace(".json", "");
            Account account = GetDataFromDatabaseServerSide.getAccount(username);
            list.add(account);
        }
        return list;
    }

    public static void showAllCustomers() throws ExceptionsLibrary.NoAccountException {
        ArrayList<Account> list = new ArrayList<>();
        String customerPath = "Resources/Accounts/Customer";
        File customerFolder = new File(customerPath);
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.getName().endsWith(".json")) {
                    return true;
                }
                return false;
            }
        };
        for (File i : customerFolder.listFiles(fileFilter)) {
            String fileName = i.getName();
            String username = fileName.replace(".json", "");
            Account account = GetDataFromDatabaseServerSide.getAccount(username);
            list.add(account);
        }
        ClientHandler.sendObject(list);
    }

    public static void showUserDetails() throws ExceptionsLibrary.NoAccountException {
        String username = ClientHandler.receiveMessage();
        Account account = GetDataFromDatabaseServerSide.getAccount(username);
        Gson gson = new GsonBuilder().serializeNulls().create();
        ClientHandler.sendMessage(gson.toJson(account));
    }

    public static void deleteUser() throws ExceptionsLibrary.NoAccountException {
        String username = ClientHandler.receiveMessage();
        Account account = GetDataFromDatabaseServerSide.getAccount(username);
        String path = "Resources/Accounts/" + account.getRole() + "/" + account.getUsername() + ".json";
        File file = new File(path);
        file.delete();
        ClientHandler.sendMessage("Success!");
    }

    public static void addAdminAccount() throws ExceptionsLibrary.UsernameAlreadyExists {
        String newAdminDetails = ClientHandler.receiveMessage();
        System.out.println(newAdminDetails);
        Gson gson = new GsonBuilder().serializeNulls().create();
        Admin admin1 = gson.fromJson(newAdminDetails,Admin.class);
        if (RegisterAndLogin.checkUsername(admin1.getUsername())) {
            RegisterAndLogin.registerAdmin(newAdminDetails);
        } else {
            ClientHandler.sendObject(new ExceptionsLibrary.UsernameAlreadyExists());
            return;
        }
        ClientHandler.sendMessage("Success!");
    }

    public static void deleteProduct() throws ExceptionsLibrary.NoProductException, ExceptionsLibrary.NoAccountException {
        int productId = Integer.parseInt(ClientHandler.receiveMessage());
        Product product = GetDataFromDatabaseServerSide.getProduct(productId);
        String path = "Resources/Products/" + product.getProductId() + ".json";
        SetDataToDatabase.updateSellerOfProduct(product,1);
        File file = new File(path);
        file.delete();
    }

    public static void showCategories() throws ExceptionsLibrary.NoCategoryException {
        ArrayList<Category> allCategories = new ArrayList<>();
        String path = "Resources/Category";
        File file = new File(path);
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file1) {
                if (file1.getName().endsWith(".json")) {
                    return true;
                }
                return false;
            }
        };
        for (File i : file.listFiles(fileFilter)) {
            String fileName = i.getName();
            String categoryName = fileName.replace(".json", "");
            Category category = GetDataFromDatabaseServerSide.getCategory(categoryName);
            allCategories.add(category);
        }
        ClientHandler.sendObject(allCategories);
    }

    public static void deleteCategory() throws ExceptionsLibrary.NoCategoryException, ExceptionsLibrary.NoProductException {
        String categoryName = ClientHandler.receiveMessage();
        try {
            Category category = GetDataFromDatabaseServerSide.getCategory(categoryName);
            FileFilter fileFilter = new FileFilter() {
                @Override
                public boolean accept(File file1) {
                    if (file1.getName().endsWith(".json")) {
                        return true;
                    }
                    return false;
                }
            };
            File productsFolder = new File("Resources/Products");
            for (File i : productsFolder.listFiles(fileFilter)) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    String fileData = "";
                    fileData = new String(Files.readAllBytes(Paths.get(i.getPath())));
                    Product product = gson.fromJson(fileData, Product.class);
                    if (product.getCategory().getName().equals(categoryName)) {
                        SetDataToDatabase.updateSellerOfProduct(product,1);
                        i.delete();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (ExceptionsLibrary.NoAccountException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String path = "Resources/Category/" + category.getName() + ".json";
            File file = new File(path);
            file.delete();
        } catch (ExceptionsLibrary.NoCategoryException e) {
            ClientHandler.sendObject(new ExceptionsLibrary.NoCategoryException());
        }

    }

    public static void editCategory() throws ExceptionsLibrary.CategoryExistsWithThisName, ExceptionsLibrary.NoCategoryException, ExceptionsLibrary.NoFeatureWithThisName, ExceptionsLibrary.NoAccountException, ExceptionsLibrary.NoProductException {
        Object[] receivedData = (Object[]) ClientHandler.receiveObject();
        String categoryName = (String) receivedData[0];
        HashMap<String, String> dataToEdit = (HashMap<String, String>) receivedData[0];
        Category category = GetDataFromDatabaseServerSide.getCategory(categoryName);
        String oldName = category.getName();
        for (String i : dataToEdit.keySet()) {
            try {
                Field field = Category.class.getDeclaredField(i);
                if (i.equals("features")) {
                    field.setAccessible(true);
                    String[] splitFeatures = dataToEdit.get(i).split("\\s*,\\s*");
                    ArrayList<Feature> newFeatures = new ArrayList<>();
                    for (String j : splitFeatures) {
                        newFeatures.add(new Feature(j, null));
                    }
                    field.set(category, newFeatures);
                } else {
                    field.setAccessible(true);
                    field.set(category, dataToEdit.get(i));
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                ClientHandler.sendObject(new ExceptionsLibrary.NoFeatureWithThisName());
            }
        }
        Gson gson = new GsonBuilder().serializeNulls().create();
        String newName = category.getName();
        String editedDetails = gson.toJson(category);
        if (newName.equals(oldName)) {
            try {
                String path = "Resources/Category/" + category.getName() + ".json";
                FileWriter fileWriter = new FileWriter(path);
                fileWriter.write(editedDetails);
                fileWriter.close();
                File file = new File(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                String newPath = "Resources/Category/" + newName + ".json";
                String oldPath = "Resources/Category/" + oldName + ".json";
                File file = new File(newPath);
                if (file.exists()) {
                    ClientHandler.sendObject(new ExceptionsLibrary.CategoryExistsWithThisName());
                }
                file.createNewFile();
                FileWriter fileWriter = new FileWriter(newPath);
                fileWriter.write(editedDetails);
                fileWriter.close();
                File file1 = new File(oldPath);
                file1.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File folder = new File("Resources/Products");
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file1) {
                if (file1.getName().endsWith(".json")) {
                    return true;
                }
                return false;
            }
        };
        for (File i : folder.listFiles(fileFilter)) {
            Gson gson1 = new GsonBuilder().serializeNulls().create();
            try {
                String fileData = "";
                fileData = new String(Files.readAllBytes(Paths.get(i.getPath())));
                Product product = gson1.fromJson(fileData, Product.class);
                product.setCategory(category);
                SetDataToDatabase.setProduct(product);
                SetDataToDatabase.updateSellerOfProduct(product,0);
            } catch (ExceptionsLibrary.NoAccountException | IOException e) {
                ClientHandler.sendObject(e);
            }
        }
    }

    public static void addCategory() throws ExceptionsLibrary.CategoryExistsWithThisName {
        String categoryDetails = ClientHandler.receiveMessage();
        Gson gson = new GsonBuilder().serializeNulls().create();
        Category category = gson.fromJson(categoryDetails, Category.class);
        if (!checkCategoryName(category.getName())) {
            String newSaleDetails = gson.toJson(category);
            try {
                String path = "Resources/Category/" + category.getName() + ".json";
                File file = new File(path);
                file.createNewFile();
                FileWriter fileWriter = new FileWriter(path);
                fileWriter.write(newSaleDetails);
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ClientHandler.sendObject(new ExceptionsLibrary.CategoryExistsWithThisName());
        }
    }

    public static void viewSaleCodeDetails() throws ExceptionsLibrary.NoSaleException {
        String saleCode = ClientHandler.receiveMessage();
        Sale sale = GetDataFromDatabaseServerSide.getSale(saleCode);
        Gson gson = new GsonBuilder().serializeNulls().create();
        ClientHandler.sendMessage(gson.toJson(sale));
    }

    public static void removeSaleCode() throws ExceptionsLibrary.NoSaleException {
        String saleCode = ClientHandler.receiveMessage();
        try {
            Sale sale = GetDataFromDatabaseServerSide.getSale(saleCode);
            FileFilter fileFilter = new FileFilter() {
                @Override
                public boolean accept(File file1) {
                    if (file1.getName().endsWith(".json")) {
                        return true;
                    }
                    return false;
                }
            };
            File customerFolder = new File("Resources/Accounts/Customer");
            File sellerFolder = new File("Resources/Accounts/Seller");
            File adminFolder = new File("Resources/Accounts/Admin");

            for (File i : customerFolder.listFiles(fileFilter)) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    String fileData = "";
                    fileData = new String(Files.readAllBytes(Paths.get(i.getPath())));
                    Customer customer = gson.fromJson(fileData, Customer.class);
                    Iterator<Sale> iterator = customer.getSaleCodes().iterator();
                    while (iterator.hasNext()) {
                        Sale tempSale = iterator.next();
                        if (tempSale.getSaleCode().equalsIgnoreCase(sale.getSaleCode())) {
                            iterator.remove();
                        }
                    }
                    SetDataToDatabase.setAccount(customer);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            for (File i : sellerFolder.listFiles(fileFilter)) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    String fileData = "";
                    fileData = new String(Files.readAllBytes(Paths.get(i.getPath())));
                    Seller seller = gson.fromJson(fileData, Seller.class);
                    Iterator<Sale> iterator = seller.getSaleCodes().iterator();
                    while (iterator.hasNext()) {
                        Sale tempSale = iterator.next();
                        if (tempSale.getSaleCode().equalsIgnoreCase(sale.getSaleCode())) {
                            iterator.remove();
                        }
                    }
                    SetDataToDatabase.setAccount(seller);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            for (File i : adminFolder.listFiles(fileFilter)) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                try {
                    String fileData = "";
                    fileData = new String(Files.readAllBytes(Paths.get(i.getPath())));
                    Admin admin = gson.fromJson(fileData, Admin.class);
                    Iterator<Sale> iterator = admin.getSaleCodes().iterator();
                    while (iterator.hasNext()) {
                        Sale tempSale = iterator.next();
                        if (tempSale.getSaleCode().equalsIgnoreCase(sale.getSaleCode())) {
                            iterator.remove();
                        }
                    }
                    SetDataToDatabase.setAccount(admin);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String path = "Resources/Sales/" + sale.getSaleCode() + ".json";
            File file = new File(path);
            file.delete();
        } catch (ExceptionsLibrary.NoSaleException e) {
            ClientHandler.sendObject(new ExceptionsLibrary.NoSaleException());
        }

    }

    public static void checkCategoryName() {
        String categoryName = ClientHandler.receiveMessage();
        String path = "Resources/Category";
        File folder = new File(path);
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file1) {
                if (file1.getName().endsWith(".json")) {
                    return true;
                }
                return false;
            }
        };
        for (File i : folder.listFiles(fileFilter)) {
            String fileName = i.getName();
            String fileCategoryName = fileName.replace(".json", "");
            if (categoryName.equals(fileCategoryName)) {
                ClientHandler.sendMessage("true");
            }
        }
        ClientHandler.sendMessage("false");
    }

    public static boolean checkCategoryName(String categoryName) {
        String path = "Resources/Category";
        File folder = new File(path);
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file1) {
                if (file1.getName().endsWith(".json")) {
                    return true;
                }
                return false;
            }
        };
        for (File i : folder.listFiles(fileFilter)) {
            String fileName = i.getName();
            String fileCategoryName = fileName.replace(".json", "");
            if (categoryName.equals(fileCategoryName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkSaleCode(String saleCode) {
        String path = "Resources/Sales";
        File folder = new File(path);
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
            String fileName = i.getName();
            String fileSaleCode = fileName.replace(".json", "");
            if (saleCode.equals(fileSaleCode)) {
                return true;
            }
        }
        return false;
    }

    public static void checkSaleCode() {
        String saleCode = ClientHandler.receiveMessage();
        String path = "Resources/Sales";
        File folder = new File(path);
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
            String fileName = i.getName();
            String fileSaleCode = fileName.replace(".json", "");
            if (saleCode.equals(fileSaleCode)) {
                ClientHandler.sendObject(true);

            }
        }
        ClientHandler.sendObject(false);

    }


    public static boolean checkIfProductExist(int productId) {
        String path = "Resources/Products/" + productId + ".json";
        File file = new File(path);
        if (!file.exists()) {
            return false;
        } else {
            return true;
        }
    }

    public static void checkIfProductExist() {
        int productId = Integer.parseInt(ClientHandler.receiveMessage());
        String path = "Resources/Products/" + productId + ".json";
        File file = new File(path);
        if (!file.exists()) {
            ClientHandler.sendObject(false);
        } else {
            ClientHandler.sendObject(true);
        }
    }

    public static void checkIfRequestExist() {
        int requestId = Integer.parseInt(ClientHandler.receiveMessage());
        String path = "Resources/Requests/" + requestId + ".json";
        File file = new File(path);
        if (!file.exists()) {
            ClientHandler.sendObject(false);
        } else {
            ClientHandler.sendObject(true);
        }
    }

    public static void getAllProducts() throws ExceptionsLibrary.NoProductException {
        ArrayList<Product> allProducts = new ArrayList<>();
        String path = "Resources/Products";
        File folder = new File(path);
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file1) {
                if (file1.getName().endsWith(".json")) {
                    return true;
                }
                return false;
            }
        };
        for (File i : folder.listFiles(fileFilter)) {
            String fileName = i.getName();
            int productId = Integer.parseInt(fileName.replace(".json", ""));
            allProducts.add(GetDataFromDatabaseServerSide.getProduct(productId));
        }
        ClientHandler.sendObject(allProducts);
    }

}
