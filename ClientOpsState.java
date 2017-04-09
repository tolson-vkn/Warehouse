import java.util.*;
import java.text.*;
import java.io.*;

public class ClientOpsState extends WareState {
    private static Warehouse warehouse;
    private static ClientOpsState instance;

    private WareContext context;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private static final int EXIT            = 0;
    private static final int SHOW_PRODUCTS   = 1;
    private static final int SHOW_ORDERS     = 2;
    private static final int SHOW_WAITLIST   = 3;
    private static final int GET_TRANS       = 4;
    private static final int GET_INVOICE     = 5;
    private static final int MENU            = 10;

    private ClientOpsState() {
        super();
        warehouse = Warehouse.instance();
    }

    public static ClientOpsState instance() {
        if (instance == null) {
            instance = new ClientOpsState();
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
         + "                     CLIENT OPS\n\n");
        userDetails();
        System.out.println(
           "       +--------------------------------------+\n"
         + "       | " + SHOW_PRODUCTS    + ")\tShow Products                 |\n"
         + "       | " + SHOW_ORDERS      + ")\tShow Orders                   |\n"
         + "       | " + SHOW_WAITLIST    + ")\tShow Waitlist                 |\n"
         + "       | " + GET_TRANS        + ")\tShow Transactions             |\n"
         + "       | " + GET_INVOICE      + ")\tShow Invoices                 |\n"
         + "       | " + MENU             + ")\tDisplay Menu                  |\n"
         + "       | " + EXIT             + ")\tExit                          |\n"
         + "       +--------------------------------------+\n");
    }

    public void showProducts() {
        Iterator allProducts = warehouse.getProducts();
        while (allProducts.hasNext()){
            Product product = (Product)(allProducts.next());
            System.out.println(product.toString());
        }
    }

    public void showOrders() {
        Iterator allOrders = warehouse.getOrders();
        String clientID = (WareContext.instance()).getUser();
        if (clientID == null) {
            System.out.println("Could not show Orders.");
            return;
        }
        while (allOrders.hasNext()) {
            Order order = (Order)(allOrders.next());
            String orderClientID = order.getClient().getID();
            if (clientID.equals(orderClientID)) {
                System.out.println(order.toString());
            }
        }
    }

    public void showWaitlist() {
        Iterator result;

        String clientID = (WareContext.instance()).getUser();
        if (clientID == null) {
            System.out.println("Could not show Transactions.");
            return;
        }
        result = warehouse.getClientWaitlistOrders(clientID);
        if (result == null) {
            System.out.println("Client does not Waitlist Orders.");
        }
        else {
            System.out.println("Begin waitlist listing.\n");
            while (result.hasNext()) {
                Waitlist waitlist = (Waitlist) result.next();
                System.out.println(waitlist.toString());
            }
            System.out.println("\nThere are no more waitlist orders.\n");
        }
    }

    public void getTransactions() {
        Iterator result;

        String clientID = (WareContext.instance()).getUser();
        if (clientID == null) {
            System.out.println("Could not show Transactions.");
            return;
        }
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

        String clientID = (WareContext.instance()).getUser();
        if (clientID == null) {
            System.out.println("Could not show Invoices.");
            return;
        }
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

    public void logout() {
        (WareContext.instance()).changeState(1);
    }

    public void process() {
        int command;
        menu();
        while ((command = getCommand()) != EXIT) {
            switch (command) {
                case SHOW_PRODUCTS:   showProducts();
                break;
                case SHOW_ORDERS:     showOrders();
                break;
                case SHOW_WAITLIST:   showWaitlist();
                break;
                case GET_TRANS:       getTransactions();
                break;
                case GET_INVOICE:     getInvoices();
                break;
                case MENU:            menu();
                break;
            }
        }
        logout();
    }

    public void run() {
        process();
    }
}
