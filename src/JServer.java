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

    public static void main (String[] args) {
        new JServer(Integer.parseInt(args[0]));
    }

    public JServer (int portNumber) {
        setupRankColor();
        this.portNumber = portNumber;
        clientFactory = new ClientFactory(this);
        clients = new ArrayList<ClientWorker>();

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
            clients.remove(index);
        }
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

    public void sendMessage (String message, int id) {
        ClientWorker client = getClient(id);
        if ((client.user.rank == Rank.Guest) && !guestChat) {
            client.sendMessage("c000000000,Guest chat is not allowed on this server.");
        }
        else {
            for (ClientWorker c: clients) {
                c.sendMessage("c000000000," + id + " [,c" + parseColor(rankColors.get(client.user.rank)) + "," + client.user.rank.name() + ",c000000000,] " + "<" + client.nick + "> " + message);
            }
        }
    }

}
