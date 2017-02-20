import java.util.*;
import java.text.*;
import java.io.*;

public class UserInterface {
    private static UserInterface userInterface;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Warehouse warehouse;
    private static final int EXIT             = 0;
    private static final int ADD_CLIENT       = 1;
    private static final int ADD_PRODUCT      = 2;
    private static final int ADD_SUPPLIER     = 3;
    private static final int ACCEPT_ORDER     = 4;
    private static final int PROCESS_ORDER    = 5;
    private static final int ASSIGN_PRODUCT   = 6;
    private static final int UNASSIGN_PRODUCT = 7;
    private static final int SHOW_CLIENTS     = 8;
    private static final int SHOW_PRODUCTS    = 9;
    private static final int SHOW_SUPPLIERS   = 10;
    private static final int SHOW_ORDERS      = 11;
    private static final int SAVE             = 12;
    private static final int MENU             = 13;

    private UserInterface() {
        if (yesOrNo("Look for saved data and use it?")) {
            retrieve();
        }
        else {
            warehouse = Warehouse.instance();
        }
    }

    public static UserInterface instance() {
        if (userInterface == null) {
            return userInterface = new UserInterface();
        }
        else {
            return userInterface;
        }
    }

    // String prompt used to capture info.
    public String getToken(String prompt) {
        do {
            try {
                System.out.print(prompt);
                String line = reader.readLine();
                StringTokenizer tokenizer = new StringTokenizer(line,"\n\r\f");
                if (tokenizer.hasMoreTokens()) {
                    return tokenizer.nextToken();
                }
            }
            catch (IOException ioe) {
                System.exit(0);
            }
        } while (true);
    }

    // Integer prompt using token method.
    public int getInt(String prompt) {
        do {
            try {
                String item = getToken(prompt);
                Integer num = Integer.valueOf(item);
                return num.intValue();
            }
            catch (NumberFormatException nfe) {
                System.out.println("Please input a number ");
            }
        } while (true);
    }

    // Float prompt using token method.
    public float getFloat(String prompt) {
        do {
            try {
                String item = getToken(prompt);
                float f = Float.parseFloat(item);
                return f;
            }
            catch (NumberFormatException nfe) {
                System.out.println("Please input a number ");
            }
        } while (true);
    }

    // Yes or no prompt.
    private boolean yesOrNo(String prompt) {
        String more = getToken(prompt + " (Y|y)[es] or anything else for no: ");
        if (more.charAt(0) != 'y' && more.charAt(0) != 'Y') {
            return false;
        }
        return true;
    }

    // Menu prompt.
    public int getCommand() {
        do {
            try {
                int value = Integer.parseInt(getToken("> "));
                if (value >= EXIT && value <= MENU) {
                    return value;
                }
            }
            catch (NumberFormatException nfe) {
                System.out.println("Enter a number");
            }
        } while (true);
    }

    // Menu of warehouse options.
    public void menu() {
        System.out.println("                 Warehouse System\n"
                         + "                      Stage 2\n\n"
                         + "       +------------------------------------+\n"
                         + "       | " + ADD_CLIENT       + ")\tAdd Client                  |\n"
                         + "       | " + ADD_PRODUCT      + ")\tAdd Product                 |\n"
                         + "       | " + ADD_SUPPLIER     + ")\tAdd Supplier                |\n"
                         + "       | " + ASSIGN_PRODUCT   + ")\tAssign Product to Supplier  |\n"
                         + "       | " + UNASSIGN_PRODUCT + ")\tUnssign Product to Supplier |\n"
                         + "       | " + ACCEPT_ORDER     + ")\tAccept Order from Client    |\n"
                         + "       | " + PROCESS_ORDER    + ")\tProcess Order               |\n"
                         + "       | " + SHOW_CLIENTS     + ")\tShow Clients                |\n"
                         + "       | " + SHOW_PRODUCTS    + ")\tShow Products               |\n"
                         + "       | " + SHOW_SUPPLIERS   + ")\tShow Suppliers              |\n"
                         + "       | " + SHOW_ORDERS      + ")\tShow Orders                 |\n"
                         + "       | " + SAVE             + ")\tSave State                  |\n"
                         + "       | " + MENU             + ")\tDisplay Menu                |\n"
                         + "       | " + EXIT             + ")\tExit                        |\n"
                         + "       +------------------------------------+\n");
    }

    // Capture tokens for adding a client.
    public void addClient() {
        String name = getToken("Enter client company: ");
        String address = getToken("Enter address: ");
        String phone = getToken("Enter phone: ");
        Client result;
        result = warehouse.addClient(name, address, phone);
        if (result == null) {
            System.out.println("Client could not be added.");
        }
        System.out.println(result);
    }

    // Capture tokens for adding a product.
    public void addProduct() {
        Product result;
        String prodName = getToken("Enter product name: ");
        int quantity = getInt("Enter quantity: ");
        float price = getFloat("Enter price per unit: $");
        result = warehouse.addProduct(prodName, quantity, price);
        if (result != null) {
            System.out.println(result);
        }
        else {
            System.out.println("Product could not be added.");
        }
    }

    // Capture tokens for adding a supplier.
    public void addSupplier() {
        String name = getToken("Enter supplier company: ");
        String address = getToken("Enter address: ");
        String phone = getToken("Enter phone: ");
        Supplier result;
        result = warehouse.addSupplier(name, address, phone);
        if (result == null) {
            System.out.println("Supplier could not be added.");
        }
        System.out.println(result);
    }

