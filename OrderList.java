import java.util.*;
import java.io.*;

public class OrderList implements Serializable {
    private static final long serialVersionUID = 1L;
    private List orders = new LinkedList();
    private static OrderList orderList;

    private OrderList() {}

    public static OrderList instance() {
        if (orderList == null) {
            return (orderList = new OrderList());
        } else {
            return orderList;
        }
    }

    public boolean insertOrder(Order order) {
        orders.add(order);
        return true;
    }

    public Iterator getOrders(){
        return orders.iterator();
    }

    public Order search(String orderID) {
        for (Iterator iterator = orders.iterator(); iterator.hasNext(); ) {
            Order order = (Order) iterator.next();
            if (order.getID().equals(orderID)) {
                return order;
            }
        }
        return null;
    }

    private void writeObject(java.io.ObjectOutputStream output) {
        try {
            output.defaultWriteObject();
            output.writeObject(orderList);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
    private void readObject(java.io.ObjectInputStream input) {
        try {
            if (orderList != null) {
                return;
            }
            else {
                input.defaultReadObject();
                if (orderList == null) {
                    orderList = (OrderList) input.readObject();
                } else {
                    input.readObject();
                }
            }
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
        catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    public String toString() {
        return orders.toString();
    }
}
