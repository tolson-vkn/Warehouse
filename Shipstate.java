import java.util.*;
import java.text.*;
import java.io.*;

public class Shipstate extends WareState {
    private static Warehouse warehouse;
    private static Shipstate instance;

    private WareContext context;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private static final int EXIT             = 0;
    private static final int ACCEPT_SHIPMENT  = 1;
    private static final int ADD_PRODUCT      = 2;
    private static final int ASSIGN_PRODUCT   = 3;
    private static final int UNASSIGN_PRODUCT = 4;
    private static final int MENU             = 18;

    private Shipstate() {
        super();
        warehouse = Warehouse.instance();
    }

    public static Shipstate instance() {
        if (instance == null) {
            instance = new Shipstate();
        }
        return instance;
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
        System.out.println(
           "                 Warehouse System\n"
         + "                   SHIPMENT MODE\n\n"
         + "       +--------------------------------------+\n"
         + "       | " + ACCEPT_SHIPMENT  + ")\tAccept Shipment from Supplier |\n"
         + "       | " + ADD_PRODUCT      + ")\tAdd Product                   |\n"
         + "       | " + ASSIGN_PRODUCT   + ")\tAssign Product to Supplier    |\n"
         + "       | " + UNASSIGN_PRODUCT + ")\tUnssign Product to Supplier   |\n"
         + "       | " + MENU             + ")\tDisplay Menu                  |\n"
         + "       | " + EXIT             + ")\tExit                          |\n"
         + "       +--------------------------------------+\n");
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

    public void usermenu() {
        String userID = getToken("Please input the user id: ");
        if (Warehouse.instance().searchClient(userID) != null) {
            (WareContext.instance()).setUser(userID);
            (WareContext.instance()).changeState(1);
        }
        else {
            System.out.println("Invalid user id.");
        }
    }

    public void logout() {
        if ((WareContext.instance()).getLogin() == WareContext.isManager) {
            (WareContext.instance()).changeState(3);
        }
        else if ((WareContext.instance()).getLogin() == WareContext.isClerk) {
            (WareContext.instance()).changeState(0);
        }
    }

    public void process() {
        int command;
        menu();
        while ((command = getCommand()) != EXIT) {
            switch (command) {
                case ACCEPT_SHIPMENT:  acceptShipment();
                break;
                case ADD_PRODUCT:      addProduct();
                break;
                case ASSIGN_PRODUCT:   linkProduct();
                break;
                case UNASSIGN_PRODUCT: unlinkProduct();
                break;
                case MENU:             menu();
                break;
            }
        }
        logout();
    }

    public void run() {
        process();
    }
}
