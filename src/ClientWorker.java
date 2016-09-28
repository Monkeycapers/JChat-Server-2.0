import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

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

    ArrayList messages;

    int currentLobby;

    public ClientWorker (JServer jServer, int id, Socket socket) {
        this.client = socket;
        this.jServer = jServer;
        this.id = id;
        isRunning = true;
        user = new User();
        nick = "Anon";
        commands = new Command[] {new UserListCommand(jServer, id), new StopCommand(jServer, id), new SignInCommand(jServer, id),
                new SignUpCommand(jServer, id), new PromoteCommand(jServer, id), new PrivateMessageCommand(jServer, id), new SignOutCommand(jServer, id), new LobbyJoinCommand(jServer, id), new LobbyCreateCommand(jServer, id)
        , new LobbyListCommand(jServer, id)};
        delay = 100;
        messages = new ArrayList();
        currentLobby = -1;
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
            Logger.logMessage("[Info]: Client info: " + client.getLocalAddress().toString());
        }
        catch (Exception e) {
            System.out.println("Could not create in/out streams");
        }
        while (isRunning) {
            //send message out
            try {
                if (pendingMessage) {
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
                    jServer.sendMessage(split[2], id, currentLobby);
                }
                else if (split[1].startsWith("/")) {
                    String name = split[1];
                    for (Command c : commands) {
                        if (c.name.equals(name)) {
                            Logger.logMessage("[Info]: Client " + id + " [" + user.rank.name() + "] " + "(" + user.username + ") " + "<" + nick + "> " + "used command: " + c.name);
                            String message = c.parse(line, user);
                            if (c.returnType == ReturnType.MessageSender) {
                                sendMessage("c000000000,[Server] to you: " + message);
                            }
                            else if (c.returnType == ReturnType.MessageAll) {
                                jServer.sendMessage("c000000000," + message, id, currentLobby);
                            }
                            else {
                                //do nothing with the parsed command
                                //c.parse(line, user);
                            }
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
        this.message += message;
        //messages.add(message);
        pendingMessage = true;
    }

    public void disconnect() {
        jServer.removeClient(id);
        isRunning = false;
    }


    public void parseCommand() {

    }


}
