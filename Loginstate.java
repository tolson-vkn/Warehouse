import java.util.*;
import java.text.*;
import java.io.*;

public class Loginstate extends WareState {
    private static final int EXIT        = 0;
    private static final int CLERK_LOGIN = 1;
    private static final int USER_LOGIN  = 2;
    private static final int MGNR_LOGIN  = 3;
    private static Loginstate instance;

    private WareContext context;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public SecurityLayer sl = new SecurityLayer();

    private Loginstate() {
        super();
    }

    public static Loginstate instance() {
        if (instance == null) {
            instance = new Loginstate();
        }
        return instance;
    }

    public int getCommand() {
        do {
            try {
                int value = Integer.parseInt(getToken("> "));
                if (value >= EXIT && value <= MGNR_LOGIN) {
                    return value;
                }
            }
            catch (NumberFormatException nfe) {
                System.out.println("Enter a number");
            }
        } while (true);
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

    private boolean yesOrNo(String prompt) {
        String more = getToken(prompt + " (Y|y)[es] or anything else for no");
        if (more.charAt(0) != 'y' && more.charAt(0) != 'Y') {
            return false;
        }
        return true;
    }


    private void user(){
        String userID = getToken("Enter client user ID (e.g. C8)\n> ");
        if (Warehouse.instance().searchClient(userID) != null){
            (WareContext.instance()).setLogin(WareContext.isUser);
            (WareContext.instance()).setUser(userID);
            (WareContext.instance()).changeState(1);
        }
        else
        System.out.println("Invalid user id.");
    }

    private void clerk() {
        String password = getToken("Enter clerk password: ");
        if (sl.validateClerk(password)) {
            System.out.println("Authenticated...");
            (WareContext.instance()).setLogin(WareContext.isClerk);
            (WareContext.instance()).changeState(0);
        }
        else {
            System.out.println("Invalid password.");
        }
    }

    private void manager() {
        String password = getToken("Enter manager password: ");
        if (sl.validateManager(password)) {
            System.out.println("Authenticated...");
            (WareContext.instance()).setLogin(WareContext.isManager);
            (WareContext.instance()).changeState(3);
        }
        else {
            System.out.println("Invalid password.");
        }
    }

    public void loginMenu() {
        System.out.println(
           "                 Warehouse System\n"
         + "                     FSM LOGIN\n\n"
         + "       +--------------------------------------+\n"
         + "       | " + CLERK_LOGIN + ")\tClerk                         |\n"
         + "       | " + USER_LOGIN  + ")\tClient                        |\n"
         + "       | " + MGNR_LOGIN  + ")\tManager                       |\n"
         + "       | " + EXIT        + ")\tExit                          |\n"
         + "       +--------------------------------------+\n");
    }

    public void process() {
        int command;
        loginMenu();
        while ((command = getCommand()) != EXIT) {
            switch (command) {
                case USER_LOGIN:        user();
                break;
                case CLERK_LOGIN:       clerk();
                break;
                case MGNR_LOGIN:        manager();
                break;
                default:                System.out.println("Invalid choice");

            }
            loginMenu();
        }
        (WareContext.instance()).changeState(2);
    }

    public void run() {
        process();
    }
}
