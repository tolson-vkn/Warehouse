import java.util.*;
import java.text.*;
import java.io.*;

public class WareContext {
    private static Warehouse warehouse;
    private static WareContext context;

    private int currentState;
    private int currentUser;
    private int lastState;
    private String userID;
    private boolean routeBack = false;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static final int isClerk    = 0;
    public static final int isUser     = 1;
    public static final int isManager  = 3;
    private WareState[] states;
    private int[][] nextState;

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

    private void retrieve() {
        try {
            Warehouse tempWarehouse = Warehouse.retrieve();
            if (tempWarehouse != null) {
                System.out.println(" The warehouse has been successfully retrieved from the file WarehouseData\n");
                warehouse = Warehouse.instance();
            }
            else {
                System.out.println("File does not exist; creating new warehouse");
                warehouse = Warehouse.instance();
            }
        }
        catch (Exception cnfe) {
            cnfe.printStackTrace();
        }
    }

    public void setLogin(int code) {
        currentUser = code;
    }

    public int getLogin() {
        return currentUser;
    }

    public void setUser(String uID) {
        userID = uID;
    }

    public String getUser() {
        return userID;
    }

    private WareContext() {
        if (yesOrNo("Look for saved data and use it?")) {
            retrieve();
        }
        else {
            warehouse = Warehouse.instance();
        }
        states = new WareState[6];
        states[0] = Clerkstate.instance();
        states[1] = Userstate.instance();
        states[2] = Loginstate.instance();
        states[3] = Mgnrstate.instance();
        states[4] = Shipstate.instance();
        states[5] = Querystate.instance();
        nextState = new int[6][6];
        nextState[0][0] = 2;  nextState[0][1] = 1;  nextState[0][2] =  2; nextState[0][3] =  3;  nextState[0][4] = 4;  nextState[0][5] = 5;
        nextState[1][0] = 2;  nextState[1][1] = 0;  nextState[1][2] = -2; nextState[1][3] =  3;  nextState[1][4] = -2; nextState[1][5] = 5;
        nextState[2][0] = 0;  nextState[2][1] = 1;  nextState[2][2] = -1; nextState[2][3] =  3;  nextState[2][4] = -2; nextState[2][5] = -2;
        nextState[3][0] = 0;  nextState[3][1] = 1;  nextState[3][2] =  2; nextState[3][3] = -2;  nextState[3][4] = 4;  nextState[3][5] = -2;
        nextState[4][0] = 0;  nextState[4][1] = -2; nextState[4][2] = -2; nextState[4][3] =  3;  nextState[4][4] = -2; nextState[4][5] = -2;
        nextState[5][0] = 0;  nextState[5][1] =  1; nextState[5][2] = -2; nextState[5][3] =  3;  nextState[5][4] = -2; nextState[5][5] = -2;
        currentState = 2;
    }

    public void changeState(int transition) {
        // System.out.print("currentState [" + currentState + "] -> transition [" + transition + "]");
        currentState = nextState[currentState][transition];
        // System.out.println(" = " + currentState);
        if (currentState == -2) {
            System.out.println("Error [" + currentState + "] has occured");
            terminate();
        }
        else if (currentState == -1) {
            terminate();
        }
        states[currentState].run();
    }

    private void terminate() {
        if (yesOrNo("Save data?")) {
            if (warehouse.save()) {
                System.out.println(" The warehouse has been successfully saved in the file WarehouseData\n");
            }
            else {
                System.out.println(" There has been an error in saving\n");
            }
        }
        System.exit(0);
    }

    public static WareContext instance() {
        if (context == null) {
            context = new WareContext();
        }
        return context;
    }

    public void process() {
        states[currentState].run();
    }

    public static void main(String[] args) {
        WareContext.instance().process();
    }
}
