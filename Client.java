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

    public Client(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        id = CLIENT_STRING + (ClientIDServer.instance()).getID();
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
