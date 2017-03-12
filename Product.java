import java.util.*;
import java.lang.*;
import java.io.*;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private String prodName;
    private int quantity;
    private double price;
    private String id;
    private List productSuppliers = new LinkedList();
    private List waitlistOrders = new LinkedList();
    private static final String PRODUCT_STRING = "P";

    // NOTE: Final design won't have the contructor list quantity, this comes from
    //       shipments in development stage 3.
    public Product(String prodName, double price) {
        this.prodName = prodName;
        this.price = moneyRound(price);
        quantity = 0;
        id = PRODUCT_STRING + (ProductIDServer.instance()).getID();
    }

    public boolean addWaitlist(Waitlist waitlist) {
        return waitlistOrders.add(waitlist);
    }

    public boolean removeWaitlist(String waitlistID) {
        for (ListIterator iterator = waitlistOrders.listIterator(); iterator.hasNext(); ) {
            Waitlist waitlist = (Waitlist) iterator.next();
            String id = waitlist.getID();
            if (id.equals(waitlistID)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public String getProdName() {
        return prodName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getID() {
        return id;
    }

    public Iterator getWaitlistOrders() {
        return waitlistOrders.iterator();
    }


    public void setQuantity(int newQuantity) {
        quantity = newQuantity;
    }

    // Used to check equality in searching
    public boolean equals(String id) {
        return this.id.equals(id);
    }

    public void addQuantity(int addQuantity) {
        quantity += addQuantity;
    }

    // Assign relationship between products and suppliers.
    public boolean link(Supplier supplier) {
        return productSuppliers.add(supplier) ? true : false;
    }

    // Unassign relationship between products and suppliers.
    public boolean unlink(Supplier supplier) {
        return productSuppliers.remove(supplier) ? true : false;
    }

    public boolean isLinked(String supplierID) {
        for (Iterator iterator = productSuppliers.iterator(); iterator.hasNext(); ) {
            Supplier supplier = (Supplier) iterator.next();
            if (supplier.getID().equals(supplierID)) {
                return true;
            }
        }
        return false;
    }

    public double moneyRound(double num) {
        return Math.round(num * 100.0) / 100.0;
    }

    public String toString() {
        return "Product: " + prodName + " ID: " + id + " Price: $" + price + " Qty: " + quantity;

    }
}
