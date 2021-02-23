import java.time.LocalDateTime;
import java.util.HashMap;

public class Message {
    private String subject;
    private String description;
    private HashMap<String, String> parameters;
    private int senderId;
    private int receiverId;
    private int messageId;
    private LocalDateTime receivingTime;
    private static int nextId = 0;

    public Message(String subject, String description, HashMap<String, String> parameters, int senderId, int receiverId) {
        this.subject = subject;
        this.description = description;
        this.parameters = parameters;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.receivingTime = LocalDateTime.now();
        this.messageId = nextId++;
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
