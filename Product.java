import java.util.*;
import java.lang.*;
import java.io.*;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private String prodName;
    private int quantity;
    private float price;
    private String id;
    private List productSuppliers = new LinkedList();
    private static final String PRODUCT_STRING = "P";

    // NOTE: Final design won't have the contructor list quantity, this comes from
    //       shipments in development stage 2.
    public Product(String prodName, int quantity, float price) {
        this.prodName = prodName;
        this.quantity = quantity;
        this.price = price;
        id = PRODUCT_STRING + (ProductIDServer.instance()).getID();
    }

    public String getProdName() {
        return prodName;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
    }

    public String getID() {
        return id;
    }

    public void setQuantity(int newQuantity) {
        quantity = newQuantity;
    }

    // Used to check equality in searching
    public boolean equals(String id) {
        return this.id.equals(id);
    }

    // Assign relationship between products and suppliers.
    public boolean link(Supplier supplier) {
        return productSuppliers.add(supplier) ? true : false;
    }

    // Unassign relationship between products and suppliers.
    public boolean unlink(Supplier supplier) {
        return productSuppliers.remove(supplier) ? true : false;
    }

    public String toString() {
        return "Product: " + prodName + " ID: " + id + " Price: $" + price + " Qty: " + quantity;

    }
}
