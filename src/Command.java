import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Evan on 8/31/2016.
 */
public class Command {

    String name = "Test";

    ReturnType returnType = ReturnType.MessageSender;

    final Rank[] rankOrder = {Rank.Banned, Rank.Guest, Rank.User, Rank.Op, Rank.Admin};

    Rank minimumRank = Rank.Guest;

    int clientId;

    public String parse(String message, User user) { return "";}

    public boolean testRank(Rank rank) {
        if (java.util.Arrays.asList(rankOrder).indexOf(rank) >= java.util.Arrays.asList(rankOrder).indexOf(minimumRank)) {
            //User's rank is higher than the min rank
            return true;
        }
        return false;
    }

}
