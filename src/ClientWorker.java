import org.java_websocket.WebSocket;

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

    WebSocket webSocket;

    int id;
    int delay;

    boolean isRunning;

    User user;

    Command[] commands;

    String nick;

    ArrayList messages;

    int currentLobby;

    DataInputStream in = null;
    DataOutputStream out = null;

    //false = raw tcp
    //true = web socket
    boolean connectionType;

    public ClientWorker (JServer jServer, int id, Socket socket) {
        this.client = socket;
        setUp(jServer, id);
        connectionType = false;
    }

    public ClientWorker(JServer jServer, int id, WebSocket webSocket) {
        this.webSocket = webSocket;
        setUp(jServer, id);
        connectionType = true;
    }

    public void setUp(JServer jServer, int id) {
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
        //Handle connections with clients
        in = null;
        out = null;
        //Create a DataIn and DataOut Stream
        if (!connectionType) {
            try {
                in = new DataInputStream(client.getInputStream());
                out = new DataOutputStream(client.getOutputStream());
                //Todo: MessageOfTheDaySupport
                send("c255255255,JChat Server");
                Logger.logMessage("[Info]: Client info: " + client.getLocalAddress().toString());
            }
            catch (Exception e) {
                System.out.println("Could not create in/out streams");
            }
        }
        else {
            Logger.logMessage("[Info]: Client info: " + webSocket.getRemoteSocketAddress().getHostString());
            send("c255255255,JChat Server");
        }
        while (isRunning && !(connectionType)) {
            loop(in, out);
        }
    }

    public void send(String message) {
        if (connectionType) {
            webSocket.send(message);
        }
        else {
            try {
                out.writeUTF(message);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void parseMessage(String line) {
        System.out.println("line: " + line);
        String split[] = line.split(" ");
        nick = split[0];
        if (split[1].startsWith("Alive")) {
            //alive
        }
        else if (split[1].startsWith("Message")) {
            jServer.sendMessage(split[2], id, currentLobby);
        }
        else if (split[1].startsWith("/")) {
            String name = split[1];
            for (Command c : commands) {
                if (c.name.equals(name)) {
                    String params = "";
                    if (c.name.equals("/signin") || c.name.equals("/signup")) {
                        params = split[2] + " apassword ";
                    } else {
                        for (String s : split) {
                            params += s + " ";
                        }
                    }

                    Logger.logMessage("[Info]: Client " + id + " [" + user.rank.name() + "] " + "(" + user.username + ") " + "<" + nick + "> " + "used command: " + c.name + " with params: " + params);
                    String message = c.parse(line, user);
                    if (c.returnType == ReturnType.MessageSender) {
                        sendMessage(jServer.cString + ",[Server] to you: " + message);
                    }
                    else if (c.returnType == ReturnType.MessageAll) {
                        jServer.sendMessage(jServer.cString + "," + message, id, currentLobby);
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

    public void loop(DataInputStream in, DataOutputStream out) {
        //read message in
        try {
            String line = null;
            line = in.readUTF();
            parseMessage(line);
        }
        catch (IOException e) {
            disconnect();
        }
        try {Thread.sleep(delay);} catch (Exception e) {}
    }

    public void sendMessage(String message) {
        send(message);
    }

    public void disconnect() {
        jServer.removeClient(id);
        isRunning = false;
    }


    public void parseCommand() {

    }


}
