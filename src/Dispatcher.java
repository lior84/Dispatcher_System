import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Vector;

public class Dispatcher
{
    private Socket          socket   = null;
    private ServerSocket    server   = null;
    private DataInputStream in       =  null;

    public Dispatcher(int port)
    {
       // manager = new DataManager();
        try
        {
            server = new ServerSocket(port);
            System.out.println("Server started");

            System.out.println("Waiting for a client ...");

            socket = server.accept();
            System.out.println("Client accepted");

            // takes input from the client socket 
            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));

            String line = "";
            Boolean endConnection = false;
            // reads message from client until "Over" is sent 
            while (!endConnection)
            {
                try
                {
                    do {
                        OutputStream outputStream = socket.getOutputStream();
                        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                        dataOutputStream.writeUTF(getDisplayMain());
                        dataOutputStream.flush(); // send the message

                        line = in.readUTF();
                        int intAns = Integer.parseInt(line);

                        if(intAns==1){//add message to the dispatcher
                            addMessageIntoDispatcher(dataOutputStream, in);
                        }
                        else if(intAns==2){//pull a message from the dispatcher
                            pullMessageFromDispatcher(dataOutputStream, in);
                        }
                        else if(intAns==3){
                            checkIfMessageInTheDispatcher(dataOutputStream, in);
                        }
                        else if(intAns==4){
                            deleteMessageFromTheDispatcher(dataOutputStream, in);
                        }
                        else if(intAns==5){
                            addUserToInformedList(dataOutputStream, in);
                        }
                        else if(intAns == 6){
                            endConnection = true;
                            dataOutputStream.writeUTF("over");
                            dataOutputStream.flush();
                        }
                        else {
                            dataOutputStream.writeUTF("#Wrong input, please try again: \n");
                            dataOutputStream.flush();
                        }
                    }while (Integer.parseInt(line) != 6);
                }
                catch(IOException i)
                {
                    System.out.println(i);
                }
            }
            System.out.println("Closing connection");

