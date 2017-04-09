import java.util.*;
import java.text.*;
import java.io.*;

public class MgnrState extends WareState {
    private static Warehouse warehouse;
    private static MgnrState instance;

    private WareContext context;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private static final int EXIT            = 0;
    private static final int SHIPMENT_MODE   = 1;
    private static final int CLERK_MODE      = 2;
    private static final int USER_MODE       = 3;
    private static final int ADD_CLIENT      = 4;
    private static final int ADD_SUPPLIER    = 5;
    private static final int ADJUST_PRICE    = 6;
    private static final int MENU            = 10;

    public SecurityLayer sl = new SecurityLayer();

    private MgnrState() {
        super();
        warehouse = Warehouse.instance();
    }

    public static MgnrState instance() {
        if (instance == null) {
            instance = new MgnrState();
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
                    if (value == EXIT) {
                        return value;
                    }
                    else if (validate()) {
                        return value;
                    }
                    else {
                        return EXIT;
                    }
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
         + "                      MANAGER\n\n"
         + "       +--------------------------------------+\n"
         + "       | " + SHIPMENT_MODE  + ")\tEnter Shipment Mode           |\n"
         + "       | " + CLERK_MODE     + ")\tEnter Clerk Mode              |\n"
         + "       | " + USER_MODE      + ")\tEnter Client Mode             |\n"
         + "       | " + ADD_CLIENT     + ")\tAdd Client                    |\n"
         + "       | " + ADD_SUPPLIER   + ")\tAdd Supplier                  |\n"
         + "       | " + ADJUST_PRICE   + ")\tAdjust Price                  |\n"
         + "       | " + MENU           + ")\tDisplay Menu                  |\n"
         + "       | " + EXIT           + ")\tExit                          |\n"
         + "       +--------------------------------------+\n");
    }

    public void shipmentMode() {
        (WareContext.instance()).changeState(4);
    }

    public void clerkmenu() {
        (WareContext.instance()).changeState(0);
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

    public void adjustPrice() {
        String productID = getToken("Enter product ID: ");
        Product productObj;
        Product result;
        productObj = warehouse.searchProduct(productID);
        if (productObj == null) {
            System.out.println("Product does not exist.");
            return;
        }
        System.out.println("Product [" + productObj.getProdName() + "] currently priced"
                         + " at $" + productObj.getPrice());
        Double newPrice = getDouble("New price: $");
        result = warehouse.adjustPrice(productID, newPrice);
        if (result == null) {
            System.out.println("Could not adjust price.");
        }
        else {
            System.out.println("Price adjusted!");
        }
    }

    public void logout() {
        if ((WareContext.instance()).getLogin() == WareContext.isManager) {
            (WareContext.instance()).changeState(2);
        }
    }

    public boolean validate() {
        String password = getToken("Enter manager password: ");
        if (sl.validateManager(password)) {
            System.out.println("Authenticated...");
            return true;
        }
        else {
            System.out.println("Invalid password. - logging out.");
            return false;
        }
    }

    public void process() {
        int command;
        menu();
        while ((command = getCommand()) != EXIT) {
            switch (command) {
                case SHIPMENT_MODE: shipmentMode();
                break;
                case USER_MODE:     usermenu();
                break;
                case CLERK_MODE:    clerkmenu();
                break;
                case ADD_CLIENT:    addClient();
                break;
                case ADD_SUPPLIER:  addSupplier();
                break;
                case ADJUST_PRICE:  adjustPrice();
                break;
                case MENU:          menu();
                break;
            }
        }
        logout();
    }

    public void run() {
        process();
    }
}
