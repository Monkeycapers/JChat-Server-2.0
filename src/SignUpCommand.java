/**
 * Created by Evan on 9/2/2016.
 */
public class SignUpCommand extends Command {
    JServer jServer;
    public SignUpCommand(JServer jServer, int id) {
        this.jServer = jServer;
        this.name = "/signup";
        this.minimumRank = Rank.Guest;
        this.clientId = id;
    }
    @Override
    public String parse(String message, User user) {
        if (!testRank(user.rank)) {
            return "Rank is lower than the minimum rank. Min rank is " + minimumRank.name() + ", got " + user.rank.name();
        }
        try {
            String[] split = message.split(",");
            User u = Authenticate.signup(split[2], split[3]);
            if (u.rank == Rank.Guest) {
                return "Could not signup with user: " + user;
            }
            else {
                jServer.getClient(clientId).user = u;
                return "Logged in and signed up. You are a: [," + JServer.parseColor(jServer.rankColors.get(u.rank)) + "," + u.rank.name() + ",c000000000,].";
            }
        }
        catch (Exception e) {
            return "Invalid usage of command: Signup";
        }
    }
}
