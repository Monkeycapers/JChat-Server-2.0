/**
 * Created by Evan on 8/31/2016.
 */
public class UserListCommand extends Command {
    JServer jServer;
    public UserListCommand(JServer jServer, int id) {
        this.jServer = jServer;
        this.name = "/userlist";
        this.minimumRank = Rank.Guest;
        this.clientId = id;
    }
    @Override
    public String parse(String message, User user) {
        if (!testRank(user.rank)) {
            return "Rank is lower than the minimum rank. Min rank is " + minimumRank.name() + ", got " + user.rank.name();
        }
        try {
            return jServer.getUserList();
        }
        catch (Exception e) {
            return "Invalid usage of command: userlist";
        }
        //return "";
    }

}