    // Capture tokens for assigning product(s) from a single supplier.
    public void linkProduct() {
        Product result;
        String supplierID = getToken("Enter supplier ID: ");
        if (warehouse.searchSupplier(supplierID) == null) {
            System.out.println("No such supplier!");
            return;
        }
        do {
            String productID = getToken("Enter product ID: ");
            result = warehouse.linkProduct(supplierID, productID);
            if (result != null) {
                System.out.println("Product [" + productID + "] assigned to supplier: [" + supplierID + "]");
            }
            else {
                System.out.println("Product could not be assigned");
            }
            if (!yesOrNo("Assign more products to supplier: [" + supplierID + "]")) {
                break;
            }
        } while (true);
    }

    // Capture tokens for unassigning product(s) from a single supplier.
    public void unlinkProduct() {
        Product result;
        String supplierID = getToken("Enter supplier ID: ");
        if (warehouse.searchSupplier(supplierID) == null) {
            System.out.println("No such supplier!");
            return;
        }
        do {
            String productID = getToken("Enter product ID: ");
            result = warehouse.unlinkProduct(supplierID, productID);
            if (result != null) {
                System.out.println("Product [" + productID + "] unassigned from supplier: [" + supplierID + "]");
            }
            else {
                System.out.println("Product could not be unassigned");
            }
            if (!yesOrNo("unassign more products from supplier: ["+ supplierID + "]")) {
                break;
            }
        } while (true);
    }

    public void acceptOrder() {
        Client clientObj;
        Product productObj;
        Order result;

        String clientID = getToken("Enter a client ID to start order: ");
        clientObj = warehouse.searchClient(clientID);
        if (clientObj == null) {
            System.out.println("Client does not exist.");
            return;
        }
        String productID = getToken("Enter a product ID: ");
        productObj = warehouse.searchProduct(productID);
        if (productObj == null) {
            System.out.println("Product does not exist.");
            return;
        }

        int quantity = getInt("How many of the products to order?: ");

        result = warehouse.addOrder(clientObj, productObj, quantity, "Q");
        if (result == null) {
            System.out.println("Order could not be added.");
        }
        else {
            System.out.println("Order added to queue!");
            System.out.println(result);
        }
    }

    public void processOrder() {
        Order orderObj;
        String orderID = getToken("Enter an order ID process: ");

        orderObj = warehouse.searchOrder(orderID);
        if (orderObj == null) {
            System.out.println("Order does not exist.");
            return;
        }

        String orderStatus = orderObj.getStatus();
        if (orderStatus.equals("C")) {
            System.out.println("Completed orders cannot be processed!");
            return;
        }

        orderObj = warehouse.processOrder(orderID);
        if (orderObj == null) {
            System.out.println("Order could not be processed.");
        }
        else {
            System.out.println("Order processed!");
        }
    }

    // Queries.
    public void showClients() {
        Iterator allClients = warehouse.getClients();
        while (allClients.hasNext()) {
            Client client = (Client)(allClients.next());
            System.out.println(client.toString());
        }
    }

    public void showProducts() {
        Iterator allProducts = warehouse.getProducts();
        while (allProducts.hasNext()){
            Product product = (Product)(allProducts.next());
            System.out.println(product.toString());
        }
    }

    public void showSuppliers() {
        Iterator allSuppliers = warehouse.getSuppliers();
        while (allSuppliers.hasNext()) {
            Supplier supplier = (Supplier)(allSuppliers.next());
            System.out.println(supplier.toString());
        }
    }

    public void showOrders() {
        Iterator allOrders = warehouse.getOrders();
        while (allOrders.hasNext()) {
            Order order = (Order)(allOrders.next());
            System.out.println(order.toString());
        }
    }

    private void save() {
        if (warehouse.save()) {
            System.out.println(" The warehouse has been successfully saved in the file WarehouseData \n" );
        }
        else {
            System.out.println(" There has been an error in saving \n" );
        }
    }

    private void retrieve() {
        try {
            Warehouse tempWarehouse = Warehouse.retrieve();
            if (tempWarehouse != null) {
                System.out.println(" The warehouse has been successfully saved in the file WarehouseData \n" );
                warehouse = tempWarehouse;
            } else {
                System.out.println("File doesnt exist; creating new warehouse" );
                warehouse = Warehouse.instance();
            }
        } catch(Exception cnfe) {
            cnfe.printStackTrace();
        }
    }

    // Switch case processing for menu.
    public void process() {
        int command;
        menu();
        while ((command = getCommand()) != EXIT) {
            switch (command) {
                case ADD_CLIENT:        addClient();
                                        break;
                case ADD_PRODUCT:       addProduct();
                                        break;
                case ADD_SUPPLIER:      addSupplier();
                                        break;
                case ASSIGN_PRODUCT:    linkProduct();
                                        break;
                case UNASSIGN_PRODUCT:  unlinkProduct();
                                        break;
                case ACCEPT_ORDER:      acceptOrder();
                                        break;
                case PROCESS_ORDER:     processOrder();
                                        break;
                case SHOW_CLIENTS:      showClients();
                                        break;
                case SHOW_PRODUCTS:     showProducts();
                                        break;
                case SHOW_SUPPLIERS:    showSuppliers();
                                        break;
                case SHOW_ORDERS:       showOrders();
                                        break;
                case SAVE:              save();
                                        break;
                case MENU:              menu();
                                        break;
            }
        }
    }

    public static void main(String[] s) {
        UserInterface.instance().process();
    }
}
