import java.util.*;
import java.io.*;

public class Supplier implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String address;
    private String phone;
    private String id;
    private List suppliersOfProduct = new LinkedList();
    private static final String SUPPLIER_STRING = "S";

    public Supplier(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        id = SUPPLIER_STRING + (SupplierIDServer.instance()).getID();
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

    public String getID() {
        return id;
    }

    public void setName(String newName) {
        name = newName;
    }

    public void setAddress(String newAddress) {
        address = newAddress;
    }

    public void setPhone(String newPhone) {
        phone = newPhone;
    }

    // Used to check equality in searching.
    public boolean equals(String id) {
        return this.id.equals(id);
    }

    // Assign relationship between products and suppliers.
    public boolean link(Product product) {
        return suppliersOfProduct.add(product) ? true : false;
    }

    // Unassign relationship between products and suppliers. 
    public boolean unlink(Product product) {
        return suppliersOfProduct.remove(product) ? true : false;
    }


    public String toString() {
        String string = "Supplier name " + name + " address " + address + " id " + id + "phone " + phone;
        return string;
    }
}
