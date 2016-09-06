/**
 * Created by Evan on 9/3/2016.
 */
public class PrivateMessageCommand extends Command {
    JServer jServer;
    public PrivateMessageCommand(JServer jServer, int id) {
        this.jServer = jServer;
        this.name = "/pm";
        this.minimumRank = Rank.User;
        this.clientId = id;
    }
    @Override
    public String parse(String message, User user) {
        if (!testRank(user.rank)) {
            return "Rank is lower than the minimum rank. Min rank is " + minimumRank.name() + ", got " + user.rank.name();
        }
        try {
            String[] split = message.split(",");
            ClientWorker worker = jServer.getClient(new User(Rank.Guest, split[2]));
            worker.sendMessage("c000000000,[" + jServer.getClient(clientId).nick + "] to you:" + split[3]);
            return "Sent pm.";
        }
        catch (Exception e) {
            return "Invalid usage of command: private message";
        }
        //return "";
    }
}
