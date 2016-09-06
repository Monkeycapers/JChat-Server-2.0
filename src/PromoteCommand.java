import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Evan on 9/3/2016.
 */
public class PromoteCommand extends Command {
    JServer jServer;
    public PromoteCommand (JServer jServer, int id) {
        this.jServer = jServer;
        this.name = "/promote";
        this.minimumRank = Rank.Op;
        this.clientId = id;
    }
    @Override
    public String parse(String message, User user) {
        if (!testRank(user.rank)) {
            return "Rank is lower than the minimum rank. Min rank is " + minimumRank.name() + ", got " + user.rank.name();
        }
        try {
            String[] split = message.split(",");
            User target = new User(Rank.valueOf(split[3]), split[2]);
            List arrayList = Arrays.asList(rankOrder);
            if (arrayList.indexOf(user.rank) <= arrayList.indexOf(target.rank)) {
                return "You are a lower or equal rank to your target";
            }
            Boolean b = Authenticate.update(true, target);
            //Todo: user jServer to update the client
            if (b) {
                try {
                    jServer.getClient(target).user = target;
                }
                catch (Exception e) {
                    //User is offline
                }
                return "Promoted user: " + target.username + " to: " + target.rank.name();
            }
            else {
                return "Could not promote user.";
            }


        }
        catch (Exception e) {
            return "Invalid usage of command: Signup";
        }
    }
}
