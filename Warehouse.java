import java.util.*;
import java.io.*;

public class Warehouse implements Serializable {
    private static final long serialVersionUID  = 1L;
    private ProductList productList;
    private ClientList clientList;
    private static Warehouse warehouse;
    private Warehouse() {
        productList = ProductList.instance();
        clientList = ClientList.instance();
    }

    public static Warehouse instance() {
        if (warehouse == null) {
            ClientIDServer.instance(); // instantiate all singletons
            ProductIDServer.instance(); // instantiate all singletons
            // SupplierIDServer.instance(); // instantiate all singletons
            return (warehouse = new Warehouse());
        }
        else {
            return warehouse;
        }
    }

    public Product addProduct(String prodName, String quantity, String price) {
        Product product = new Product(prodName, quantity, price);
        if (productList.insertProduct(product)) {
            return (product);
        }
        return null;
    }

    public Client addClient(String name, String address, String phone) {
        Client client = new Client(name, address, phone);
        if (clientList.insertClient(client)) {
            return (client);
        }
        return null;
    }

    public Iterator getProducts() {
        return productList.getProducts();
    }

    public Iterator getClients() {
        return clientList.getClients();
    }

    public static Warehouse retrieve() {
        try {
            FileInputStream file = new FileInputStream("WarehouseData");
            ObjectInputStream input = new ObjectInputStream(file);
            input.readObject();
            ClientIDServer.retrieve(input);
            ProductIDServer.retrieve(input);
            // SupplierIDServerf.retrieve(input);
            return warehouse;
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
        catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            return null;
        }
    }

    public static boolean save() {
        try {
            FileOutputStream file = new FileOutputStream("WarehouseData");
            ObjectOutputStream output = new ObjectOutputStream(file);
            output.writeObject(warehouse);
            output.writeObject(ClientIDServer.instance());
            output.writeObject(ProductIDServer.instance());
            output.writeObject(SupplierIDServer.instance());
            return true;
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }

    private void writeObject(java.io.ObjectOutputStream output) {
        try {
            output.defaultWriteObject();
            output.writeObject(warehouse);
        }
        catch(IOException ioe) {
            System.out.println(ioe);
        }
    }

    private void readObject(java.io.ObjectInputStream input) {
        try {
            input.defaultReadObject();
            if (warehouse == null) {
                warehouse = (Warehouse) input.readObject();
            }
            else {
                input.readObject();
            }
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return productList + "\n" + clientList;
    }
}
