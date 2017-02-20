import java.util.*;
import java.io.*;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    private Client client;
    private Product product;
    private int quantity;
    private String id;
    private String status;
    private static final String ORDER_STRING = "O";

    private static final String COMPLETED = "C";
    private static final String WAITLIST  = "W";
    private static final String QUEUED    = "Q";

    public Order(Client client, Product product, int quantity, String status) {
        this.client = client;
        this.product = product;
        this.quantity = quantity;
        this.status = statusCheck(status) ? status : QUEUED;
        id = ORDER_STRING + (OrderIDServer.instance()).getID();
    }

    public Client getClient() {
        return client;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public String getID() {
        return id;
    }

    public void setClient(Client newClient) {
        client = newClient;
    }

    public void setProduct(Product newProduct) {
        product = newProduct;
    }

    public void setQuantity(int newQuantity) {
        quantity = newQuantity;
    }

    public boolean statusCheck(String isStatus) {
        if (isStatus.equals(COMPLETED) || isStatus.equals(WAITLIST) || isStatus.equals(QUEUED)) {
            return true;
        }
        else {
            return false;
        }
    }

    public void setStatus(String newStatus) {
        status = statusCheck(newStatus) ? newStatus : status;
    }

    public boolean equals(String id) {
        return this.id.equals(id);
    }

    public String toString() {
        String string = "Order [" + id + "]:\n" + product + "\nQuantity to Order: " + quantity + "\nStatus: " + status;
        return string;
    }
}
