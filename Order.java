import java.util.*;
import java.io.*;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    private Client client;
    private String id;
    private List orderItems = new LinkedList();
    private static final String ORDER_STRING = "O";

    public Order(Client client) {
        this.client = client;
        id = ORDER_STRING + (OrderIDServer.instance()).getID();
    }

    public boolean addOrderItem(OrderItem orderitem) {
        return orderItems.add(orderitem);
    }

    public boolean removeOrderItem(String orderitemID) {
        for (ListIterator iterator = orderItems.listIterator(); iterator.hasNext(); ) {
            OrderItem orderitem = (OrderItem) iterator.next();
            String id = orderitem.getID();
            if (id.equals(orderitemID)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public Client getClient() {
        return client;
    }

    public String getID() {
        return id;
    }

    public void setClient(Client newClient) {
        client = newClient;
    }

    public boolean equals(String id) {
        return this.id.equals(id);
    }

    public Iterator getOrderItems(){
        return orderItems.iterator();
    }

    public String toString() {
        String string = "Order " + id + "\n";
        for (Iterator iterator = orderItems.iterator(); iterator.hasNext(); ) {
            OrderItem orderitem = (OrderItem) iterator.next();
            string += "\t" + orderitem.toString() + "\n";
        }
        return string;
    }
}
