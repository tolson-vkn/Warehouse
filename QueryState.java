import java.util.*;
import java.text.*;
import java.io.*;

public class QueryState extends WareState {
    private static Warehouse warehouse;
    private static QueryState instance;

    private WareContext context;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private static final int EXIT            = 0;
    private static final int ACCEPT_SHIPMENT = 1;
    private static final int SHOW_CLIENTS    = 2;
    private static final int SHOW_PRODUCTS   = 3;
    private static final int SHOW_SUPPLIERS  = 4;
    private static final int SHOW_ORDERS     = 5;
    private static final int GET_TRANS       = 6;
    private static final int GET_INVOICE     = 7;
    private static final int MENU            = 10;

    private QueryState() {
        super();
        warehouse = Warehouse.instance();
    }

    public static QueryState instance() {
        if (instance == null) {
            instance = new QueryState();
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
         + "                    QUERY MODE\n\n"
         + "       +--------------------------------------+\n"
         + "       | " + SHOW_CLIENTS     + ")\tShow Clients                  |\n"
         + "       | " + SHOW_PRODUCTS    + ")\tShow Products                 |\n"
         + "       | " + SHOW_SUPPLIERS   + ")\tShow Suppliers                |\n"
         + "       | " + SHOW_ORDERS      + ")\tShow Orders                   |\n"
         + "       | " + GET_TRANS        + ")\tGet Transaction of a Client   |\n"
         + "       | " + GET_INVOICE      + ")\tGet Invoices of a Client      |\n"
         + "       | " + MENU             + ")\tDisplay Menu                  |\n"
         + "       | " + EXIT             + ")\tExit                          |\n"
         + "       +--------------------------------------+\n");
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

    public void logout() {
        if ((WareContext.instance()).getLogin() == WareContext.isClerk) {
            (WareContext.instance()).changeState(0);
        }
        else if ((WareContext.instance()).getLogin() == WareContext.isManager) {
            (WareContext.instance()).changeState((WareContext.instance()).getLastState());
        }
    }

    public void process() {
        int command;
        menu();
        while ((command = getCommand()) != EXIT) {
            switch (command) {
                case SHOW_CLIENTS:    showClients();
                break;
                case SHOW_PRODUCTS:   showProducts();
                break;
                case SHOW_SUPPLIERS:  showSuppliers();
                break;
                case SHOW_ORDERS:     showOrders();
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
