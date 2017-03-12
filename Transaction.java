import java.util.*;
import java.io.*;

public class Transaction implements Serializable {
    public static final long serialVersionUID = 1L;
    private String info;
    private Calendar date;

    public Transaction(String info) {
        this.info = info;
        date = new GregorianCalendar();
        date.setTimeInMillis(System.currentTimeMillis());
    }

    public boolean onDate(Calendar date) {
        return ((date.get(Calendar.YEAR) == this.date.get(Calendar.YEAR)) &&
                (date.get(Calendar.MONTH) == this.date.get(Calendar.MONTH)) &&
                (date.get(Calendar.DATE) == this.date.get(Calendar.DATE)));
    }

    public String getInfo() {
        return info;
    }

    public String getDate() {
        return date.get(Calendar.MONTH) + "/" + date.get(Calendar.DATE) + "/" + date.get(Calendar.YEAR);
    }

    public String toString(){
        return ("[" + getDate() + "] Transaction Info: " + info);
    }
}
