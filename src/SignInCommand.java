/**
 * Created by Evan on 9/2/2016.
 */
public class SignInCommand extends Command {
    JServer jServer;
    public SignInCommand(JServer jServer, int id) {
        this.jServer = jServer;
        this.name = "/signin";
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
            try {
                if (jServer.containsClient(split[2])) {
                    return "User already exists";
                }
            }
            catch (Exception e) {
                System.out.println("No users, exception.");
            }
            User u = Authenticate.login(split[2], split[3]);
            if (u.rank == Rank.Guest) {
                return "Could not login with user: " + user;
            }
            else {
                jServer.getClient(clientId).user = u;
                return "Logged in. You are a: [," + JServer.parseColor(jServer.rankColors.get(u.rank)) + "," + u.rank.name() + "," + jServer.cString + ",].";
            }

        }
        catch (Exception e) {
            return "Invalid usage of command: signin";
        }
        //return "";
    }
}
