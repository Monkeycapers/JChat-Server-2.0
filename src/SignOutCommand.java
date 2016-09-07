/**
 * Created by Evan on 9/7/2016.
 */
public class SignOutCommand extends Command {
    JServer jServer;
    public SignOutCommand(JServer jServer, int id) {
        this.jServer = jServer;
        this.name = "/signout";
        this.minimumRank = Rank.User;
        this.clientId = id;
    }
    @Override
    public String parse(String message, User user) {
        if (!testRank(user.rank)) {
            return "Rank is lower than the minimum rank. Min rank is " + minimumRank.name() + ", got " + user.rank.name();
        }
        try {
            jServer.getClient(clientId).user = new User();
            return "Signed out.";
        }
        catch (Exception e) {
            return "Invalid usage of command: signout";
        }
        //return "";

    }
}
