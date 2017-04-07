import java.util.*;
import java.text.*;
import java.io.*;

// Terrifyingly basic...
public class SecurityLayer {
    private String managerPassword = "manager";
    private String clerkPassword   = "clerk";

    SecurityLayer() {}

    public boolean validateManager(String password) {
        return managerPassword.equals(password);
    }

    public boolean validateClerk(String password) {
        return clerkPassword.equals(password);
    }
}
