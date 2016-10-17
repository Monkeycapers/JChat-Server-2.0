import org.java_websocket.WebSocket;

import javax.xml.ws.Endpoint;
import javax.xml.ws.http.HTTPBinding;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Evan on 8/30/2016.
 */
public class JServer {

    int portNumber;

    ServerSocket serverSocket;

    ClientFactory clientFactory;

    ArrayList<ClientWorker> clients;

    boolean running = true;

    boolean guestChat = false;

    Map <Rank, Color> rankColors = new HashMap<Rank, Color>();

    CardGamePlayer cardGamePlayer;

    ArrayList<Lobby> lobbys;

    String cString = "c255255255";

    WebSocketFactory webSocketFactory;

    public static void main (String[] args) {
        new JServer(Integer.parseInt(args[0]));
    }

    public JServer (int portNumber) {
        setupRankColor();
        this.portNumber = portNumber;
        clientFactory = new ClientFactory(this);
        //http = new httpWorker(8000);
        clients = new ArrayList<ClientWorker>();
        lobbys = new ArrayList<Lobby>();
        webSocketFactory = new WebSocketFactory(8080, this);
        webSocketFactory.start();

        new Thread(clientFactory).start();
    }

    public void setupRankColor () {
        rankColors.put(Rank.Banned, Color.RED);
        rankColors.put(Rank.Guest, Color.green);
        rankColors.put(Rank.User, Color.cyan);
        rankColors.put(Rank.Op, Color.orange);
        rankColors.put(Rank.Admin, Color.pink);
    }

    public static String parseColorInt (int i) {
        String s = Integer.toString(i);
        String total = "";
        if (s.length() == 1) {
            total = "00";
        }
        else if (s.length() == 2) {
            total = "0";
        }
        return total + s;
    }

    public static String parseColor (Color c) {
        return parseColorInt(c.getRed()) + parseColorInt(c.getGreen()) + parseColorInt(c.getBlue());
    }

    public void addClient (ClientWorker client) {
        clients.add(client);
    }

    public void removeClient (int clientId) {
        int index = -1;
        for (ClientWorker client: clients) {
            if (client.id == clientId) {
                index = clients.indexOf(client);
            }
        }
        if (index != -1) {

            int currentLobby = 0;
            //try {
                currentLobby = getClient(clientId).currentLobby;
            //}
            //catch (Exception e) {

            //}

            if (currentLobby != -1) {
                getLobby(currentLobby).removeClient(clientId);
                if (getLobby(currentLobby).clients.isEmpty()) {
                    removeLobby(currentLobby);
                }
            }
            clients.remove(index);
        }
    }

    public void removeLobby(int lobbyId) {
        int index = 0;
        int spot = 0;
        for (Lobby lobby: lobbys) {
            if (lobby.id == lobbyId) {
                spot = index;
            }
            index++;
        }

        lobbys.remove(spot);
    }

    public ClientWorker getClient (int clientId) {
        for (ClientWorker client: clients) {
            if (client.id == clientId) {
                return client;
            }
        }
        return null;
    }

    public ClientWorker getClient (User user) {
        for (ClientWorker client: clients) {
            if (client.user.username.equals(user.username)) {
                return client;
            }
        }
        return null;
    }

    public boolean containsClient (String user) {
        for (ClientWorker client: clients) {
            if (client.user.username.equals(user)) {
                return true;
            }
        }
        return false;
    }

    public void ClientManager () {

    }

    public String getUserList () {
        String total = "";
        for (ClientWorker client: clients) {
            total += ",c255255255," + client.id + " [,c" + parseColor(rankColors.get(client.user.rank)) + "," + client.user.rank + "," + cString + ",] " + "(" + client.user.username + ") " + client.nick + "\n";
        }

        return total.substring(0, total.length() - 1);
    }

    public String getLobbyList () {
        String total = "";
        for (Lobby lobby: lobbys) {
            total += ",c255255255," + lobby.name + " " + lobby.clients.size() + "\n" ;
        }
        if (total.equals("")) {
            return "No current lobbys.";
        }
        else {
            return total.substring(0, total.length() - 1);
        }

    }

    public void sendMessage (String message, int id, int lobbyId) {
        ClientWorker client = getClient(id);

        if ((client.user.rank == Rank.Guest) && !guestChat) {
            client.sendMessage("c255255255,Guest chat is not allowed on this server.");
        }
        else {
            System.out.println(id + " [" + client.user.rank.name() + "] " + "<" + client.nick + "> " + message);
            for (ClientWorker c: clients) {

                //Todo: See if the message is being sent to one or all lobby(s)
                if (c.currentLobby == lobbyId) {
                    c.sendMessage("c255255255," + id  + " " + getLobbyName(lobbyId) +  " [,c" + parseColor(rankColors.get(client.user.rank)) + "," + client.user.rank.name() + ",c255255255,] " + "<" + client.nick + "> " + message);
                }
            }
            Logger.logMessage(id + " " + getLobbyName(lobbyId) + " [" + client.user.rank.name() + "] " + "<" + client.nick + "> " + message);
        }
    }

    public void createLobby(int id, String name, boolean isPublic) {
        Lobby lobby = new Lobby(name, isPublic, id, lobbys.size());
        System.out.println("?c");
        lobbys.add(lobby);
        System.out.println("?c2");
        getClient(id).currentLobby = lobby.id;
    }

    public Lobby getLobby(int lobbyId) {
        for (Lobby lobby: lobbys) {
            if (lobby.id == lobbyId) {
                return lobby;
            }
        }
        return null;
    }

    public void addToLobby(int lobbyId, int id) {
        Lobby lobby = getLobby(lobbyId);
        lobby.clients.add(id);
        getClient(id).currentLobby = lobbyId;
    }

    public int getLobbyInt(String name) {
        for (Lobby lobby: lobbys) {
            if (lobby.name.equals(name)) {
                return lobby.id;
            }
        }
        return -1;
    }

    public String getLobbyName(int lobbyId) {
        for (Lobby lobby: lobbys) {
            if (lobby.id == lobbyId) {
                return lobby.name;
            }
        }
        return "General";
    }

    public ClientWorker getClient(WebSocket webSocket) {
        for (ClientWorker c: clients) {
            if (c.connectionType) {
                if (c.webSocket.equals(webSocket)) {
                    return c;
                }
            }
        }
        return null;
    }

}
