import java.util.HashMap;
import java.util.Vector;
import java.util.Map;
import java.util.Iterator;
import java.util.*;

class DataManager
{
    // static variable single_instance of type Singleton
    private static DataManager single_instance = null;

    // variable of type String
    public static HashMap<Integer, Message> messageIdAndMessage = new HashMap<>();

    // private constructor restricted to this class itself
    private DataManager(){messageIdAndMessage = new HashMap<Integer, Message>();}

    public static Vector<Message> getAllMessages(int id) {
        Vector<Message> messages = new Vector<>();
        Vector<Integer> recordsToDelete = new Vector<>();
        for (Map.Entry<Integer, Message> v : messageIdAndMessage.entrySet()) {
            if(v.getValue().getReceiverId() == id){
                messages.add(v.getValue());
                recordsToDelete.add(v.getKey());
            }
        }

        for(int k : recordsToDelete){
            messageIdAndMessage.remove(k);
        }

        if(messages.isEmpty())
            return null;
        return messages;
    }

    public static Vector<Message> getMessagesBySenderId(int id, int senderId) {
        Vector<Message> messages = new Vector<>();
        Vector<Integer> recordsToDelete = new Vector<>();
        for (Map.Entry<Integer, Message> v : messageIdAndMessage.entrySet()) {
            if(v.getValue().getReceiverId() == id && v.getValue().getSenderId() == senderId){
                messages.add(v.getValue());
                recordsToDelete.add(v.getKey());
            }
        }

        for(int k : recordsToDelete){
            messageIdAndMessage.remove(k);
        }

        if(messages.isEmpty())
            return null;
        return messages;
    }

    public static Vector<Message> getMessagesBySubject(int id, String subject) {
        Vector<Message> messages = new Vector<>();
        Vector<Integer> recordsToDelete = new Vector<>();
        for (Map.Entry<Integer, Message> v : messageIdAndMessage.entrySet()) {
            if(v.getValue().getReceiverId() == id && v.getValue().getSubject().equals(subject)){
                messages.add(v.getValue());
                recordsToDelete.add(v.getKey());
            }
        }

        for(int k : recordsToDelete){
            messageIdAndMessage.remove(k);
        }

        if(messages.isEmpty())
            return null;
        return messages;
    }

    public static Vector<Message> getMessagesByMessageId(int id, int messageId) {
        Vector<Message> messages = new Vector<>();
        if(messageIdAndMessage.containsKey(messageId) && messageIdAndMessage.get(messageId).getReceiverId() == id){
            messages.add(messageIdAndMessage.get(messageId));
            messageIdAndMessage.remove(messageId);
        }

        if(messages.isEmpty())
            return null;
        return messages;
    }

    public static Boolean removeMessage(int senderId, int messageId) {
        if(DataManager.isInDispatcher(senderId, messageId)){
            messageIdAndMessage.remove(messageId);
            return true;
        }
        return false;
    }

    public void addMessage(Message message){
        messageIdAndMessage.put(message.getMessageId(), new Message(message));
    }

    public static Boolean isInDispatcher(int senderId, int messageId){
        return (messageIdAndMessage.containsKey(messageId) && messageIdAndMessage.get(messageId).getSenderId() == senderId);
    }

    // static method to create instance of Singleton class
    public static DataManager getInstance()
    {
        if (single_instance == null)
            single_instance = new DataManager();

        return single_instance;
    }

}