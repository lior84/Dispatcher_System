import java.net.*;
import java.io.*;

public class Client implements Runnable
{
    // initialize socket and input output streams
    private Socket socket		 = null;
    private DataInputStream input = null;
    private DataOutputStream out	 = null;

    @Override
    public void run() {

    }

    // constructor to put ip address and port
    public Client(String address, int port)
    {
        // establish a connection
        try
        {
            socket = new Socket(address, port);
            System.out.println("Connected");

            // takes input from terminal
            input = new DataInputStream(System.in);

            // sends output to the socket
            out = new DataOutputStream(socket.getOutputStream());
        }
        catch(UnknownHostException u)
        {
            System.out.println(u);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }

        // string to read message from input
        String line = "";

        // keep reading until "Over" is input
        while (!line.equals("Over"))
        {
            try
            {
                InputStream inputStream = socket.getInputStream();
                // create a DataInputStream so we can read data from it.
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                // read the message from the socket
                String message = "";
                message = dataInputStream.readUTF();

                if(message.charAt(0) == '#') {
                    message = message.substring(1);
                    System.out.println(message);
                }
                else {
                    System.out.println(message);
                    line = input.readLine();
                    out.writeUTF(line);
                }

            }
            catch(IOException i)
            {
                System.out.println(i);
            }
        }

        // close the connection
        try
        {
            input.close();
            out.close();
            socket.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }

    public static void main(String args[])
    {
        Client client = new Client("127.0.0.1", 5000);
    }
}
