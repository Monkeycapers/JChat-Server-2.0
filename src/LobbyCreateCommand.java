/**
 * Created by Evan on 9/23/2016.
 */
public class LobbyCreateCommand extends Command {
    JServer jServer;
    public LobbyCreateCommand(JServer jServer, int id) {
        this.jServer = jServer;
        this.name = "/createlobby";
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
            System.out.println("?");
            for (String s: split) {
                System.out.println(s);
            }
            String name = split[2];

            jServer.createLobby(clientId, name, true);
            return "Created lobby.";
        }
        catch (Exception e) {
            return "Invalid usage of command: " + name;
        }
        //return "";
    }

}