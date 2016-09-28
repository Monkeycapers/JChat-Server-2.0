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

    public static void main (String[] args) {
        new JServer(Integer.parseInt(args[0]));
    }

    public JServer (int portNumber) {
        setupRankColor();
        this.portNumber = portNumber;
        clientFactory = new ClientFactory(this);
        clients = new ArrayList<ClientWorker>();
        lobbys = new ArrayList<Lobby>();
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
                lobbys.get(currentLobby).removeClient(clientId);
                if (lobbys.get(currentLobby).clients.isEmpty()) {
                    removeLobby(currentLobby);
                }
            }
            clients.remove(index);
        }
    }

    public void removeLobby(int lobbyId) {
        lobbys.remove(lobbyId);
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
            total += ",c000000000," + client.id + " [,c" + parseColor(rankColors.get(client.user.rank)) + "," + client.user.rank + ",c000000000,] " + "(" + client.user.username + ") " + client.nick + "\n";
        }

        return total.substring(0, total.length() - 1);
    }

    public String getLobbyList () {
        String total = "";
        for (Lobby lobby: lobbys) {
            total += ",c000000000," + lobby.name + " " + lobby.clients.size() + "\n" ;
        }

        return total.substring(0, total.length() - 1);
    }

    public void sendMessage (String message, int id, int lobbyId) {
        ClientWorker client = getClient(id);

        if ((client.user.rank == Rank.Guest) && !guestChat) {
            client.sendMessage("c000000000,Guest chat is not allowed on this server.");
        }
        else {
            System.out.println(id + " [" + client.user.rank.name() + "] " + "<" + client.nick + "> " + message);
            for (ClientWorker c: clients) {
                if (c.currentLobby == lobbyId) {
                    c.sendMessage("c000000000," + id  + " " + getLobbyName(lobbyId) +  " [,c" + parseColor(rankColors.get(client.user.rank)) + "," + client.user.rank.name() + ",c000000000,] " + "<" + client.nick + "> " + message);
                }
            }
            Logger.logMessage(id + " " + lobbyId + " [" + client.user.rank.name() + "] " + "<" + client.nick + "> " + message);
        }
    }

    public void createLobby(int id, String name, boolean isPublic) {
        Lobby lobby = new Lobby(name, isPublic, id, lobbys.size());
        System.out.println("?c");
        lobbys.add(lobby);
        clients.get(id).currentLobby = lobbys.indexOf(lobby);
    }

    public void addToLobby(int lobbyId, int id) {
        Lobby lobby = lobbys.get(lobbyId);
        lobby.clients.add(id);
        getClient(id).currentLobby = lobbyId;
    }

    public int getLobbyInt(String name) {
        for (Lobby lobby: lobbys) {
            if (lobby.name.equals(name)) {
                return lobbys.indexOf(lobby);
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

}
