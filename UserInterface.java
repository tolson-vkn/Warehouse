import java.util.*;
import java.text.*;
import java.io.*;

public class UserInterface {
    private static UserInterface userInterface;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Warehouse warehouse;
    private static final int TEST             = -1;
    private static final int EXIT             = 0;
    private static final int ADD_CLIENT       = 1;
    private static final int ADD_PRODUCT      = 2;
    private static final int ADD_SUPPLIER     = 3;
    private static final int ACCEPT_SHIPMENT  = 4;
    private static final int ACCEPT_ORDER     = 5;
    private static final int PROCESS_ORDER    = 6;
    private static final int CREATE_INVOICE   = 7;
    private static final int PAYMENT          = 8;
    private static final int ASSIGN_PRODUCT   = 9;
    private static final int UNASSIGN_PRODUCT = 10;
    private static final int SHOW_CLIENTS     = 11;
    private static final int SHOW_PRODUCTS    = 12;
    private static final int SHOW_SUPPLIERS   = 13;
    private static final int SHOW_ORDERS      = 14;
    private static final int GET_TRANS        = 15;
    private static final int GET_INVOICE      = 16;
    private static final int SAVE             = 17;
    private static final int MENU             = 18;

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
    public double getDouble(String prompt) {
        do {
            try {
                String item = getToken(prompt);
                double f = Double.parseDouble(item);
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
                if (value >= TEST && value <= MENU) {
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
        System.out.println(
           "                 Warehouse System\n"
         + "                      Stage 3\n\n"
         + "       +--------------------------------------+\n"
         + "       | " + ADD_CLIENT       + ")\tAdd Client                    |\n"
         + "       | " + ADD_PRODUCT      + ")\tAdd Product                   |\n"
         + "       | " + ADD_SUPPLIER     + ")\tAdd Supplier                  |\n"
         + "       | " + ACCEPT_SHIPMENT  + ")\tAccept Shipment from Supplier |\n"
         + "       | " + ACCEPT_ORDER     + ")\tAccept Order from Client      |\n"
         + "       | " + PROCESS_ORDER    + ")\tProcess Order                 |\n"
         + "       | " + CREATE_INVOICE   + ")\tInvoice from processed Order  |\n"
         + "       | " + PAYMENT          + ")\tMake a payment                |\n"
         + "       | " + ASSIGN_PRODUCT   + ")\tAssign Product to Supplier    |\n"
         + "       | " + UNASSIGN_PRODUCT + ")\tUnssign Product to Supplier   |\n"
         + "       | " + SHOW_CLIENTS     + ")\tShow Clients                  |\n"
         + "       | " + SHOW_PRODUCTS    + ")\tShow Products                 |\n"
         + "       | " + SHOW_SUPPLIERS   + ")\tShow Suppliers                |\n"
         + "       | " + SHOW_ORDERS      + ")\tShow Orders                   |\n"
         + "       | " + GET_TRANS        + ")\tGet Transaction of a Client   |\n"
         + "       | " + GET_INVOICE      + ")\tGet Invoices of a Client      |\n"
         + "       | " + SAVE             + ")\tSave State                    |\n"
         + "       | " + MENU             + ")\tDisplay Menu                  |\n"
         + "       | " + EXIT             + ")\tExit                          |\n"
         + "       +--------------------------------------+\n");
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
        double price = getDouble("Enter price per unit: $");
        result = warehouse.addProduct(prodName, price);
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
        Order orderObj;
        OrderItem orderItemOjb;

        String clientID = getToken("Enter a client ID to start order: ");
        clientObj = warehouse.searchClient(clientID);
        if (clientObj == null) {
            System.out.println("Client does not exist.");
            return;
        }

        orderObj = warehouse.createOrder(clientID);
        if (orderObj == null) {
            System.out.println("Could not initiate order.");
            return;
        }

        String orderID = orderObj.getID();

        do {
            String productID = getToken("Enter a product ID: ");
            productObj = warehouse.searchProduct(productID);
            if (productObj == null) {
                System.out.println("Product does not exist.");
                return;
            }

            int quantity = getInt("How many of the products to order?: ");

            orderItemOjb = warehouse.addToOrder(orderID, productID, quantity);
            if (orderItemOjb == null) {
                System.out.println("Order could not be added.");
            }
            else {
                System.out.println("Order added to queue!");
                System.out.println("\t" + orderItemOjb);
            }

            if(yesOrNo("More products on this order?")) {
                System.out.println();
            }
            else {
                System.out.println("Order added!");
                break;
            }
        } while (true);
    }

    public void acceptShipment() {
        Supplier supplierObj;
        Product productObj;
        Product result;

        String supplierID = getToken("Enter a supplier ID to start shipment: ");
        supplierObj = warehouse.searchSupplier(supplierID);
        if (supplierObj == null) {
            System.out.println("Supplier does not exist.");
            return;
        }

        do {
            String productID = getToken("Enter product ID in shipment: ");
            productObj = warehouse.searchProduct(productID);
            if (productObj == null) {
                System.out.println("Product does not exist.");
                return;
            }

            if (!warehouse.isLinked(supplierID, productID)) {
                System.out.println("Supplier does not offer product.");
                return;
            }

            int quantity = getInt("How many of the products are in the shipment?: ");

            for (Iterator iterator = warehouse.getWaitlistOrders(productID); iterator.hasNext();) {
                Waitlist waitlist = (Waitlist)(iterator.next());
                System.out.println("There is a waitlisted order for this shipment:");
                Client theClient = waitlist.getClient();
                String waitlistReply =  "\t" + theClient.getName() + "\n\tneeds " +
                waitlist.getQuantity() + " " + productObj.getProdName() + "(s) to fullfill waitlist.";
                System.out.println(waitlistReply);

                if (quantity < waitlist.getQuantity()) {
                    System.out.println("There is not enough in the shipment to fullfill this waitlist order.\n");
                    continue;
                }

                if (yesOrNo("Attempt to fullfill waitlist?")) {
                    waitlist = warehouse.processWaitlist(productID, waitlist.getID(), quantity);
                    if (waitlist == null) {
                        System.out.println("Could not fullfill waitlist order.");
                    }
                    else {
                        System.out.println(theClient.getName() + " waitlist fullfilled.");
                        quantity = quantity - waitlist.getQuantity();
                        iterator.remove();
                    }
                }

                // For pretty output.
                System.out.println();
            }

            result = warehouse.addShipment(supplierID, productID, quantity);
            if (result == null) {
                System.out.println("Shipment of product could not be completed.");
            }
            else {
                System.out.println("Product " + productObj.getProdName()
                                 + " updated quantity " + result.getQuantity());
            }

            if(yesOrNo("More products on this shipment?")) {
                System.out.println();
            }
            else {
                System.out.println("Shipment processed!");
                break;
            }
        } while (true);
    }

    public void processOrder() {
        Order orderObj;
        String orderID = getToken("Enter an order ID process: ");

        orderObj = warehouse.searchOrder(orderID);
        if (orderObj == null) {
            System.out.println("Order does not exist.");
            return;
        }

        orderObj = warehouse.processOrder(orderID);
        if (orderObj == null) {
            System.out.println("Order could not be processed, is waitlisted.");
        }
        else {
            System.out.println("Order processed! Use option (" + CREATE_INVOICE
                             + ") to make an invoice.");
        }
    }

    public void createInvoice() {
        Order orderObj;
        String clientID;
        Invoice invoiceObj;

        String orderID = getToken("Enter an order ID to create invoice from: ");
        orderObj = warehouse.searchOrder(orderID);
        if (orderObj == null) {
            System.out.println("Order does not exist.");
        }

        invoiceObj = warehouse.createInvoice(orderID);
        if (invoiceObj == null) {
            System.out.println("Could not create order");
        }
        else {
            System.out.println("Order created:\n");
            System.out.println(invoiceObj);
        }
    }

    public void payment() {
        Client result;
        String clientID = getToken("Enter a client ID to make a payment for: ");

        result = warehouse.searchClient(clientID);
        if (result == null) {
            System.out.println("Client does not exist.");
            return;
        }

        if (!warehouse.needsPayment(clientID)) {
            System.out.println("Client does not need to make payment!");
            return;
        }

        double clientPayment = getDouble("Enter amount to pay: ");
        if (clientPayment < 0) {
            System.out.println("Cannot mane negative paments.");
            return;
        }

        result = warehouse.makePayment(clientID, clientPayment);
        if (result == null) {
            System.out.println("Payment could not be made.");
        }
        else {
            System.out.println("Payment made, thank you!");
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

    public void getTransactions() {
        Iterator result;

        String clientID = getToken("Enter client ID to view transactions: ");
        result = warehouse.getTransactions(clientID);
        if (result == null) {
            System.out.println("Client does not exist.");
        }
        else {
            System.out.println("Begin transaction listing.\n");
            while (result.hasNext()) {
                Transaction transaction = (Transaction) result.next();
                System.out.println(transaction.toString());
            }
            System.out.println("\nThere are no more transactions.\n");
        }
    }

    public void getInvoices() {
        Iterator result;

        String clientID = getToken("Enter client ID to view invoices: ");
        result = warehouse.getInvoices(clientID);
        if (result == null) {
            System.out.println("Client does not exist.");
        }
        else {
            System.out.println("Begin invoice listing.\n");
            while (result.hasNext()) {
                Invoice invoice = (Invoice) result.next();
                System.out.println(invoice.toString());
            }
            System.out.println("\nThere are no more invoices.\n");
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

    public void test() {
        // System.out.println("Could not get this working properly, please view logfile in finished folder.");
        // String supplierID = getToken("Enter supplier ID: ");
        // String productID = getToken("Enter product ID: ");
        // System.out.println("Status: " + warehouse.isLinked(supplierID, productID));
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
                case ACCEPT_SHIPMENT:   acceptShipment();
                                        break;
                case ACCEPT_ORDER:      acceptOrder();
                                        break;
                case PROCESS_ORDER:     processOrder();
                                        break;
                case CREATE_INVOICE:    createInvoice();
                                        break;
                case PAYMENT:           payment();
                                        break;
                case SHOW_CLIENTS:      showClients();
                                        break;
                case SHOW_PRODUCTS:     showProducts();
                                        break;
                case SHOW_SUPPLIERS:    showSuppliers();
                                        break;
                case SHOW_ORDERS:       showOrders();
                                        break;
                case GET_TRANS:         getTransactions();
                                        break;
                case GET_INVOICE:       getInvoices();
                                        break;
                case SAVE:              save();
                                        break;
                case MENU:              menu();
                                        break;
                case TEST:              test();
                                        break;
            }
        }
    }

    public static void main(String[] s) {
        UserInterface.instance().process();
    }
}
