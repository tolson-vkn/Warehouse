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
            WaitlistIDServer.instance();
            OrderItemIDServer.instance();
            return (warehouse = new Warehouse());
        }
        else {
            return warehouse;
        }
    }

    // Add a product to the warehouse.
    public Product addProduct(String prodName, double price) {
        Product product = new Product(prodName, price);
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

    public Product adjustPrice(String productID, double newPrice) {
        Product product = productList.search(productID);
        if (product == null) {
            return null;
        }

        product.setPrice(newPrice);
        return product;
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

    public boolean isLinked(String supplierID, String productID) {
        // Check if supplier with ID exists.
        Supplier supplier = supplierList.search(supplierID);
        if (supplier == null) {
            return false;
        }

        // Check if product with ID exists.
        Product product = productList.search(productID);
        if (product == null) {
            return false;
        }

        return (product.isLinked(supplierID) && supplier.isLinked(productID));
    }

    public Product addShipment(String supplierID, String productID, int quantity) {
        // Check if supplier with ID exists.
        Supplier supplier = supplierList.search(supplierID);
        if (supplier == null) {
            return null;
        }

        // Check if product with ID exists.
        Product product = productList.search(productID);
        if (product == null) {
            return null;
        }

        // Check if products are offered by the supplier.
        if(!(product.isLinked(supplierID) && supplier.isLinked(productID))) {
            return null;
        }

        product.addQuantity(quantity);

        return product;
    }

    public Waitlist processWaitlist(String productID, String waitlistID, int shipmentQty) {
        // System.out.println("\u001B[33m" + "in processWaitlist" + "\u001B[37m");
        Product product = productList.search(productID);
        if (product == null) {
            return null;
        }
        int productQty = product.getQuantity();
        double pricePerUnit = product.getPrice();

        Waitlist waitlist = product.searchWaitlist(waitlistID);
        if (waitlist == null) {
            return null;
        }
        // System.out.println("\u001B[33m" + "given WID " + waitlistID + " from iterator WID " + waitlist.getID() + "\u001B[37m");
        int waitlistQty = waitlist.getQuantity();

        // Can fullfill part of order.
        if ((productQty + shipmentQty) >= waitlistQty) {
            int temp = waitlistQty - productQty;
            shipmentQty = shipmentQty - (waitlistQty - productQty);
            double oldBalance = waitlist.getClient().getBalance();
            double newBalance = waitlistQty * pricePerUnit;
            waitlist.getClient().setBalance(oldBalance - newBalance);

            // product.removeWaitlist(waitlistID);
            // System.out.println("\u001B[33m" + "pre return waitlist" + "\u001B[37m");
            return waitlist;
        }

        return null;
    }

    // Add a order to the warehouse.
    public Order createOrder(String clientID) {
        // Check if client with ID exists.
        Client client = clientList.search(clientID);
        if (client == null) {
            return null;
        }

        Order order = new Order(client);
        if (orderList.insertOrder(order)) {
            return order;
        }
        return null;
    }

    // Add a order to the warehouse.
    public OrderItem addToOrder(String orderID, String productID, int quantity) {
        // Check if order with ID exists.
        Order order = orderList.search(orderID);
        if (order == null) {
            return null;
        }

        Product product = productList.search(productID);
        if (product == null) {
            return null;
        }

        OrderItem orderitem = new OrderItem(product, quantity);
        if (order.addOrderItem(orderitem)) {
            return orderitem;
        }
        return null;
    }

    // // Process an order existing
    public Order processOrder(String orderID) {
        Order order = orderList.search(orderID);
        if (order == null) {
            return null;
        }
        Client client = order.getClient();


        for (Iterator iterator = order.getOrderItems();  iterator.hasNext();) {
            OrderItem orderitem = (OrderItem) iterator.next();
            if (!orderitem.isPending()) {
                continue;
            }
            Product product = orderitem.getProduct();
            double pricePerUnit = product.getPrice();
            int productQty = product.getQuantity();
            int orderQty = orderitem.getQuantity();

            int newWareHouseQty = productQty - orderQty;

            if (newWareHouseQty < 0) {
                Waitlist waitlistOrder = new Waitlist(client, product, orderQty);
                if (client.addWaitlist(waitlistOrder) && product.addWaitlist(waitlistOrder)) {
                    orderitem.setStatusW();
                }
            }
            else {
                // Adjust client balance.
                double oldBalance = client.getBalance();
                double newBalance = orderQty * pricePerUnit;
                client.setBalance(oldBalance - newBalance);
                orderitem.setStatusC();

                // Adjust warehouse stock.
                product.setQuantity(newWareHouseQty);
            }
        }
        return order;
    }

    public Invoice createInvoice(String orderID) {
        Order order = orderList.search(orderID);
        if (order == null) {
            return null;
        }

        Client client = order.getClient();
        Invoice invoice = new Invoice(order);

        for (Iterator iterator = order.getOrderItems();  iterator.hasNext();) {
            OrderItem orderitem = (OrderItem) iterator.next();
            if (orderitem.isPending()) {
                return null;
            }
            Product product = orderitem.getProduct();
            double pricePerUnit = product.getPrice();
            int orderQty = orderitem.getQuantity();
            double orderPrice = orderQty * pricePerUnit;

            InvoiceItem invoiceitem = new InvoiceItem(product, orderQty, orderitem.getStatus(), orderPrice);
            invoice.addInvoiceItem(invoiceitem);

        }

        if (!client.addInvoice(invoice)) {
            return null;
        }

        return invoice;
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

        client.makePayment(newBalance);
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

    public Iterator getClientWaitlistOrders(String clientID) {
        Client client = clientList.search(clientID);
        if (client == null) {
            return null;
        }

        return client.getWaitlistOrders();
    }

    public Iterator getWaitlistOrders(String productID) {
        Product product = productList.search(productID);
        return product.getWaitlistOrders();
    }

    public Iterator getInvoices(String clientID) {
        Client client = clientList.search(clientID);
        if (client == null) {
            return null;
        }
        return client.getInvoices();
    }

    public Iterator getTransactions(String clientID) {
        Client client = clientList.search(clientID);
        if (client == null) {
            return null;
        }
        return client.getTransactions();
    }

    public static Warehouse retrieve() {
        try {
            FileInputStream file = new FileInputStream("WarehouseData");
            ObjectInputStream input = new ObjectInputStream(file);
            input.readObject();
            ClientIDServer.retrieve(input);
            ProductIDServer.retrieve(input);
            SupplierIDServer.retrieve(input);
            OrderIDServer.retrieve(input);
            WaitlistIDServer.retrieve(input);
            OrderItemIDServer.retrieve(input);
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
            output.writeObject(OrderIDServer.instance());
            output.writeObject(WaitlistIDServer.instance());
            output.writeObject(OrderItemIDServer.instance());
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
