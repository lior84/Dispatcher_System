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
    public HashMap<Integer,  Set<Integer>> receiverIdAndMessagesId = new HashMap<>();
    private HashMap<Integer, Integer> MessageIdAndSenderId = new HashMap<>();
    private HashMap<Integer, Vector<Integer>> pulledSenderIdAndMessagesId = new HashMap<>();

    // private constructor restricted to this class itself
    private DataManager(){}

    // static method to create instance of Singleton class
    public static DataManager getInstance()
    {
        if (single_instance == null)
            single_instance = new DataManager();

        return single_instance;
    }

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
            receiverIdAndMessagesId.get(messageIdAndMessage.get(k).getReceiverId()).remove(k);
            if(receiverIdAndMessagesId.get(messageIdAndMessage.get(k).getReceiverId()).isEmpty())
                receiverIdAndMessagesId.remove(messageIdAndMessage.get(k).getReceiverId());
            messageIdAndMessage.remove(k);
        }
    }

//    private void addToReceiverAndMessageId(){
//
//    }

    public void addSenderToInformedList(int messageId, int senderId){
        MessageIdAndSenderId.put(messageId, senderId);
    }

    public Vector<Message> getAllMessages(int receiverId) {
        Vector<Message> messages = new Vector<>();
        Vector<Integer> recordsToDelete = new Vector<>();

        if(receiverIdAndMessagesId.containsKey(receiverId)){
            for(int messageId : receiverIdAndMessagesId.get(receiverId)){
                messages.add(messageIdAndMessage.get(messageId));
                recordsToDelete.add(messageId);
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
            receiverIdAndMessagesId.get(messageIdAndMessage.get(messageId).getReceiverId()).remove(messageId);
            if(receiverIdAndMessagesId.get(messageIdAndMessage.get(messageId).getReceiverId()).isEmpty())
                receiverIdAndMessagesId.remove(messageIdAndMessage.get(messageId).getReceiverId());
            messageIdAndMessage.remove(messageId);
        }

        if(messages.isEmpty())
            return null;
        return messages;
    }

    public Boolean removeMessage(int senderId, int messageId) {
        if(this.isInDispatcher(senderId, messageId)){
            receiverIdAndMessagesId.get(messageIdAndMessage.get(messageId).getReceiverId()).remove(messageId);
            if(receiverIdAndMessagesId.get(messageIdAndMessage.get(messageId).getReceiverId()).isEmpty())
                receiverIdAndMessagesId.remove(messageIdAndMessage.get(messageId).getReceiverId());
            messageIdAndMessage.remove(messageId);
            return true;
        }
        return false;
    }

    public void addMessage(Message message){
        messageIdAndMessage.put(message.getMessageId(), message);
        int receiverId = message.getReceiverId();
        if(receiverIdAndMessagesId.containsKey(receiverId)){
            receiverIdAndMessagesId.get(receiverId).add(message.getMessageId());
        }
        else{
            Set<Integer> tempSet = new HashSet<Integer>();
            tempSet.add(message.getMessageId());
            receiverIdAndMessagesId.put(receiverId, tempSet);
        }
    }

    public Boolean isInDispatcher(int senderId, int messageId){
        return (messageIdAndMessage.containsKey(messageId) && messageIdAndMessage.get(messageId).getSenderId() == senderId);
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