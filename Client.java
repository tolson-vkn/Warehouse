import java.util.*;
import java.io.*;

public class Client implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String address;
    private String phone;
    private String id;
    private double balance = 0;
    private static final String CLIENT_STRING = "C";
    private List transactions = new LinkedList();
    private List waitlistOrders = new LinkedList();
    private List orders = new LinkedList();
    private List invoices = new LinkedList();

    public Client(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        id = CLIENT_STRING + (ClientIDServer.instance()).getID();
        transactions.add(new Transaction("Client added to warehouse!"));
    }

    public boolean addInvoice(Invoice invoice) {
        transactions.add(new Transaction("Invoice added for " + invoice.getOrder().getID()));
        return invoices.add(invoice);
    }

    public boolean addWaitlist(Waitlist waitlist) {
        transactions.add(new Transaction("Waitlist added for " + waitlist.getProduct().getProdName()));
        return waitlistOrders.add(waitlist);
    }

    public boolean removeWaitlist(String waitlistID) {
        for (ListIterator iterator = waitlistOrders.listIterator(); iterator.hasNext(); ) {
            Waitlist waitlist = (Waitlist) iterator.next();
            String id = waitlist.getID();
            if (id.equals(waitlistID)) {
                transactions.add(new Transaction ("Waitlist removed for " + waitlist.getProduct().getProdName()));
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public boolean addOrder(Order order) {
        transactions.add(new Transaction("Order added."));
        return orders.add(order);
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public double getBalance() {
        return balance;
    }

    public String getID() {
        return id;
    }

    public Iterator getTransactions() {
        List result = new LinkedList();
        for (Iterator iterator = transactions.iterator(); iterator.hasNext(); ) {
            Transaction transaction = (Transaction) iterator.next();
            result.add(transaction);
        }
        return (result.iterator());
    }

    public Iterator getInvoices() {
        List result = new LinkedList();
        for (Iterator iterator = invoices.iterator(); iterator.hasNext(); ) {
            Invoice invoice = (Invoice) iterator.next();
            result.add(invoice);
        }
        return (result.iterator());
    }

    public Iterator getWaitlistOrders() {
        return waitlistOrders.iterator();
    }

    public void setName(String newName) {
        name = newName;
    }

    public void setPhone(String newPhone) {
        phone = newPhone;
    }

    public void setAddress(String newAddress) {
        address = newAddress;
    }

    public void setBalance(double newBalance) {
        balance = moneyRound(newBalance);
    }

    public void makePayment(double newBalance) {
        setBalance(newBalance);
        transactions.add(new Transaction("Payment | Balance: " + balance));
    }

    public boolean equals(String id) {
        return this.id.equals(id);
    }

    public double moneyRound(double num) {
        return Math.round(num * 100.0) / 100.0;
    }

    public String toString() {
        String string = "Client name " + name + " address " + address + " id " + id + " phone " + phone + " balance " + balance;
        return string;
    }
}
