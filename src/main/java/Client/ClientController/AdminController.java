package Client.ClientController;

import Client.Client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.*;

import java.util.*;

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

    public static String showAdminInfo() throws ExceptionsLibrary.NoAccountException {
//        Gson gson = new GsonBuilder().serializeNulls().create();
        if (getAdmin() == null) {
            throw new ExceptionsLibrary.NoAccountException();
        }

        String func = "Show Admin Info";
        Client.sendMessage(func);

        String adminUsername = getAdmin().getUsername();
        Client.sendMessage(adminUsername);

        Object data = Client.receiveObject();

        if(data instanceof Exception){
            throw new ExceptionsLibrary.NoAccountException();
        }
        else{
            return String.valueOf(data);
        }

//        Admin admin = (Admin) GetDataFromDatabase.getAccount(getAdmin().getUsername());
//        setAdmin(admin);
//        String data = gson.toJson(admin);
    }

    public static void editAdminInfo(HashMap<String, String> dataToEdit) throws ExceptionsLibrary.NoFeatureWithThisName, ExceptionsLibrary.NoAccountException, ExceptionsLibrary.ChangeUsernameException {
//        Admin admin = (Admin) GetDataFromDatabase.getAccount(getAdmin().getUsername());
        for (String i : dataToEdit.keySet()) {
            if (i.equals("username")) {
                throw new ExceptionsLibrary.ChangeUsernameException();
            }
            String func = "Edit Admin Info";
            Client.sendMessage(func);

            Object[] toSend = new Object[2];
            toSend[0] = getAdmin().getUsername();
            toSend[1] = dataToEdit;
            Client.sendObject(toSend);
            Object response = Client.receiveObject();

            if(response instanceof ExceptionsLibrary.NoFeatureWithThisName)
                throw new ExceptionsLibrary.NoFeatureWithThisName();
            if (response instanceof ExceptionsLibrary.NoAccountException)
                throw new ExceptionsLibrary.NoAccountException();
            else
                return;
//                Field field = Admin.class.getSuperclass().getDeclaredField(i);
//                field.setAccessible(true);
//                field.set(admin, dataToEdit.get(i));
//            } catch (NoSuchFieldException | IllegalAccessException e) {
//                throw new ExceptionsLibrary.NoFeatureWithThisName();
//            }

//            Gson gson = new GsonBuilder().serializeNulls().create();
//            setAdmin(admin);
//            SetDataToDatabase.setAccount(getAdmin());
        }
    }
    public static ArrayList<Request> showAdminRequests() throws ExceptionsLibrary.NoRequestException {
        ArrayList<Request> allRequests = new ArrayList<>();

        String func = "Show Admin Requests";
        Client.sendMessage(func);

        Object response = Client.receiveObject();

        if(response instanceof ExceptionsLibrary.NoRequestException)
            throw new ExceptionsLibrary.NoRequestException();
        else{
            allRequests = (ArrayList<Request>) response;
        }
//        String path = "Resources/Requests";
//        File file = new File(path);
//        FileFilter fileFilter = new FileFilter() {
//            @Override
//            public boolean accept(File file1) {
//                if (file1.getName().endsWith(".json")) {
//                    return true;
//                }
//                return false;
//            }
//        };
//        for (File i : file.listFiles(fileFilter)) {
//            String fileName = i.getName();
//            String requestId = fileName.replace(".json", "");
//            Request request = GetDataFromDatabase.getRequest(Integer.parseInt(requestId));
//            if (request.getRequestCondition().equals(RequestOrCommentCondition.PENDING_TO_ACCEPT)) {
//                allRequests.add(request);
//            }
//        }
        return allRequests;
    }

    public static String showRequest(int requestId) throws ExceptionsLibrary.NoRequestException {

//        Request request = GetDataFromDatabase.getRequest(requestId);

        String func = "Show Request";
        Client.sendMessage(func);

        Client.sendMessage(String.valueOf(requestId));
        Request request = (Request) Client.receiveObject();

        if (request != null) {
            if (request.getRequestCondition().equals(RequestOrCommentCondition.PENDING_TO_ACCEPT)) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                String requestData = gson.toJson(request);
                return requestData;
            }
            else {
                throw new ExceptionsLibrary.NoRequestException();
            }
        } else {
            throw new ExceptionsLibrary.NoRequestException();
        }
    }

    public static void processRequest(int requestId, boolean acceptStatus) throws ExceptionsLibrary.NoRequestException, ExceptionsLibrary.NoAccountException, ExceptionsLibrary.UsernameAlreadyExists, ExceptionsLibrary.NoProductException {

        Object[] toSend = new Object[2];
        toSend[0] = requestId;
        toSend[1] = acceptStatus;
        Client.sendObject(toSend);
        Object response = Client.receiveObject();

        if(response instanceof ExceptionsLibrary.NoRequestException)
            throw new ExceptionsLibrary.NoRequestException();
        if(response instanceof ExceptionsLibrary.NoAccountException)
            throw new ExceptionsLibrary.NoAccountException();
        if(response instanceof ExceptionsLibrary.UsernameAlreadyExists)
            throw new ExceptionsLibrary.UsernameAlreadyExists();
        if(response instanceof ExceptionsLibrary.NoProductException)
            throw new ExceptionsLibrary.NoProductException();
//        Request request = GetDataFromDatabase.getRequest(requestId);
//        Gson gson = new GsonBuilder().serializeNulls().create();
//        switch (request.getRequestType()) {
//            case ADD_OFF:
//                if (acceptStatus) {
//                    Off off = gson.fromJson(request.getRequestDescription(), Off.class);
//                    off.setOffCondition(ProductOrOffCondition.ACCEPTED);
//                    while (checkIfOffExist(off.getOffId())) {
//                        Random random = new Random();
//                        off.setOffId(random.nextInt(1000000));
//                    }
//                    String offDetails = gson.toJson(off);
//                    Seller seller = (Seller) GetDataFromDatabase.getAccount(request.getRequestSeller());
//                    seller.getSellerOffs().add(off);
//                    try {
//                        String offPath = "Resources/Offs/" + off.getOffId() + ".json";
//                        File file = new File(offPath);
//                        file.createNewFile();
//                        FileWriter fileWriter = new FileWriter(file);
//                        fileWriter.write(offDetails);
//                        fileWriter.close();
//                        SetDataToDatabase.setAccount(seller);
//                        for (String i :off.getOffProducts()){
//                            Product temp = GetDataFromDatabase.getProduct(Integer.parseInt(i));
//                            temp.setPriceWithOff(temp.getPrice()-off.getOffAmount());
//                            SetDataToDatabase.setProduct(temp);
//                            SetDataToDatabase.updateSellerOfProduct(temp,0);
//                        }
//                        request.setRequestCondition(RequestOrCommentCondition.ACCEPTED);
//                        SetDataToDatabase.setRequest(request);
//                    } catch (IOException e) {
//                        request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
//                        SetDataToDatabase.setRequest(request);
//                        throw new ExceptionsLibrary.NoAccountException();
//                    }
//                } else {
//                    request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
//                    SetDataToDatabase.setRequest(request);
//
//                }
//                break;
//            case EDIT_OFF:
//                if (acceptStatus) {
//                    Off off = gson.fromJson(request.getRequestDescription(), Off.class);
//                    off.setOffCondition(ProductOrOffCondition.ACCEPTED);
//                    String offDetails = gson.toJson(off);
//                    Seller seller = (Seller) GetDataFromDatabase.getAccount(request.getRequestSeller());
//                    Iterator iterator = seller.getSellerOffs().iterator();
//                    while (iterator.hasNext()) {
//                        Off tempOff = (Off) iterator.next();
//                        if (tempOff.getOffId() == off.getOffId()) {
//                            iterator.remove();
//                        }
//                    }
//                    seller.getSellerOffs().add(off);
//                    SetDataToDatabase.setAccount(seller);
//                    for (String i :off.getOffProducts()){
//                        Product temp = GetDataFromDatabase.getProduct(Integer.parseInt(i));
//                        temp.setPriceWithOff(temp.getPrice()-off.getOffAmount());
//                        SetDataToDatabase.setProduct(temp);
//                        SetDataToDatabase.updateSellerOfProduct(temp,0);
//                    }
//                    try {
//                        String offPath = "Resources/Offs/" + off.getOffId() + ".json";
//                        String sellerPath = "Resources/Accounts/Seller/" + seller.getUsername() + ".json";
//                        FileWriter fileWriter = new FileWriter(offPath);
//                        fileWriter.write(offDetails);
//                        fileWriter.close();
//                        request.setRequestCondition(RequestOrCommentCondition.ACCEPTED);
//                        SetDataToDatabase.setRequest(request);
//                    } catch (IOException e) {
//                        request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
//                        SetDataToDatabase.setRequest(request);
//                        throw new ExceptionsLibrary.NoAccountException();
//                    }
//                } else {
//                    request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
//                    SetDataToDatabase.setRequest(request);
//                }
//                break;
//            case ADD_PRODUCT:
//                if (acceptStatus) {
//                    Product product = gson.fromJson(request.getRequestDescription(), Product.class);
//                    product.setProductCondition(ProductOrOffCondition.ACCEPTED);
//                    while (checkIfProductExist(product.getProductId())) {
//                        Random random = new Random();
//                        product.setProductId(random.nextInt(1000000));
//                    }
//                    String productDetails = gson.toJson(product);
//                    Seller seller = (Seller) GetDataFromDatabase.getAccount(request.getRequestSeller());
//                    seller.getSellerProducts().add(product);
//                    try {
//                        String productPath = "Resources/Products/" + product.getProductId() + ".json";
//                        File file = new File(productPath);
//                        file.createNewFile();
//                        FileWriter fileWriter = new FileWriter(file);
//                        fileWriter.write(productDetails);
//                        fileWriter.close();
//                        SetDataToDatabase.setAccount(seller);
//                        request.setRequestCondition(RequestOrCommentCondition.ACCEPTED);
//                        SetDataToDatabase.setRequest(request);
//                    } catch (IOException e) {
//                        request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
//                        SetDataToDatabase.setRequest(request);
//                        throw new ExceptionsLibrary.NoAccountException();
//                    }
//                } else {
//                    request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
//                    SetDataToDatabase.setRequest(request);
//                }
//                break;
//            case EDIT_PRODUCT:
//                if (acceptStatus) {
//                    Product product = gson.fromJson(request.getRequestDescription(), Product.class);
//                    product.setProductCondition(ProductOrOffCondition.ACCEPTED);
//                    String productDetails = gson.toJson(product);
//                    Seller seller = (Seller) GetDataFromDatabase.getAccount(request.getRequestSeller());
//                    Iterator iterator = seller.getSellerProducts().iterator();
//                    while (iterator.hasNext()) {
//                        Product tempProduct = (Product) iterator.next();
//                        if (tempProduct.getProductId() == product.getProductId()) {
//                            iterator.remove();
//                        }
//                    }
//                    seller.getSellerProducts().add(product);
//                    try {
//                        String productPath = "Resources/Products/" + product.getProductId() + ".json";
//                        String sellerPath = "Resources/Accounts/Seller/" + seller.getUsername() + ".json";
//                        FileWriter fileWriter = new FileWriter(productPath);
//                        fileWriter.write(productDetails);
//                        fileWriter.close();
//                        SetDataToDatabase.setAccount(seller);
//                        SetDataToDatabase.updateSellerOfProduct(product,0);
//                        request.setRequestCondition(RequestOrCommentCondition.ACCEPTED);
//                        SetDataToDatabase.setRequest(request);
//                    } catch (IOException e) {
//                        request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
//                        SetDataToDatabase.setRequest(request);
//                        throw new ExceptionsLibrary.NoAccountException();
//                    }
//                } else {
//                    request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
//                    SetDataToDatabase.setRequest(request);
//                }
//                break;
//            case REGISTER_SELLER:
//                if (acceptStatus) {
//                    Seller seller = gson.fromJson(request.getRequestDescription(),Seller.class);
//                    if (RegisterAndLogin.checkUsername(seller.getUsername())) {
//                        try {
//                            String sellerPath = "Resources/Accounts/Seller/" + seller.getUsername() + ".json";
//                            File file = new File(sellerPath);
//                            file.createNewFile();
//                            FileWriter fileWriterSeller = new FileWriter(sellerPath);
//                            Gson gsonSeller = new GsonBuilder().serializeNulls().create();
//                            String sellerData = gsonSeller.toJson(seller);
//                            fileWriterSeller.write(sellerData);
//                            fileWriterSeller.close();
//                            request.setRequestCondition(RequestOrCommentCondition.ACCEPTED);
//                            SetDataToDatabase.setRequest(request);
//                        } catch (IOException e) {
//                            throw new ExceptionsLibrary.NoAccountException();
//                        }
//                    } else {
//                        request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
//                        SetDataToDatabase.setRequest(request);
//                        throw new ExceptionsLibrary.UsernameAlreadyExists();
//                    }
//                } else {
//                    request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
//                    SetDataToDatabase.setRequest(request);
//                }
//                break;
//            case REMOVE_PRODUCT:
//                if (acceptStatus) {
//                    Product product = gson.fromJson(request.getRequestDescription(),Product.class);
//                    String path = "Resources/Products/" + product.getProductId() + ".json";
//                    SetDataToDatabase.updateSellerOfProduct(product,1);
//                    File file = new File(path);
//                    file.delete();
//                    request.setRequestCondition(RequestOrCommentCondition.ACCEPTED);
//                    SetDataToDatabase.setRequest(request);
//                } else {
//                    request.setRequestCondition(RequestOrCommentCondition.NOT_ACCEPTED);
//                    SetDataToDatabase.setRequest(request);
//                }
//                break;
//        }
    }

    private static boolean checkIfOffExist(int offId) {

        Client.sendMessage("check if off exists");
        String toSend = String.valueOf(offId);
        Client.sendObject(toSend);
        String response = Client.receiveMessage();
        if(response.equals("true"))
            return true;
        return false;
//        String path = "Resources/Offs/" + offId + ".json";
//        File file = new File(path);
//        if (!file.exists()) {
//            return false;
//        } else {
//            return true;
//        }
    }

    public static ArrayList<Sale> showSales() throws ExceptionsLibrary.NoSaleException {
        ArrayList<Sale> allSales = new ArrayList<>();
        Client.sendMessage("Show Sales");
        Object response = Client.receiveObject();

        if(response instanceof ExceptionsLibrary.NoSaleException)
            throw new ExceptionsLibrary.NoSaleException();
        else
            allSales = (ArrayList<Sale>) response;
//        String path = "Resources/Sales";
//        File file = new File(path);
//        FileFilter fileFilter = new FileFilter() {
//            @Override
//            public boolean accept(File file) {
//                if (file.getName().endsWith(".json")) {
//                    return true;
//                }
//                return false;
//            }
//        };
//        for (File i : file.listFiles(fileFilter)) {
//            String fileName = i.getName();
//            String saleCode = fileName.replace(".json", "");
//            Sale sale = GetDataFromDatabase.getSale(saleCode);
//            allSales.add(sale);
//        }
        return allSales;
    }

    public static void editSaleInfo(String saleCode, HashMap<String, String> dataToEdit) throws ExceptionsLibrary.NoSaleException, ExceptionsLibrary.NoFeatureWithThisName, ExceptionsLibrary.CannotChangeThisFeature {

        Client.sendMessage("Edit Sale Info");

        Object[] toSend = new Object[2];
        toSend[0] = String.valueOf(saleCode);
        toSend[1] = dataToEdit;
        Client.sendObject(toSend);
        Object response = Client.receiveObject();

        if(response instanceof ExceptionsLibrary.NoSaleException)
            throw new ExceptionsLibrary.NoSaleException();
        if(response instanceof ExceptionsLibrary.NoFeatureWithThisName)
            throw new ExceptionsLibrary.NoFeatureWithThisName();
        if (response instanceof ExceptionsLibrary.CannotChangeThisFeature)
            throw new ExceptionsLibrary.CannotChangeThisFeature();

//                Sale sale = GetDataFromDatabase.getSale(saleCode);
//        for (String i : dataToEdit.keySet()) {
//            try {
//                if (i.equalsIgnoreCase("saleId")){
//                    throw new ExceptionsLibrary.CannotChangeThisFeature();
//                }
//                Field field = Sale.class.getDeclaredField(i);
//                if (i.equals("salePercent")) {
//                    field.setAccessible(true);
//                    field.set(sale, Double.parseDouble(dataToEdit.get(i)));
//                } else if (i.equals("saleMaxAmount")) {
//                    field.setAccessible(true);
//                    field.set(sale, Double.parseDouble(dataToEdit.get(i)));
//                } else if (i.equals("validTimes")) {
//                    field.setAccessible(true);
//                    field.set(sale, Integer.parseInt(dataToEdit.get(i)));
//                } else {
//                    field.setAccessible(true);
//                    field.set(sale, dataToEdit.get(i));
//                }
//            } catch (NoSuchFieldException | IllegalAccessException e) {
//                throw new ExceptionsLibrary.NoFeatureWithThisName();
//            }
//        }
//        File customerFolder = new File("Resources/Accounts/Customer");
//        File sellerFolder = new File("Resources/Accounts/Seller");
//        File adminFolder = new File("Resources/Accounts/Admin");
//
//        FileFilter fileFilter = new FileFilter() {
//            @Override
//            public boolean accept(File file1) {
//                if (file1.getName().endsWith(".json")) {
//                    return true;
//                }
//                return false;
//            }
//        };
//
//        for (File i : customerFolder.listFiles(fileFilter)) {
//            Gson gson = new GsonBuilder().serializeNulls().create();
//            try {
//                String fileData = "";
//                fileData = new String(Files.readAllBytes(Paths.get(i.getPath())));
//                Customer customer = gson.fromJson(fileData, Customer.class);
//                Iterator<Sale> iterator = customer.getSaleCodes().iterator();
//                while (iterator.hasNext()) {
//                    Sale tempSale = iterator.next();
//                    if (tempSale.getSaleCode().equalsIgnoreCase(sale.getSaleCode())) {
//                        iterator.remove();
//                    }
//                }
//                customer.getSaleCodes().add(sale);
//                SetDataToDatabase.setAccount(customer);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        for (File i : sellerFolder.listFiles(fileFilter)) {
//            Gson gson = new GsonBuilder().serializeNulls().create();
//            try {
//                String fileData = "";
//                fileData = new String(Files.readAllBytes(Paths.get(i.getPath())));
//                Seller seller = gson.fromJson(fileData, Seller.class);
//
//                Iterator<Sale> iterator = seller.getSaleCodes().iterator();
//                while (iterator.hasNext()) {
//                    Sale tempSale = iterator.next();
//                    if (tempSale.getSaleCode().equalsIgnoreCase(sale.getSaleCode())) {
//                        iterator.remove();
//                    }
//                }
//                seller.getSaleCodes().add(sale);
//                SetDataToDatabase.setAccount(seller);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        for (File i : adminFolder.listFiles(fileFilter)) {
//            Gson gson = new GsonBuilder().serializeNulls().create();
//            try {
//                String fileData = "";
//                fileData = new String(Files.readAllBytes(Paths.get(i.getPath())));
//                Admin admin = gson.fromJson(fileData, Admin.class);
//                Iterator<Sale> iterator = admin.getSaleCodes().iterator();
//                while (iterator.hasNext()) {
//                    Sale tempSale = iterator.next();
//                    if (tempSale.getSaleCode().equalsIgnoreCase(sale.getSaleCode())) {
//                        iterator.remove();
//                    }
//                }
//                admin.getSaleCodes().add(sale);
//                SetDataToDatabase.setAccount(admin);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        Gson gson = new GsonBuilder().serializeNulls().create();
//        String editedDetails = gson.toJson(sale);
//        try {
//            String path = "Resources/Sales/" + sale.getSaleCode() + ".json";
//            FileWriter fileWriter = new FileWriter(path);
//            fileWriter.write(editedDetails);
//            fileWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static void addSale(Sale sale) {
        while (checkSaleCode(sale.getSaleCode())) {
            sale.setSaleCode(Sale.getRandomSaleCode());
        }

        Client.sendMessage("Add Sale");
        //Client.receiveMessage();

        Client.sendObject(sale);
        String response = Client.receiveMessage();

        if(response.equals("No Problem"))
            return;
        else if(response.equals("Problem")){
            try {
                throw new ExceptionsLibrary.NoAccountException();
            } catch (ExceptionsLibrary.NoAccountException e) {
                e.printStackTrace();
            }
        }
//        if (sale.getSaleAccounts().size() == 0){
//            try {
//                sale.getSaleAccounts().clear();
//                sale.getSaleAccounts().addAll(controller.AdminController.showAllUsers());
//            } catch (ExceptionsLibrary.NoAccountException e) {
//                e.printStackTrace();
//            }
//        }
//        for (Account i : sale.getSaleAccounts()) {
//            if (i.getSaleCodes() == null) {
//                i.setSaleCodes(new ArrayList<>());
//            }
//            i.getSaleCodes().add(sale);
//            SetDataToDatabase.setAccount(i);
//        }
//
//        SetDataToDatabase.setSale(sale);
    }

    public static ArrayList<Account> showAllUsers() throws ExceptionsLibrary.NoAccountException {

        ArrayList<Account> list = new ArrayList<Account>();
        String func = "Show All Users";
        Client.sendMessage(func);

        Object response = Client.receiveObject();

        if(response instanceof ExceptionsLibrary.NoAccountException)
            throw new ExceptionsLibrary.NoAccountException();
        else
            list = (ArrayList<Account>) response;
//        String customerPath = "Resources/Accounts/Customer";
//        String sellerPath = "Resources/Accounts/Seller";
//        String adminPath = "Resources/Accounts/Admin";
//        File customerFolder = new File(customerPath);
//        File sellerFolder = new File(sellerPath);
//        File adminFolder = new File(adminPath);
//        FileFilter fileFilter = new FileFilter() {
//            @Override
//            public boolean accept(File file) {
//                if (file.getName().endsWith(".json")) {
//                    return true;
//                }
//                return false;
//            }
//        };
//        for (File i : customerFolder.listFiles(fileFilter)) {
//            String fileName = i.getName();
//            String username = fileName.replace(".json", "");
//            Account account = GetDataFromDatabase.getAccount(username);
//            list.add(account);
//        }
//        for (File i : sellerFolder.listFiles(fileFilter)) {
//            String fileName = i.getName();
//            String username = fileName.replace(".json", "");
//            Account account = GetDataFromDatabase.getAccount(username);
//            list.add(account);
//        }
//
//        for (File i : adminFolder.listFiles(fileFilter)) {
//            String fileName = i.getName();
//            String username = fileName.replace(".json", "");
//            Account account = GetDataFromDatabase.getAccount(username);
//            list.add(account);
//        }
        return list;
    }

    public static ArrayList<Account> showAllCustomers() throws ExceptionsLibrary.NoAccountException {

        ArrayList<Account> list = new ArrayList<Account>();
        String func = "Show All Customers";
        Client.sendMessage(func);

        Object response = Client.receiveObject();

        if(response instanceof ExceptionsLibrary.NoAccountException)
            throw new ExceptionsLibrary.NoAccountException();
        else
            list = (ArrayList<Account>) response;

//        String customerPath = "Resources/Accounts/Customer";
//        File customerFolder = new File(customerPath);
//        FileFilter fileFilter = new FileFilter() {
//            @Override
//            public boolean accept(File file) {
//                if (file.getName().endsWith(".json")) {
//                    return true;
//                }
//                return false;
//            }
//        };
//        for (File i : customerFolder.listFiles(fileFilter)) {
//            String fileName = i.getName();
//            String username = fileName.replace(".json", "");
//            Account account = GetDataFromDatabase.getAccount(username);
//            list.add(account);
//        }
        return list;
    }

    public static String showUserDetails(String username) throws ExceptionsLibrary.NoAccountException {

        String func = "Show User Details";
        Client.sendMessage(func);

        Client.sendMessage(username);
        Object response = Client.receiveObject();

        if(response instanceof ExceptionsLibrary.NoAccountException)
            throw new ExceptionsLibrary.NoAccountException();
        else{
            return (String) response;
        }
//        Account account = GetDataFromDatabase.getAccount(username);
//        Gson gson = new GsonBuilder().serializeNulls().create();
//        return gson.toJson(account);
    }

    public static void deleteUser(String username) throws ExceptionsLibrary.NoAccountException {

        String func = "Delete User";
        Client.sendMessage(func);

        Client.sendMessage(username);
        Object response = Client.receiveObject();

        if(response instanceof ExceptionsLibrary.NoAccountException)
            throw new ExceptionsLibrary.NoAccountException();
        else{
            return;
        }
//        Account account = GetDataFromDatabase.getAccount(username);
//        String path = "Resources/Accounts/" + account.getRole() + "/" + account.getUsername() + ".json";
//        File file = new File(path);
//        file.delete();
    }

    public static void addAdminAccount(String newAdminDetails) throws ExceptionsLibrary.UsernameAlreadyExists {

        String func = "Add Admin Account";
        Client.sendMessage(func);

        Client.sendMessage(newAdminDetails);
        Object response = Client.receiveObject();

        if(response instanceof ExceptionsLibrary.UsernameAlreadyExists)
            throw new ExceptionsLibrary.UsernameAlreadyExists();
        else{
            return;
        }


//        Gson gson = new GsonBuilder().serializeNulls().create();
//        Admin admin1 = gson.fromJson(newAdminDetails,Admin.class);
//        if (RegisterAndLogin.checkUsername(admin1.getUsername())) {
//            RegisterAndLogin.registerAdmin(newAdminDetails);
//        } else {
//            throw new ExceptionsLibrary.UsernameAlreadyExists();
//        }
    }

    public static void deleteProduct(int productId) throws ExceptionsLibrary.NoProductException, ExceptionsLibrary.NoAccountException {

        String func = "Delete Product";
        Client.sendMessage(func);

        Client.sendMessage(String.valueOf(productId));
        Object response = Client.receiveObject();

        if(response instanceof ExceptionsLibrary.NoAccountException)
            throw new ExceptionsLibrary.NoAccountException();
        else if(response instanceof ExceptionsLibrary.NoProductException)
            throw new ExceptionsLibrary.NoProductException();
        else
            return;
//        Product product = GetDataFromDatabase.getProduct(productId);
//        String path = "Resources/Products/" + product.getProductId() + ".json";
//        SetDataToDatabase.updateSellerOfProduct(product,1);
//        File file = new File(path);
//        file.delete();
    }

    public static ArrayList<Category> showCategories() throws ExceptionsLibrary.NoCategoryException {
        ArrayList<Category> allCategories = new ArrayList<>();

        String func = "Show Categories";
        Client.sendMessage(func);

        Object response = Client.receiveObject();

        if(response instanceof ExceptionsLibrary.NoCategoryException)
            throw new ExceptionsLibrary.NoCategoryException();
        else
            allCategories = (ArrayList<Category>) response;

//        String path = "Resources/Category";
//        File file = new File(path);
//        FileFilter fileFilter = new FileFilter() {
//            @Override
//            public boolean accept(File file1) {
//                if (file1.getName().endsWith(".json")) {
//                    return true;
//                }
//                return false;
//            }
//        };
//        for (File i : file.listFiles(fileFilter)) {
//            String fileName = i.getName();
//            String categoryName = fileName.replace(".json", "");
//            Category category = GetDataFromDatabase.getCategory(categoryName);
//            allCategories.add(category);
//        }
        return allCategories;
    }

    public static void deleteCategory(String categoryName) throws ExceptionsLibrary.NoCategoryException, ExceptionsLibrary.NoProductException {

        String func = "Delete Category";
        Client.sendMessage(func);

        Client.sendMessage(categoryName);
        Object response = Client.receiveObject();

        if(response instanceof ExceptionsLibrary.NoCategoryException)
            throw new ExceptionsLibrary.NoCategoryException();
        else if(response instanceof ExceptionsLibrary.NoProductException)
            throw new ExceptionsLibrary.NoProductException();
        else
            return;
//                try {
//            Category category = GetDataFromDatabase.getCategory(categoryName);
//            FileFilter fileFilter = new FileFilter() {
//                @Override
//                public boolean accept(File file1) {
//                    if (file1.getName().endsWith(".json")) {
//                        return true;
//                    }
//                    return false;
//                }
//            };
//            File productsFolder = new File("Resources/Products");
//            for (File i : productsFolder.listFiles(fileFilter)) {
//                Gson gson = new GsonBuilder().serializeNulls().create();
//                try {
//                    String fileData = "";
//                    fileData = new String(Files.readAllBytes(Paths.get(i.getPath())));
//                    Product product = gson.fromJson(fileData, Product.class);
//                    if (product.getCategory().getName().equals(categoryName)) {
//                        SetDataToDatabase.updateSellerOfProduct(product,1);
//                        i.delete();
//                    }
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (ExceptionsLibrary.NoAccountException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            String path = "Resources/Category/" + category.getName() + ".json";
//            File file = new File(path);
//            file.delete();
//        } catch (ExceptionsLibrary.NoCategoryException e) {
//            throw new ExceptionsLibrary.NoCategoryException();
//        }
    }

    public static void editCategory(String categoryName, HashMap<String, String> dataToEdit) throws ExceptionsLibrary.CategoryExistsWithThisName, ExceptionsLibrary.NoCategoryException, ExceptionsLibrary.NoFeatureWithThisName, ExceptionsLibrary.NoAccountException, ExceptionsLibrary.NoProductException {

        String func = "Edit Category";
        Client.sendMessage(func);

        Object[] toSend = new Object[2];
        toSend[0] = categoryName;
        toSend[1] = dataToEdit;
        Client.sendObject(toSend);

        Object response = Client.receiveObject();

        if (response instanceof ExceptionsLibrary.CategoryExistsWithThisName)
            throw new ExceptionsLibrary.CategoryExistsWithThisName();

        else if (response instanceof ExceptionsLibrary.NoCategoryException)
            throw new ExceptionsLibrary.NoCategoryException();

        else if (response instanceof ExceptionsLibrary.NoFeatureWithThisName)
            throw new ExceptionsLibrary.NoFeatureWithThisName();

        else if (response instanceof ExceptionsLibrary.NoAccountException)
            throw new ExceptionsLibrary.NoAccountException();

        else if (response instanceof ExceptionsLibrary.NoProductException)
            throw new ExceptionsLibrary.NoProductException();
//        Category category = GetDataFromDatabase.getCategory(categoryName);
//        String oldName = category.getName();
//        for (String i : dataToEdit.keySet()) {
//            try {
//                Field field = Category.class.getDeclaredField(i);
//                if (i.equals("features")) {
//                    field.setAccessible(true);
//                    String[] splitFeatures = dataToEdit.get(i).split("\\s*,\\s*");
//                    ArrayList<Feature> newFeatures = new ArrayList<>();
//                    for (String j : splitFeatures) {
//                        newFeatures.add(new Feature(j, null));
//                    }
//                    field.set(category, newFeatures);
//                } else {
//                    field.setAccessible(true);
//                    field.set(category, dataToEdit.get(i));
//                }
//            } catch (NoSuchFieldException | IllegalAccessException e) {
//                throw new ExceptionsLibrary.NoFeatureWithThisName();
//            }
//        }
//        Gson gson = new GsonBuilder().serializeNulls().create();
//        String newName = category.getName();
//        String editedDetails = gson.toJson(category);
//        if (newName.equals(oldName)) {
//            try {
//                String path = "Resources/Category/" + category.getName() + ".json";
//                FileWriter fileWriter = new FileWriter(path);
//                fileWriter.write(editedDetails);
//                fileWriter.close();
//                File file = new File(path);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            try {
//                String newPath = "Resources/Category/" + newName + ".json";
//                String oldPath = "Resources/Category/" + oldName + ".json";
//                File file = new File(newPath);
//                if (file.exists()) {
//                    throw new ExceptionsLibrary.CategoryExistsWithThisName();
//                }
//                file.createNewFile();
//                FileWriter fileWriter = new FileWriter(newPath);
//                fileWriter.write(editedDetails);
//                fileWriter.close();
//                File file1 = new File(oldPath);
//                file1.delete();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        File folder = new File("Resources/Products");
//        FileFilter fileFilter = new FileFilter() {
//            @Override
//            public boolean accept(File file1) {
//                if (file1.getName().endsWith(".json")) {
//                    return true;
//                }
//                return false;
//            }
//        };
//        for (File i : folder.listFiles(fileFilter)) {
//            Gson gson1 = new GsonBuilder().serializeNulls().create();
//            try {
//                String fileData = "";
//                fileData = new String(Files.readAllBytes(Paths.get(i.getPath())));
//                Product product = gson1.fromJson(fileData, Product.class);
//                product.setCategory(category);
//                SetDataToDatabase.setProduct(product);
//                SetDataToDatabase.updateSellerOfProduct(product,0);
//            } catch (ExceptionsLibrary.NoAccountException | IOException e) {
//                throw new ExceptionsLibrary.NoAccountException();
//            }
//        }
    }

    public static void addCategory(String categoryDetails) throws ExceptionsLibrary.CategoryExistsWithThisName {

        String func = "Add Category";
        Client.sendMessage(func);

        Client.sendMessage(categoryDetails);
        Object response = Client.receiveObject();

        if (response instanceof ExceptionsLibrary.CategoryExistsWithThisName)
            throw new ExceptionsLibrary.CategoryExistsWithThisName();

        else
            return;
//        Gson gson = new GsonBuilder().serializeNulls().create();
//        Category category = gson.fromJson(categoryDetails, Category.class);
//        if (!checkCategoryName(category.getName())) {
//            String newSaleDetails = gson.toJson(category);
//            try {
//                String path = "Resources/Category/" + category.getName() + ".json";
//                File file = new File(path);
//                file.createNewFile();
//                FileWriter fileWriter = new FileWriter(path);
//                fileWriter.write(newSaleDetails);
//                fileWriter.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            throw new ExceptionsLibrary.CategoryExistsWithThisName();
//        }
    }

    public static String viewSaleCodeDetails(String saleCode) throws ExceptionsLibrary.NoSaleException {

        String func = "View Sale Code Details";
        Client.sendMessage(func);

        Client.sendMessage(saleCode);
        Object response = Client.receiveObject();

        if(response instanceof ExceptionsLibrary.NoSaleException)
            throw new ExceptionsLibrary.NoSaleException();

        else
            return (String) response;

//        Sale sale = GetDataFromDatabase.getSale(saleCode);
//        Gson gson = new GsonBuilder().serializeNulls().create();
//        return gson.toJson(sale);
    }

    public static void removeSaleCode(String saleCode) throws ExceptionsLibrary.NoSaleException {

        String func = "Remove Sale Code";
        Client.sendMessage(func);

        Client.sendMessage(saleCode);
        Object response = Client.receiveObject();

        if (response instanceof ExceptionsLibrary.NoSaleException)
            throw new ExceptionsLibrary.NoSaleException();
        else
            return;
//        try {
//            Sale sale = GetDataFromDatabase.getSale(saleCode);
//            FileFilter fileFilter = new FileFilter() {
//                @Override
//                public boolean accept(File file1) {
//                    if (file1.getName().endsWith(".json")) {
//                        return true;
//                    }
//                    return false;
//                }
//            };
//            File customerFolder = new File("Resources/Accounts/Customer");
//            File sellerFolder = new File("Resources/Accounts/Seller");
//            File adminFolder = new File("Resources/Accounts/Admin");
//
//            for (File i : customerFolder.listFiles(fileFilter)) {
//                Gson gson = new GsonBuilder().serializeNulls().create();
//                try {
//                    String fileData = "";
//                    fileData = new String(Files.readAllBytes(Paths.get(i.getPath())));
//                    Customer customer = gson.fromJson(fileData, Customer.class);
//                    Iterator<Sale> iterator = customer.getSaleCodes().iterator();
//                    while (iterator.hasNext()) {
//                        Sale tempSale = iterator.next();
//                        if (tempSale.getSaleCode().equalsIgnoreCase(sale.getSaleCode())) {
//                            iterator.remove();
//                        }
//                    }
//                    SetDataToDatabase.setAccount(customer);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            for (File i : sellerFolder.listFiles(fileFilter)) {
//                Gson gson = new GsonBuilder().serializeNulls().create();
//                try {
//                    String fileData = "";
//                    fileData = new String(Files.readAllBytes(Paths.get(i.getPath())));
//                    Seller seller = gson.fromJson(fileData, Seller.class);
//                    Iterator<Sale> iterator = seller.getSaleCodes().iterator();
//                    while (iterator.hasNext()) {
//                        Sale tempSale = iterator.next();
//                        if (tempSale.getSaleCode().equalsIgnoreCase(sale.getSaleCode())) {
//                            iterator.remove();
//                        }
//                    }
//                    SetDataToDatabase.setAccount(seller);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            for (File i : adminFolder.listFiles(fileFilter)) {
//                Gson gson = new GsonBuilder().serializeNulls().create();
//                try {
//                    String fileData = "";
//                    fileData = new String(Files.readAllBytes(Paths.get(i.getPath())));
//                    Admin admin = gson.fromJson(fileData, Admin.class);
//                    Iterator<Sale> iterator = admin.getSaleCodes().iterator();
//                    while (iterator.hasNext()) {
//                        Sale tempSale = iterator.next();
//                        if (tempSale.getSaleCode().equalsIgnoreCase(sale.getSaleCode())) {
//                            iterator.remove();
//                        }
//                    }
//                    SetDataToDatabase.setAccount(admin);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            String path = "Resources/Sales/" + sale.getSaleCode() + ".json";
//            File file = new File(path);
//            file.delete();
//        } catch (ExceptionsLibrary.NoSaleException e) {
//            throw new ExceptionsLibrary.NoSaleException();
//        }
    }

    public static boolean checkCategoryName(String categoryName) {

        String func = "Check Category Name";
        Client.sendMessage(func);

        Client.sendMessage(categoryName);
        Object response = Client.receiveObject();
        return (boolean) response;
//        String path = "Resources/Category";
//        File folder = new File(path);
//        FileFilter fileFilter = new FileFilter() {
//            @Override
//            public boolean accept(File file1) {
//                if (file1.getName().endsWith(".json")) {
//                    return true;
//                }
//                return false;
//            }
//        };
//        for (File i : folder.listFiles(fileFilter)) {
//            String fileName = i.getName();
//            String fileCategoryName = fileName.replace(".json", "");
//            if (categoryName.equals(fileCategoryName)) {
//                return true;
//            }
//        }
//        return false;
    }

    public static boolean checkSaleCode(String saleCode) {

        String func = "Check Sale Code";
        Client.sendMessage(func);

        Client.sendMessage(saleCode);
        Object response = Client.receiveObject();
        return (boolean) response;
//        String path = "Resources/Sales";
//        File folder = new File(path);
//        FileFilter fileFilter = new FileFilter() {
//            @Override
//            public boolean accept(File file) {
//                if (file.getName().endsWith(".json")) {
//                    return true;
//                }
//                return false;
//            }
//        };
//        for (File i : folder.listFiles(fileFilter)) {
//            String fileName = i.getName();
//            String fileSaleCode = fileName.replace(".json", "");
//            if (saleCode.equals(fileSaleCode)) {
//                return true;
//            }
//        }
//        return false;
    }


    public static boolean checkIfProductExist(int productId) {

        String func = "Check If Product Exists";
        Client.sendMessage(func);

        Client.sendMessage(String.valueOf(productId));
        Object response = Client.receiveObject();
        return (boolean) response;
//        String path = "Resources/Products/" + productId + ".json";
//        File file = new File(path);
//        if (!file.exists()) {
//            return false;
//        } else {
//            return true;
//        }
    }

    public static boolean checkIfRequestExist(int requestId) {

        String func = "Check If Request Exists";
        Client.sendMessage(func);

        Client.sendMessage(String.valueOf(requestId));
        Object response = Client.receiveObject();
        return (boolean) response;
//        String path = "Resources/Requests/" + requestId + ".json";
//        File file = new File(path);
//        if (!file.exists()) {
//            return false;
//        } else {
//            return true;
//        }
    }

    public static ArrayList<Product> getAllProducts() throws ExceptionsLibrary.NoProductException {
        ArrayList<Product> allProducts = new ArrayList<>();

        String func = "Get All Products Admin";
        Client.sendMessage(func);

        Object response = Client.receiveObject();

        if(response instanceof ExceptionsLibrary.NoProductException)
            throw new ExceptionsLibrary.NoProductException();

        else
            allProducts = (ArrayList<Product>) response;
//        String path = "Resources/Products";
//        File folder = new File(path);
//        FileFilter fileFilter = new FileFilter() {
//            @Override
//            public boolean accept(File file1) {
//                if (file1.getName().endsWith(".json")) {
//                    return true;
//                }
//                return false;
//            }
//        };
//        for (File i : folder.listFiles(fileFilter)) {
//            String fileName = i.getName();
//            int productId = Integer.parseInt(fileName.replace(".json", ""));
//            allProducts.add(GetDataFromDatabase.getProduct(productId));
//        }
        return allProducts;
    }
}
