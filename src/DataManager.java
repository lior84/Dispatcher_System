import java.util.HashMap;
import java.util.Vector;
import java.util.Map;
import java.util.Iterator;
import java.util.*;

class DataManager
{
    // static variable single_instance of type Singleton
    private static DataManager single_instance = null;

    public HashMap<Integer, Message> messageIdAndMessage = new HashMap<>();
    private HashMap<Integer, Integer> MessageIdAndSenderId = new HashMap<>();
    private HashMap<Integer, Vector<Integer>> pulledSenderIdAndMessagesId = new HashMap<>();

    // private constructor restricted to this class itself
    private DataManager(){messageIdAndMessage = new HashMap<Integer, Message>();}


    public Boolean hasBeenPulled(int senderId){
        return pulledSenderIdAndMessagesId.containsKey(senderId);
    }

    private void addToWaitingForNotificationList(int k){
        if(MessageIdAndSenderId.containsKey(k)){
            if(pulledSenderIdAndMessagesId.containsKey(MessageIdAndSenderId.get(k))){
                pulledSenderIdAndMessagesId.get(MessageIdAndSenderId.get(k)).add(k);
            }
            else{
                Vector<Integer> tempVec = new Vector<>();
                tempVec.add(k);
                pulledSenderIdAndMessagesId.put(MessageIdAndSenderId.get(k), tempVec);
            }
            MessageIdAndSenderId.remove(k);
        }
    }

    private void deleteAllElements(Vector<Integer> recordsToDelete){
        for(int k : recordsToDelete){
            addToWaitingForNotificationList(k);
            messageIdAndMessage.remove(k);
        }
    }

    public void addSenderToInformedList(int messageId, int senderId){
        MessageIdAndSenderId.put(messageId, senderId);
    }

    public Vector<Message> getAllMessages(int id) {
        Vector<Message> messages = new Vector<>();
        Vector<Integer> recordsToDelete = new Vector<>();
        for (Map.Entry<Integer, Message> v : messageIdAndMessage.entrySet()) {
            if(v.getValue().getReceiverId() == id){
                messages.add(v.getValue());
                recordsToDelete.add(v.getKey());
            }
        }

        deleteAllElements(recordsToDelete);

        if(messages.isEmpty())
            return null;
        return messages;
    }

    public Vector<Message> getMessagesBySenderId(int id, int senderId) {
        Vector<Message> messages = new Vector<>();
        Vector<Integer> recordsToDelete = new Vector<>();
        for (Map.Entry<Integer, Message> v : messageIdAndMessage.entrySet()) {
            if(v.getValue().getReceiverId() == id && v.getValue().getSenderId() == senderId){
                messages.add(v.getValue());
                recordsToDelete.add(v.getKey());
            }
        }

        deleteAllElements(recordsToDelete);

        if(messages.isEmpty())
            return null;
        return messages;
    }

    public Vector<Message> getMessagesBySubject(int id, String subject) {
        Vector<Message> messages = new Vector<>();
        Vector<Integer> recordsToDelete = new Vector<>();
        for (Map.Entry<Integer, Message> v : messageIdAndMessage.entrySet()) {
            if(v.getValue().getReceiverId() == id && v.getValue().getSubject().equals(subject)){
                messages.add(v.getValue());
                recordsToDelete.add(v.getKey());
            }
        }

        deleteAllElements(recordsToDelete);

        if(messages.isEmpty())
            return null;
        return messages;
    }

    public Vector<Message> getMessagesByMessageId(int id, int messageId) {
        Vector<Message> messages = new Vector<>();
        if(messageIdAndMessage.containsKey(messageId) && messageIdAndMessage.get(messageId).getReceiverId() == id){
            messages.add(messageIdAndMessage.get(messageId));
            if(MessageIdAndSenderId.containsKey(messageId)){
                pulledSenderIdAndMessagesId.put(MessageIdAndSenderId.get(messageId), new Vector<>(messageId));
                MessageIdAndSenderId.remove(messageId);
            }
            messageIdAndMessage.remove(messageId);
        }

        if(messages.isEmpty())
            return null;
        return messages;
    }

    public Boolean removeMessage(int senderId, int messageId) {
        if(this.isInDispatcher(senderId, messageId)){
            messageIdAndMessage.remove(messageId);
            return true;
        }
        return false;
    }

    public void addMessage(Message message){
        messageIdAndMessage.put(message.getMessageId(), new Message(message));
    }

    public Boolean isInDispatcher(int senderId, int messageId){
        return (messageIdAndMessage.containsKey(messageId) && messageIdAndMessage.get(messageId).getSenderId() == senderId);
    }

    // static method to create instance of Singleton class
    public static DataManager getInstance()
    {
        if (single_instance == null)
            single_instance = new DataManager();

        return single_instance;
    }

    public String getNotificationStr(int senderId) {
        String notificationMessage = "These are the messages that has been pulled from the dispatcher: ";
        for(int messageId : pulledSenderIdAndMessagesId.get(senderId)){
            notificationMessage += String.valueOf(messageId) + ", ";
        }
        pulledSenderIdAndMessagesId.remove(senderId);
        return notificationMessage.substring(0, notificationMessage.length()-2);
    }


}