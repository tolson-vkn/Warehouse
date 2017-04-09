import java.util.*;
import java.text.*;
import java.io.*;

public class ClerkState extends WareState {
    private static Warehouse warehouse;
    private static ClerkState instance;

    private WareContext context;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private static final int EXIT            = 0;
    private static final int SHIPMENT_MODE   = 1;
    private static final int QUERY_MODE      = 2;
    private static final int USER_MODE       = 3;
    private static final int PROCESS_ORDER   = 4;
    private static final int CREATE_INVOICE  = 5;
    private static final int MENU            = 10;

    private ClerkState() {
        super();
        warehouse = Warehouse.instance();
    }

    public static ClerkState instance() {
        if (instance == null) {
            instance = new ClerkState();
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
         + "                       CLERK\n\n"
         + "       +--------------------------------------+\n"
         + "       | " + SHIPMENT_MODE    + ")\tEnter Shipment Mode           |\n"
         + "       | " + QUERY_MODE       + ")\tEnter Query Mode              |\n"
         + "       | " + USER_MODE        + ")\tEnter Client Mode             |\n"
         + "       | " + PROCESS_ORDER    + ")\tProcess Order                 |\n"
         + "       | " + CREATE_INVOICE   + ")\tCreate Invoice                |\n"
         + "       | " + MENU             + ")\tDisplay Menu                  |\n"
         + "       | " + EXIT             + ")\tExit                          |\n"
         + "       +--------------------------------------+\n");
    }

    public void shipmentMode() {
        (WareContext.instance()).changeState(4);
    }

    public void queryMode() {
        (WareContext.instance()).changeState(5);
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
            System.out.println("Order processed!");
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

    public void logout() {
        if ((WareContext.instance()).getLogin() == WareContext.isManager) {
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
                case SHIPMENT_MODE:  shipmentMode();
                break;
                case USER_MODE:      usermenu();
                break;
                case QUERY_MODE:     queryMode();
                break;
                case PROCESS_ORDER:  processOrder();
                break;
                case CREATE_INVOICE: createInvoice();
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
