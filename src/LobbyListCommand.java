/**
 * Created by Evan on 9/23/2016.
 */
public class LobbyListCommand extends Command {
    JServer jServer;
    public LobbyListCommand(JServer jServer, int id) {
        this.jServer = jServer;
        this.name = "/lobbys";
        this.minimumRank = Rank.Guest;
        this.clientId = id;
    }
    @Override
    public String parse(String message, User user) {
        if (!testRank(user.rank)) {
            return "Rank is lower than the minimum rank. Min rank is " + minimumRank.name() + ", got " + user.rank.name();
        }
        try {
            return jServer.getLobbyList();
        }
        catch (Exception e) {
            return "Invalid usage of command: lobbys";
        }
        //return "";
    }

}
