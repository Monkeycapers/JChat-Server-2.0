/**
 * Created by Evan on 9/22/2016.
 */
public class CardGameCommand extends Command {
    JServer jServer;
    public CardGameCommand(JServer jServer, int id) {
        this.jServer = jServer;
        this.name = "/cardgame";
        this.minimumRank = Rank.Guest;
        this.clientId = id;
        this.returnType = ReturnType.MessageNone;
    }
    @Override
    public String parse(String message, User user) {
        if (!testRank(user.rank)) {
            return "Rank is lower than the minimum rank. Min rank is " + minimumRank.name() + ", got " + user.rank.name();
        }
        try {
            String[] split = message.split(",");

            if (split[3].startsWith("join")) {

            }
            else if (split[3].startsWith("start")) {

            }
            else if (split[3].startsWith("command")) {

            }

        }
        catch (Exception e) {
            return "Invalid usage of command: " + name;
        }
        return "";
    }

}
