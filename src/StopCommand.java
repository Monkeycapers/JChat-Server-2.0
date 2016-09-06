/**
 * Created by Evan on 8/31/2016.
 */
public class StopCommand extends Command {
    JServer jServer;
    public StopCommand(JServer jServer, int id) {
        this.jServer = jServer;
        this.name = "/stop";
        this.minimumRank = Rank.Admin;
        this.clientId = id;
    }
    @Override
    public String parse(String message, User user) {
        if (!testRank(user.rank)) {
            return "Rank is lower than the minimum rank. Min rank is " + minimumRank.name() + ", got " + user.rank.name();
        }
        try {
            System.exit(1);
            return "";
        }
        catch (Exception e) {
            return "Invalid usage of command: " + name;
        }
        //return "";
    }

}
