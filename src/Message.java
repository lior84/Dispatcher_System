import java.time.LocalDateTime;
import java.util.HashMap;

public class Message {
    private String subject;
    private String description;
    private HashMap<String, String> parameters;
    private int senderId;
    private int receiverId;
    private int messageId;
    private static int nextId = 0;
    private LocalDateTime receivingTime;


    public Message(String subject, String description, HashMap<String, String> parameters, int senderId, int receiverId) {
        this.subject = subject;
        this.description = description;
        this.parameters = parameters;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.receivingTime = LocalDateTime.now();
        this.messageId = nextId++;
    }

    public Message(Message other) {
        this.subject = other.subject;
        this.description = other.description;
        this.parameters = other.parameters;
        this.senderId = other.senderId;
        this.receiverId = other.receiverId;
        this.receivingTime = other.receivingTime;
        this.messageId = other.messageId;
    }

    public static int getNextId() {
        return nextId;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public HashMap<String, String> getParameters() {
        return parameters;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public LocalDateTime getReceivingTime() {
        return receivingTime;
    }

    public static void main(String [] args){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("1", "a");
        params.put("2", "b");
        Message msg = new Message("Mail", "my mail", params, 84, 58);

        HashMap<String, String> params1 = new HashMap<String, String>();
        params.put("1", "a");
        params.put("2", "b");
        Message msg1 = new Message("Mail", "my mail", params, 84, 58);

        System.out.println(Message.getNextId());

    }

    @Override
    public String toString() {
        return "subject= " + subject +
                "\tdescription= " + description +
                "\tparameters= " + parameters +
                "\tsenderId= " + senderId +
                "\treceiverId= " + receiverId +
                "\treceivingTime= " + receivingTime;
    }

    public int getMessageId() {
        return messageId;
    }
}
