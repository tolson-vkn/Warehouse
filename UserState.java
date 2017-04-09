import java.util.*;
import java.text.*;
import java.io.*;

public class UserState extends WareState {
    private static UserState userstate;
    private static Warehouse warehouse;

    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private static final int EXIT             = 0;
    private static final int OPS_MODE         = 1;
    private static final int QUERY_MODE       = 2;
    private static final int ACCEPT_ORDER     = 3;
    private static final int PROCESS_ORDER    = 4;
    private static final int CREATE_INVOICE   = 5;
    private static final int PAYMENT          = 6;
    private static final int MENU             = 10;

    private UserState() {
        warehouse = Warehouse.instance();
    }

    public static UserState instance() {
        if (userstate == null) {
            return userstate = new UserState();
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

    public void userDetails() {
        Client clientObj;
        double balance;
        String balanceDetails;
        String clientID = (WareContext.instance()).getUser();
        if (clientID == null) {
            return;
        }
        clientObj = warehouse.searchClient(clientID);
        balance = clientObj.getBalance();
        if (balance < 0) {
            balanceDetails = "\u001B[31m" + "-$" + Math.abs(balance) + "\u001B[0m";
        }
        else {
            balanceDetails = "\u001B[32m" + "$" + balance + "\u001B[0m";
        }
        String details = "       Client:  " + clientObj.getName() + " ["
                       + clientObj.getID() + "]\n"
                       + "       Address: " + clientObj.getAddress() + "\n"
                       + "       Phone:   " + clientObj.getPhone() + "\n"
                       + "       Balance: " + balanceDetails + "\n";
        System.out.print(details);
    }

    // Menu of warehouse options.
    public void menu() {
        System.out.print(
           "                 Warehouse System\n"
         + "                      CLIENT\n\n");
        userDetails();
        System.out.println(
           "       +--------------------------------------+\n"
         + "       | " + OPS_MODE         + ")\tEnter Client Ops              |\n"
         + "       | " + ACCEPT_ORDER     + ")\tOrder Product                 |\n"
         + "       | " + PAYMENT          + ")\tMake a payment                |\n"
         + "       | " + MENU             + ")\tDisplay Menu                  |\n"
         + "       | " + EXIT             + ")\tExit                          |\n"
         + "       +--------------------------------------+\n");
    }

    public void clientOpsMode() {
        (WareContext.instance()).changeState(6);
    }

    public void acceptOrder() {
        Client clientObj;
        Product productObj;
        Order orderObj;
        OrderItem orderItemOjb;

        String clientID = (WareContext.instance()).getUser();
        if (clientID == null) {
            System.out.println("Could not show Transactions.");
            return;
        }

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

    public void payment() {
        Client result;
        String clientID = (WareContext.instance()).getUser();
        if (clientID == null) {
            System.out.println("Could not show Transactions.");
            return;
        }

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
                case OPS_MODE:       clientOpsMode();
                break;
                case ACCEPT_ORDER:   acceptOrder();
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
