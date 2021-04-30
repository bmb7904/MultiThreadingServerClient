package threadpool2;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * EchoServer. The server creates a serverSocket object and a ThreadPool object. In an
 * infinite loop, it listens for connections using the serverSocket object. When
 * it accepts a new connection, the server creates a Socket object serves as the
 * connection. It then creates a Connection object as passes that object to the
 * ThreaPool.
 *
 * @author Brett Bernardi
 */
public class EchoServer {
    public static final int PORT = 1509;
    public static void main(String[] args) throws IOException {

        System.out.println("Server Started!");
        ServerSocket serverSocket = new ServerSocket(PORT);
        // creates ThreadPool object
        ThreadPool threadPool = new ThreadPool();
        System.out.println("ThreadPool Created!");
        System.out.println("Awaiting Clients!");

        // infinite loop to get connections
        while(true) {
            try {
                Socket client = serverSocket.accept();
                System.out.println("New Connection");
                Connection connection = new Connection(client);
                threadPool.addConnection(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