            // close connection 
            socket.close();
            in.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }

    private void addUserToInformedList(DataOutputStream dataOutputStream, DataInputStream in) {
        try {
            int senderId =  checkId(dataOutputStream, in);
            if(DataManager.getInstance().hasBeenPulled(senderId)){
                dataOutputStream.writeUTF( "$" + DataManager.getInstance().getNotificationStr(senderId));
                dataOutputStream.flush();
            }

            dataOutputStream.writeUTF("Enter your message ID: ");
            dataOutputStream.flush();
            int messageId = Integer.parseInt(in.readUTF());
            if(DataManager.getInstance().isInDispatcher(senderId, messageId)){
                DataManager.getInstance().addSenderToInformedList(messageId, senderId);
                dataOutputStream.writeUTF( "#When the message with the ID: " + messageId + " will be pulled" +
                        " from the dispatcher, you will be informed!\n");
                dataOutputStream.flush();
            }
            else {
                dataOutputStream.writeUTF( "#The message is not in the dispatcher!\n");
                dataOutputStream.flush();
            }
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }

    private int checkId(DataOutputStream dataOutputStream, DataInputStream in) throws IOException {
        int id = 0;
        do {
            dataOutputStream.writeUTF("Enter your ID: ");
            dataOutputStream.flush();
            try {
                id = Integer.parseInt(in.readUTF());
            } catch (Exception e) {
                dataOutputStream.writeUTF("#ID must be a numeric value!\n");
                dataOutputStream.flush();
            }
        }while (!isLegalId(id));
        return id;
    }

    private String getSubject(DataOutputStream dataOutputStream, DataInputStream in) throws IOException {
        dataOutputStream.writeUTF("Enter your message subject: ");
        dataOutputStream.flush();
        return new String(in.readUTF());
    }

    private String getDescription(DataOutputStream dataOutputStream, DataInputStream in) throws IOException {
        dataOutputStream.writeUTF("Enter your message description: ");
        dataOutputStream.flush();
        return new String(in.readUTF());
    }

    private int getReceiverId(DataOutputStream dataOutputStream, DataInputStream in) throws IOException {
        int receiverId = 0;
        do {
            dataOutputStream.writeUTF("Enter your receiver ID: ");
            dataOutputStream.flush();
            try {
                receiverId = Integer.parseInt(in.readUTF());
            } catch (Exception e) {
                dataOutputStream.writeUTF( "#ID must be a numeric value!\n");
                dataOutputStream.flush();
            };
        } while (!isLegalId(receiverId));
        return receiverId;
    }

    private HashMap<String, String> getParameters(DataOutputStream dataOutputStream, DataInputStream in) throws IOException {
        HashMap<String, String> parameters = new HashMap<>();
        String data;
        do {
            dataOutputStream.writeUTF("Would you like to add parameters to the message? (" +
                    "press y for adding parameters or other key to send the message): ");
            dataOutputStream.flush();
            data = new String(in.readUTF());
            if (data.equals("y") || data.equals("Y")) {
                dataOutputStream.writeUTF("Enter a key: ");
                dataOutputStream.flush();
                String key = new String(in.readUTF());
                dataOutputStream.writeUTF("Enter a value: ");
                dataOutputStream.flush();
                String value = new String(in.readUTF());
                parameters.put(key, value);
            }
        } while (data.equals("y") || data.equals("Y"));
        return parameters;
    }

    public void addMessageIntoDispatcher(DataOutputStream dataOutputStream, DataInputStream in){
        try {
            int senderId = checkId(dataOutputStream, in);
            if(DataManager.getInstance().hasBeenPulled(senderId)){
                dataOutputStream.writeUTF( "$" + DataManager.getInstance().getNotificationStr(senderId));
                dataOutputStream.flush();
            }

            //get subject
            String subject = getSubject(dataOutputStream, in);

            //get description
            String description = getDescription(dataOutputStream, in);

            //get receiver ID
            int receiverId = getReceiverId(dataOutputStream, in);

            //get parameters(optional)
            HashMap<String, String> parameters = getParameters(dataOutputStream, in);

            //compose and insert message into the Data Manager
            Message composedMessage = new Message(subject, description, parameters, senderId, receiverId);
            DataManager manager = DataManager.getInstance();
            manager.addMessage(composedMessage);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }

    private String setMessageStr(Vector<Message> messages){
        String messagesStr = "#";
        if (messages != null) {
            messagesStr += messages.get(0).toString() + "\n";
        } else {
            messagesStr += "There are no relevant messages for you!\n";
        }
        return messagesStr;
    }

    private void pullMessageFromDispatcher(DataOutputStream dataOutputStream, DataInputStream in){
        try {
            //get receiver ID, check if the user is authenticated
            int receiverId = checkId(dataOutputStream, in);
            if(DataManager.getInstance().hasBeenPulled(receiverId)){
                dataOutputStream.writeUTF( "$" + DataManager.getInstance().getNotificationStr(receiverId));
                dataOutputStream.flush();
            }

            int intPullAns;
            do {
                //send the options menu of pulling a message
                dataOutputStream.writeUTF(getPullMessageMain());
                dataOutputStream.flush();
                intPullAns = Integer.parseInt(in.readUTF());

                Vector<Message> messages = new Vector<>();
                if (intPullAns == 1) {
                    messages = DataManager.getInstance().getAllMessages(receiverId);
                    String messagesStr = "#";
                    if (messages != null) {
                        for (Message msg : messages) {
                            messagesStr += msg.toString() + "\n";
                        }
                    } else {
                        messagesStr += "There are no relevant messages for you!\n";
                    }
                    //send all the messages
                    dataOutputStream.writeUTF( messagesStr + "\n\n");
                    dataOutputStream.flush();

                } else if (intPullAns == 2) {
                    dataOutputStream.writeUTF("Enter your message ID: ");
                    dataOutputStream.flush();
                    int messageId = Integer.parseInt(in.readUTF());

                    messages = DataManager.getInstance().getMessagesByMessageId(receiverId, messageId);
                    //send all the messages
                    dataOutputStream.writeUTF( setMessageStr(messages) + "\n\n");
                    dataOutputStream.flush();

                } else if (intPullAns == 3) {
                    dataOutputStream.writeUTF("Enter the sender ID: ");
                    dataOutputStream.flush();
                    int senderId = Integer.parseInt(in.readUTF());

                    messages = DataManager.getInstance().getMessagesBySenderId(receiverId, senderId);
                    //send all the messages
                    dataOutputStream.writeUTF( setMessageStr(messages) + "\n\n");
                    dataOutputStream.flush();

                } else if (intPullAns == 4) {
                    dataOutputStream.writeUTF("Enter the subject: ");
                    dataOutputStream.flush();
                    String subject = new String(in.readUTF());

                    messages = DataManager.getInstance().getMessagesBySubject(receiverId, subject);
                    //send all the messages
                    dataOutputStream.writeUTF( setMessageStr(messages) + "\n\n");
                    dataOutputStream.flush();

                } else if (intPullAns != 5) {
                    dataOutputStream.writeUTF( "#Wrong input, please try again: ");
                    dataOutputStream.flush();
                }
            } while (intPullAns != 5);
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }

    private void checkIfMessageInTheDispatcher(DataOutputStream dataOutputStream, DataInputStream in){
        try {
            int senderId =  checkId(dataOutputStream, in);
            if(DataManager.getInstance().hasBeenPulled(senderId)){
                dataOutputStream.writeUTF( "$" + DataManager.getInstance().getNotificationStr(senderId));
                dataOutputStream.flush();
            }

            dataOutputStream.writeUTF("Enter your message ID: ");
            dataOutputStream.flush();
            int messageId = Integer.parseInt(in.readUTF());
            if(DataManager.getInstance().isInDispatcher(senderId, messageId)){
                dataOutputStream.writeUTF( "#The message is still in the dispatcher!\n");
                dataOutputStream.flush();
            }
            else {
                dataOutputStream.writeUTF( "#The message is not in the dispatcher!\n");
                dataOutputStream.flush();
            }
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }

    private void deleteMessageFromTheDispatcher(DataOutputStream dataOutputStream, DataInputStream in){
        try {
            int senderId = checkId(dataOutputStream, in);
            if(DataManager.getInstance().hasBeenPulled(senderId)){
                dataOutputStream.writeUTF( "$" + DataManager.getInstance().getNotificationStr(senderId));
                dataOutputStream.flush();
            }

            dataOutputStream.writeUTF("Enter your message ID: ");
            dataOutputStream.flush();
            int messageId = Integer.parseInt(in.readUTF());
            if(DataManager.getInstance().removeMessage(senderId, messageId)){
                dataOutputStream.writeUTF( "#The message has been deleted!\n");
                dataOutputStream.flush();
            }
            else {
                dataOutputStream.writeUTF( "#The message is not in the dispatcher!\n");
                dataOutputStream.flush();
            }
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }

    private String getPullMessageMain() {
        String pullMain = "Pick an action by entering the number which represents it:\n";
        pullMain += "1. Pull all of my messages.\n" +
                "2. Pull a specific message.\n" +
                "3. Pull all the messages from a specific sender.\n" +
                "4. Pull all the messages with a specific subject.\n" +
                "5. Exit\n\n" + "Your choice: ";

        return pullMain;
    }

    private boolean isLegalId(int id) {return(id > 99 && id < 1000); }

    private boolean isAuthenticate(int id) { return(id > 99 && id < 1000); }

    private String getDisplayMain() {
        String mainPage = "Pick an action by entering the number which represents it:\n";
        mainPage += "1. Send a message.\n" +
                           "2. Pull a message.\n" +
                           "3. Check if a message is in the dispatcher.\n" +
                           "4. Delete a message.\n" +
                           "5. Request to be informed when a message is pulled out of the dispatcher.\n" +
                           "6. Exit.\n\n" +
                           "Your choice: ";

        return mainPage;
    }

    public static void main(String args[])
    {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("a","b");
        DataManager manager = DataManager.getInstance() ;
        manager.messageIdAndMessage.put(0, new Message("a", "a", parameters, 111, 999));
        manager.messageIdAndMessage.put(1, new Message("b", "b", parameters, 222, 999));
        manager.messageIdAndMessage.put(2, new Message("c", "c", parameters, 333, 999));
        manager.messageIdAndMessage.put(3, new Message("d", "d", parameters, 111, 888));
        manager.messageIdAndMessage.put(4, new Message("e", "e", parameters, 111, 888));
        manager.messageIdAndMessage.put(5, new Message("f", "f", parameters, 111, 888));

        Dispatcher server = new Dispatcher(5000);
    }
}