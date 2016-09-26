/**
 * Created by Evan on 9/23/2016.
 */
public class LobbyJoinCommand extends Command {
    JServer jServer;
    public LobbyJoinCommand(JServer jServer, int id) {
        this.jServer = jServer;
        this.name = "/joinlobby";
        this.minimumRank = Rank.User;
        this.clientId = id;
        this.returnType = ReturnType.MessageSender;
    }
    @Override
    public String parse(String message, User user) {
        if (!testRank(user.rank)) {
            return "Rank is lower than the minimum rank. Min rank is " + minimumRank.name() + ", got " + user.rank.name();
        }
        try {
            String[] split = message.split(",");
            String name = split[2];
            jServer.addToLobby(jServer.getLobbyInt(name), clientId);
            return "Joined lobby.";
        }
        catch (Exception e) {
            return "Invalid usage of command: " + name;
        }
        //return "";
    }
}
