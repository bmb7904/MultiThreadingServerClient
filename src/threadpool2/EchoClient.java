package threadpool2;

import threadpool.EchoServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * A custom class that will create a Socket object and connection with a server using
 * the socket object. This will prompt the user on the command line for some input, and
 * it will send the the line of input to the server's side of the socket. It will then
 * get immediately get a line from the socket and print it out. This occurs during an
 * infinite loop which only breaks when a single "." is entered by the user.
 *
 * @author Brett Bernardi
 */
public class EchoClient {
    public static void main(String[] args) throws IOException {
        final String HOST = "Localhost";
        System.out.println("Echo Client Starting");
        // create connection with Server by creating a Socket object
        Socket server = new Socket(HOST, EchoServer.PORT);
        // Scanner object to get keyboard input
        Scanner keyboard = new Scanner(System.in);
        // PrintWriter object to write to server's side of the socket
        PrintWriter output = new PrintWriter(server.getOutputStream(),true);
        // Scanner object to read a line from the Server
        Scanner input = new Scanner(server.getInputStream());
        // string will temporarily hold the input and output
        String str = "";
        // the main loop only breaks out if user enters "."
        while(true) {
            // prompt the client
            System.out.print("Client: ");
            // get line of input from keyboard
            str = keyboard.nextLine();
            // write it to the server side of the socket
            output.println(str);
            // if period entered, break out and terminate client
            if(str.equals(".")) {
                server.close();
                keyboard.close();
                output.close();
                break;
            }
            // get the output from the Server and print it out
            str = input.nextLine();
            System.out.println("Server: " + str);
        }

        System.out.println("Echo Client Terminating");
    }
}