import java.io.*;

/**
 * Created by Evan on 9/2/2016.
 */
public class Authenticate {

    static String userFile = System.getProperty("user.home") + "\\AppData\\Roaming\\JChat\\Server\\Users";

    public static User login (String user, String pass) {
        try {
            FileInputStream fis = new FileInputStream(new File(userFile));

            //Construct BufferedReader from InputStreamReader
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            while ((line = br.readLine()) != null) {
                //System.out.println("Now parsing: " + line);
                String[] split = line.split(",");
                String suser = split[0];
                String spass = split[1];
                String srank = split[2];
                //System.out.println(suser + "," + spass + "," + srank);
                if (suser.equals(user) && (BCrypt.checkpw(pass, spass))) {
                    //Move user to top of stack
                    return new User(Rank.valueOf(srank), user);
                }
            }

            br.close();
        }
        catch (Exception e) {
            return new User();
        }
        return new User();
    }
    public static User signup (String user, String pass) {
        try {
            FileInputStream fis = new FileInputStream(new File(userFile));
            //Construct BufferedReader from InputStreamReader
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String total = "";
            String line = null;
            while ((line = br.readLine()) != null) {
                total += line + "\n";
            }
            br.close();
            PrintWriter pw = new PrintWriter(new FileWriter(userFile));
            pw.print(total);
            pw.print(user + "," + BCrypt.hashpw(pass, BCrypt.gensalt()) + "," + Rank.User.name());
            pw.close();
            return new User(Rank.User, user);
        }
        catch (Exception e) {
            return new User();
        }
    }
    public static boolean update (boolean UpdateType, User user) {
        try {
            FileInputStream fis = new FileInputStream(new File(userFile));
            //Construct BufferedReader from InputStreamReader
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String total = "";
            String line = null;
            String text = "";
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                if (split[0].equals(user.username)) {
                    if (UpdateType) {
                        text = user.username + "," + split[1] + "," + user.rank.name() + "\n";
                    }
                    else {
                        //Don't add the line, essentially deleting the user
                    }
                }
                else {
                    total += line + "\n";
                }
            }
            br.close();
            PrintWriter pw = new PrintWriter(new FileWriter(userFile));
            pw.print(text + total);
            pw.close();
            return true;
        }
        catch (Exception e) {
            System.out.println("Error in signing up..." + e.toString());
        }
        return false;
    }
}
