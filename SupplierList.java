import java.util.*;
import java.io.*;

public class SupplierList implements Serializable {
    private static final long serialVersionUID = 1L;
    private List suppliers = new LinkedList();
    private static SupplierList supplierList;

    private SupplierList() {}

    public static SupplierList instance() {
        if (supplierList == null) {
            return (supplierList = new SupplierList());
        } else {
            return supplierList;
        }
    }

    public boolean insertSupplier(Supplier supplier) {
        suppliers.add(supplier);
        return true;
    }

    public Iterator getSuppliers(){
        return suppliers.iterator();
    }

    public Supplier search(String supplierID) {
        for (Iterator iterator = suppliers.iterator(); iterator.hasNext(); ) {
            Supplier supplier = (Supplier) iterator.next();
            if (supplier.getID().equals(supplierID)) {
                return supplier;
            }
        }
        return null;
    }

    private void writeObject(java.io.ObjectOutputStream output) {
        try {
            output.defaultWriteObject();
            output.writeObject(supplierList);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
    private void readObject(java.io.ObjectInputStream input) {
        try {
            if (supplierList != null) {
                return;
            }
            else {
                input.defaultReadObject();
                if (supplierList == null) {
                    supplierList = (SupplierList) input.readObject();
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
        return suppliers.toString();
    }
}
