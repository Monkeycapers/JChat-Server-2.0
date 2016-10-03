import java.io.Serializable;

public class Message implements Serializable {

    String messageType;

    String[] arguments;

    public Message(String messageType, String[] arguments) {
        this.messageType = messageType;
        this.arguments = arguments;
    }

}