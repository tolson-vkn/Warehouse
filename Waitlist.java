import java.util.*;
import java.io.*;

public class Waitlist implements Serializable {
    private static final long serialVersionUID = 1L;
    private Client client;
    private Product product;
    private int quantity;
    private String id;
    private static final String WAITLIST_STRING = "W";

    public Waitlist(Client client, Product product, int quantity) {
        this.client = client;
        this.product = product;
        this.quantity = quantity;
        id = WAITLIST_STRING + (WaitlistIDServer.instance()).getID();
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

    public boolean equals(String id) {
        return this.id.equals(id);
    }

    public String toString() {
        String string = "Waitlist [" + id + "]:\n" + product + "\nQuantity to Waitlist: " + quantity;
        return string;
    }
}
