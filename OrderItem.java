import java.util.*;
import java.io.*;

public class OrderItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private Product product;
    private int quantity;
    private String id;
    private String status;
    private static final String ORDERITEM_STRING = "IT";
    private static final String PENDING  = "P";
    private static final String COMPLETE = "C";
    private static final String WAITLIST = "W";

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.status = PENDING;
        id = ORDERITEM_STRING + (OrderItemIDServer.instance()).getID();
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getID() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public boolean isPending() {
        return getStatus().equals(PENDING) ? true : false;
    }

    public boolean isCompleted() {
        return getStatus().equals(COMPLETE) ? true : false;
    }

    public boolean isWaitlisted() {
        return getStatus().equals(WAITLIST) ? true : false;
    }

    public void setStatusW() {
        status = WAITLIST;
    }

    public void setStatusC() {
        status = COMPLETE;
    }

    public void setProduct(Product newProduct) {
        product = newProduct;
    }

    public void setQuantity(int newQuantity) {
        quantity = newQuantity;
    }

    public boolean equals(String id) {
        return this.id.equals(id);
    }

    public String toString() {
        String string = "Product: " + product.getProdName() + " Quantity: " + quantity + " Status: " + status;
        return string;
    }
}
