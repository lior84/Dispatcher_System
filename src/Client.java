import javax.swing.*;
import java.net.*;
import java.io.*;
import javax.swing.*;

public class Client
{
    // initialize socket and input output streams
    private Socket socket		 = null;
    private DataInputStream input = null;
    private DataOutputStream out	 = null;

    //Notification pop-up
    public class OptionPane {
        JFrame f;
        OptionPane(String notification){
            f=new JFrame();
            f.setVisible(true);
            JOptionPane.showMessageDialog(f,notification);
        }
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
        Boolean endConnection = false;
        // keep reading until endConnection is true
        while (!endConnection)
        {
            try
            {
                InputStream inputStream = socket.getInputStream();
                // create a DataInputStream so we can read data from it.
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                // read the message from the socket
                String message = "";
                message = dataInputStream.readUTF();

                //A message which starts with '#' character doesn't require an answer from the client
                if(message.charAt(0) == '#') {
                    message = message.substring(1);
                    System.out.println(message);
                }
                //A message which starts with '$' character is a notification request and it doesn't require an answer from the client
                else if(message.charAt(0) == '$'){
                    new OptionPane(message.substring(1));
                }
                else if(message.equals("over"))
                {
                    endConnection = true;
                }
                //Print the message and get an answer from the user to be sent to the dispatcher
                else {
                    System.out.println(message);
                    line = input.readLine();
                    out.writeUTF(line);
                }
            }
            catch(IOException i)
            {
                endConnection = true;
            }
        }

        // close the connection
        try
        {
            input.close();
            out.close();
            socket.close();
            System.out.println("The connection has been closed!");
            System.exit(0);
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
