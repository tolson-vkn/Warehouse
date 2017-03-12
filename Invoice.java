import java.util.*;
import java.io.*;

public class Invoice implements Serializable {
    private static final long serialVersionUID = 1L;
    private Order order;
    private String mmddyy;
    private Calendar date;
    private List invoiceItems = new LinkedList();

    public Invoice(Order order) {
        this.order = order;
        date = new GregorianCalendar();
        date.setTimeInMillis(System.currentTimeMillis());
        mmddyy = getDate();
    }

    public boolean addInvoiceItem(InvoiceItem invoiceitem) {
        return invoiceItems.add(invoiceitem);
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order newOrder) {
        order = newOrder;
    }

    public String getDate() {
        return date.get(Calendar.MONTH) + "/" + date.get(Calendar.DATE) + "/" + date.get(Calendar.YEAR);
    }

    public double moneyRound(double num) {
        return Math.round(num * 100.0) / 100.0;
    }

    public String toString() {
        double orderTotal = 0;
        String string;
        string = "----------------------\n"
               + "Invoice date: " + mmddyy + "\nFrom order: " + order.getID() + "\n";
        for (Iterator iterator = invoiceItems.iterator(); iterator.hasNext(); ) {
            InvoiceItem invoiceitem = (InvoiceItem) iterator.next();
            orderTotal += invoiceitem.getTotal();
            string += invoiceitem.toString() + "\n";
        }

        orderTotal = moneyRound(orderTotal);
        string += "----------------------\n"
                + "Total: $" + orderTotal + "\n"
                + "----------------------\n";
        return string;
    }
}
