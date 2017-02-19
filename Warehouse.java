import java.util.*;
import java.io.*;

public class Warehouse implements Serializable {
    private static final long serialVersionUID  = 1L;
    private ProductList productList;
    private ClientList clientList;
    private SupplierList supplierList;
    private OrderList orderList;
    private static Warehouse warehouse;

    // Instantiate the lists of objects.
    private Warehouse() {
        productList  = ProductList.instance();
        clientList   = ClientList.instance();
        supplierList = SupplierList.instance();
        orderList    = OrderList.instance();
    }

    // Get the server singleton instantiations.
    public static Warehouse instance() {
        if (warehouse == null) {
            ClientIDServer.instance();
            ProductIDServer.instance();
            SupplierIDServer.instance();
            OrderIDServer.instance();
            return (warehouse = new Warehouse());
        }
        else {
            return warehouse;
        }
    }

    // Add a product to the warehouse.
    public Product addProduct(String prodName, int quantity, float price) {
        Product product = new Product(prodName, quantity, price);
        if (productList.insertProduct(product)) {
            return (product);
        }
        return null;
    }

    // Add a client to the warehouse.
    public Client addClient(String name, String address, String phone) {
        Client client = new Client(name, address, phone);
        if (clientList.insertClient(client)) {
            return (client);
        }
        return null;
    }

    // Add a supplier to the warehouse.
    public Supplier addSupplier(String name, String address, String phone) {
        Supplier supplier = new Supplier(name, address, phone);
        if (supplierList.insertSupplier(supplier)) {
            return (supplier);
        }
        return null;
    }

    // Assign a product to a supplier.
    public Product linkProduct(String supplierID, String productID) {

        // Check if product with ID exists.
        Product product = productList.search(productID);
        if (product == null) {
            return null;
        }

        // Check if supplier with ID exists.
        Supplier supplier = supplierList.search(supplierID);
        if (supplier == null) {
            return null;
        }

        // Both objects exist, attempt to link them.
        if (!(product.link(supplier) && supplier.link(product))) {
            return null;
        }

        return product;
    }

    // Unasign a product to a supplier.
    public Product unlinkProduct(String supplierID, String productID) {

        // Check if product with ID exists.
        Product product = productList.search(productID);
        if (product == null) {
            return null;
        }

        // Check if supplier with ID exists.
        Supplier supplier = supplierList.search(supplierID);
        if (supplier == null) {
            return null;
        }

        // Both exist, attempt to unlink them.
        if (!(product.unlink(supplier) && supplier.unlink(product))) {
            return null;
        }

        return product;
    }

    // Add a order to the warehouse.
    public Order addOrder(Client client, Product product, int quantity) {
        Order order = new Order(client, product, quantity);
        if (orderList.insertOrder(order)) {
            return (order);
        }
        return null;
    }

    // Process an order existing
    public Order processOrder(String orderID) {
        Order order = orderList.search(orderID);
        if (order == null) {
            return null;
        }
        Client client = order.getClient();
        Product product = order.getProduct();
        float pricePerUnit = product.getPrice();
        int productQty = product.getQuantity();
        int orderQty = order.getQuantity();

        // check if order can proceed. later waitlist will handle other conditions.
        if (orderQty > productQty) {
            return null;
        }

        // Adjust client balance.
        float oldBalance = client.getBalance();
        float newBalance = orderQty * pricePerUnit;
        client.setBalance(oldBalance - newBalance);

        // Adjust warehouse stock.
        product.setQuantity(productQty - orderQty);
        return order;
    }

    public Client searchClient(String clientID) {
        return clientList.search(clientID);
    }

    public Supplier searchSupplier(String supplierID) {
        return supplierList.search(supplierID);
    }

    public Product searchProduct(String productID) {
        return productList.search(productID);
    }

    public Order searchOrder(String orderID) {
        return orderList.search(orderID);
    }

    public Iterator getProducts() {
        return productList.getProducts();
    }

    public Iterator getClients() {
        return clientList.getClients();
    }

    public Iterator getSuppliers() {
        return supplierList.getSuppliers();
    }

    public Iterator getOrders() {
        return orderList.getOrders();
    }

    public static Warehouse retrieve() {
        try {
            FileInputStream file = new FileInputStream("WarehouseData");
            ObjectInputStream input = new ObjectInputStream(file);
            input.readObject();
            ClientIDServer.retrieve(input);
            ProductIDServer.retrieve(input);
            SupplierIDServer.retrieve(input);
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
