import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Evan on 9/22/2016.
 */
public class Logger {
    public static String logFile = System.getProperty("user.home") + "\\AppData\\Roaming\\JChat\\Server\\Logs\\" + new SimpleDateFormat("yyyy").format(new Date()) + "\\" + new SimpleDateFormat("MMMM").format(new Date()) + "\\" +   new SimpleDateFormat("dd").format(new Date()) + " JChat.log";

    public static void logMessage(String message) {
        try {
            File file = new File(logFile);
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileInputStream fis = new FileInputStream(file);
            //Construct BufferedReader from InputStreamReader
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String total = "";
            String line = "";
            while ((line = br.readLine()) != null) {
                total += line + "\n";
            }
            br.close();
            PrintWriter pw = new PrintWriter(new FileWriter(file));
            pw.println(total + new SimpleDateFormat("[MMMM dd yyyy hh:mm.ss a]").format(new Date()) + " " +  message);
            pw.close();
        }
        catch (Exception e) {

        }
    }
}
