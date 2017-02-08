import java.io.*;

public class ClientIDServer implements Serializable {
    private int idCounter;
    private static ClientIDServer server;

    private ClientIDServer() {
        idCounter = 1;
    }

    public static ClientIDServer instance() {
        if (server == null) {
            return (server = new ClientIDServer());
        }
        else {
            return server;
        }
    }

    public int getID() {
        return idCounter++;
    }

    public String toString() {
        return ("IdServer" + idCounter);
    }

    public static void retrieve(ObjectInputStream input) {
        try {
            server = (ClientIDServer) input.readObject();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
        catch(Exception cnfe) {
            cnfe.printStackTrace();
        }
    }

    private void writeObject(java.io.ObjectOutputStream output) throws IOException {
        try {
            output.defaultWriteObject();
            output.writeObject(server);
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void readObject(java.io.ObjectInputStream input) throws IOException, ClassNotFoundException {
        try {
            input.defaultReadObject();
            if (server == null) {
                server = (ClientIDServer) input.readObject();
            }
            else {
                input.readObject();
            }
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
