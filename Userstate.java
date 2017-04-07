import java.util.*;
import java.text.*;
import java.io.*;

public class Userstate extends WareState {
    private static Userstate userstate;
    private static Warehouse warehouse;

    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private static final int EXIT             = 0;
    private static final int QUERY_MODE       = 4;
    private static final int ACCEPT_ORDER     = 5;
    private static final int PROCESS_ORDER    = 6;
    private static final int CREATE_INVOICE   = 7;
    private static final int PAYMENT          = 8;
    private static final int MENU             = 18;

    private Userstate() {
        warehouse = Warehouse.instance();
    }

    public static Userstate instance() {
        if (userstate == null) {
            return userstate = new Userstate();
        }
        else {
            return userstate;
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
         + "                      CLIENT\n\n"
         + "       +--------------------------------------+\n"
         + "       | " + QUERY_MODE       + ")\tEnter Query Mode              |\n"
         + "       | " + ACCEPT_ORDER     + ")\tAccept Order from Client      |\n"
         + "       | " + PROCESS_ORDER    + ")\tProcess Order                 |\n"
         + "       | " + CREATE_INVOICE   + ")\tInvoice from processed Order  |\n"
         + "       | " + PAYMENT          + ")\tMake a payment                |\n"
         + "       | " + MENU             + ")\tDisplay Menu                  |\n"
         + "       | " + EXIT             + ")\tExit                          |\n"
         + "       +--------------------------------------+\n");
    }

    public void queryMode() {
        (WareContext.instance()).changeState(5);
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

    public void logout() {
        if ((WareContext.instance()).getLogin() == WareContext.isClerk) {
            (WareContext.instance()).changeState(1);
        }
        else if (WareContext.instance().getLogin() == WareContext.isUser) {
            (WareContext.instance()).changeState(0);
        }
        else if (WareContext.instance().getLogin() == WareContext.isManager) {
            (WareContext.instance()).changeState(3);
        }
        else {
            (WareContext.instance()).changeState(2);
        }
    }

    public void process() {
        int command;
        menu();
        while ((command = getCommand()) != EXIT) {
            switch (command) {
                case QUERY_MODE:     queryMode();
                break;
                case ACCEPT_ORDER:   acceptOrder();
                break;
                case PROCESS_ORDER:  processOrder();
                break;
                case CREATE_INVOICE: acceptOrder();
                break;
                case PAYMENT:        payment();
                break;
                case MENU:           menu();
                break;
            }
        }
        logout();
    }

    public void run() {
        process();
    }
}
