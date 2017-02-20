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
    public Product addProduct(String prodName, int quantity, double price) {
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
    public Order addOrder(Client client, Product product, int quantity, String status) {
        Order order = new Order(client, product, quantity, status);
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
        double pricePerUnit = product.getPrice();
        int productQty = product.getQuantity();
        int orderQty = order.getQuantity();

        int newWareHouseQty = productQty - orderQty;

        // Order excedes warehouse stock.
        if (newWareHouseQty < 0) {
            int waitlistQty = Math.abs(newWareHouseQty);
            orderQty = orderQty - waitlistQty;
            order.setQuantity(orderQty);
            Order waitlistOrder = new Order(client, product, waitlistQty, "W");
            if (!orderList.insertOrder(waitlistOrder)) {
                return null;
            }
            newWareHouseQty = 0;
        }
        else {
            newWareHouseQty = productQty - orderQty;
        }

        // Adjust client balance.
        double oldBalance = client.getBalance();
        double newBalance = orderQty * pricePerUnit;
        client.setBalance(oldBalance - newBalance);

        // Adjust warehouse stock.
        product.setQuantity(newWareHouseQty);

        // Completed, set status.
        order.setStatus("C");
        return order;
    }

    public boolean needsPayment(String clientID) {
        Client client = clientList.search(clientID);
        if (client == null) {
            return false;
        }

        return (client.getBalance() < 0) ? true : false;
    }

    public Client makePayment(String clientID, double payment) {
        Client client = clientList.search(clientID);
        if (client == null) {
            return null;
        }

        double oldBalance = client.getBalance();
        if (payment < 0 || payment > Math.abs(oldBalance)) {
            return null;
        }

        double newBalance = oldBalance + payment;

        client.setBalance(newBalance);
        return client;
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
