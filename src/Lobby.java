import java.util.ArrayList;

/**
 * Created by Evan on 9/22/2016.
 */
public class Lobby {
    public ArrayList clients;
    int owner;
    String name;
    boolean isPublic;
    int id;
    public Lobby(String name, boolean isPublic, int creator, int lobbyId) {
        this.id = lobbyId;
        this.name = name;
        this.isPublic = isPublic;
        this.owner = creator;
        clients = new ArrayList();
        clients.add(0, owner);
    }
    public Lobby() {
        this.name = "";
        this.isPublic = true;
    }
    public void removeClient(int clientId) {
        int index = clients.indexOf(clientId);
        if (index != -1) {
            clients.remove((Object)(clientId));
        }
        else {
            return;
        }
        if ((owner == clientId) && !(clients.isEmpty())) {
            owner = (int)(clients.get(0));
        }
    }
}
