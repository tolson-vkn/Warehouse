import java.util.*;
import java.io.*;

public class InvoiceItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private Product product;
    private int quantity;
    private String status;
    private double total;
    private String verboseStatus;

    private static final String PENDING  = "P";
    private static final String COMPLETE = "C";
    private static final String WAITLIST = "W";

    public InvoiceItem(Product product, int quantity, String status, double total) {
        this.product = product;
        this.quantity = quantity;
        this.status = status;
        this.total = moneyRound(total);
        setVerboseStatus();
    }

    public String toString() {
        String string = "    Product:   [" + product.getProdName() + "]\n"
                      + "       Cost:   "  + quantity + " x $" + product.getPrice() + " = $" + total + "\n"
                      + "       Note:   "  + verboseStatus;
        return string;
    }

    public void setVerboseStatus() {
        if (status.equals("W")) {
            verboseStatus = "Product waitlisted!";
            total = 0;
        }
        else if (status.equals("C")) {
            verboseStatus = "Product fullfilled.";
        }
    }

    public double getTotal() {
        return total;
    }

    public double moneyRound(double num) {
        return Math.round(num * 100.0) / 100.0;
    }
}
