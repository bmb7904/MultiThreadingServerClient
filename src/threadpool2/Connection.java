package threadpool2;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * A custom class will serve as an interface between the Server and Client. It will take a
 * socket object that is already connected from the EchoServer class, get a line of input
 * from it, and output the same line back to the client ("echo it").
 *
 * @author Brett Bernardi
 */
public class Connection implements Runnable {
    // the client socket
    private Socket client;

    public Connection(Socket socket) {
        this.client = socket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Scanner sc = new Scanner(client.getInputStream());
                PrintWriter pw = new PrintWriter(client.getOutputStream(), true);
                String str = null;
                // get line from client
                str = sc.nextLine();
                // check for termination
                if(str.equals(".")) {
                    client.close();
                    pw.close();
                    sc.close();
                    break;
                }
                // write client's line back to it
                pw.println(str);
            } catch (Exception e) {

            }
        }

    }
}
