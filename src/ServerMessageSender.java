import java.io.DataOutputStream;

/**
 * Created by Evan on 10/1/2016.
 */
public class ServerMessageSender {
    DataOutputStream out;
    public ServerMessageSender(DataOutputStream dataOutputStream) {
       out = dataOutputStream;
    }
    public void send(String message) {
        try {
            out.writeUTF(message);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
