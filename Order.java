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

    public Order(Client client, Product product, int quantity) {
        this.client = client;
        this.product = product;
        this.quantity = quantity;
        this.status = QUEUED;
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

    public boolean setStatus(String newStatus) {
        if (!(newStatus.equals(COMPLETED) || newStatus.equals(WAITLIST) || newStatus.equals(QUEUED))) {
            status = newStatus;
            return true;
        }
        else {
            return false;
        }
    }

    public boolean equals(String id) {
        return this.id.equals(id);
    }

    public String toString() {
        String string = "Order [" + id + "]:\nClient: " + client + "\n\t" + product + "\nQuantity to Order: " + quantity;
        return string;
    }
}
