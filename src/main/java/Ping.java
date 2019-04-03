

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Ping {
    private static Ping ourInstance = new Ping();

    public static Ping getInstance() {
        return ourInstance;
    }

    private Ping() {
    }

    // Sends ping request to a provided IP address
    public boolean crunchifyAddressReachable(String address, int port, int timeout) {
        try {

            try (Socket crunchifySocket = new Socket()) {
                // Connects this socket to the server with a specified timeout value.
                crunchifySocket.connect(new InetSocketAddress(address, port), timeout);
            }
            // Return true if connection successful
            return true;
        } catch (IOException exception) {
            exception.printStackTrace();

            // Return false if connection fails
            return false;
        }
    }
}
