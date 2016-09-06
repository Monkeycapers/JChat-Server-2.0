import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Evan on 8/30/2016.
 */
public class ClientWorker implements Runnable {

    JServer jServer;

    private Socket client;

    int id;
    int delay;

    boolean isRunning;
    String message;
    boolean pendingMessage;

    User user;

    Command[] commands;

    String nick;

    public ClientWorker (JServer jServer, int id, Socket socket) {
        this.client = socket;
        this.jServer = jServer;
        this.id = id;
        isRunning = true;
        user = new User();
        nick = "Anon";
        commands = new Command[] {new UserListCommand(jServer, id), new StopCommand(jServer, id), new SignInCommand(jServer, id), new SignUpCommand(jServer, id), new PromoteCommand(jServer, id), new PrivateMessageCommand(jServer, id)};
        delay = 100;
    }

    public void run() {
        message = "c000000000,JChat Server 2.0";
        pendingMessage = true;
        //Handle connections with clients
        DataInputStream in = null;
        DataOutputStream out = null;
        //Create a DataIn and DataOut Stream
        try {
            in = new DataInputStream(client.getInputStream());
            out = new DataOutputStream(client.getOutputStream());
        }
        catch (Exception e) {
            System.out.println("Could not create in/out streams");
        }
        while (isRunning) {
            //send message out
            try {
                if (pendingMessage) {
                    //TODO: Explore making message a array

                    out.writeUTF(message);

                    pendingMessage = false;
                }
                else {
                    out.writeUTF("Alive");
                }
                message = "";
            }
            catch (IOException e) {
                disconnect();
            }
            //read message in
            try {
                String line = in.readUTF();
                String split[] = line.split(",");
                nick = split[0];
                if (split[1].startsWith("Alive")) {
                    //alive
                }
                else if (split[1].startsWith("Message")) {
                    //Message
                    jServer.sendMessage(split[2], id);
                }
                else if (split[1].startsWith("/")) {
                    String name = split[1];
                    for (Command c : commands) {
                        if (c.name.equals(name)) {
                            sendMessage("c000000000,[Server] to you: " + c.parse(line, user));
                            break;
                        }
                    }
                }
            }
            catch (IOException e) {
                disconnect();
            }
            try {Thread.sleep(delay);} catch (Exception e) {}
        }
    }

    public void sendMessage(String message) {
        this.message = message;
        pendingMessage = true;
    }

    public void disconnect() {
        jServer.removeClient(id);
        isRunning = false;
    }

    public void parseCommand() {

    }


}
