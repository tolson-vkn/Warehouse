import java.util.*;
import java.lang.*;
import java.io.*;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private String prodName;
    private String quantity;
    private String price;
    private String id;
    private static final String PRODUCT_STRING = "P";

    public Product(String prodName, String quantity, String price) {
        this.prodName = prodName;
        this.quantity = quantity;
        this.price = price;
        id = PRODUCT_STRING + (ProductIDServer.instance()).getID();
    }

    public String getProdName() {
        return prodName;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    public String getID() {
        return id;
    }

    public String toString() {
        return "Product: " + prodName + " ID: " + id + " Price: $" + price + " Qty: " + quantity;

    }
}
