import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Evan on 8/31/2016.
 */
public class User {
    Rank rank;
    String username;

    public User(Rank r, String un) {
        rank = r;
        username = un;
    }
    public User() {
        rank = Rank.Guest;
        username = "Anon";
    }
}
