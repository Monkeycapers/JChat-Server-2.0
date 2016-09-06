import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.jar.Pack200;

/**
 * Created by Evan on 8/30/2016.
 */
public class ClientFactory implements Runnable {

    public JServer jServer;

    ArrayList threads;

    boolean isRunning;



    public ClientFactory(JServer jServer) {
        this.jServer = jServer;
        threads = new ArrayList();
        isRunning = true;
    }

    public void run() {
        jServer.serverSocket = null;
        try {
            jServer.serverSocket = new ServerSocket(jServer.portNumber);
        }
        catch (Exception e) {
            System.out.println("Could not create a socket");
        }
        jServer.clients = new ArrayList<ClientWorker>();
        int id = 0;
        while (isRunning) {
            try {
                ClientWorker w;
                w = new ClientWorker(jServer, id, jServer.serverSocket.accept());
                jServer.addClient(w);
                threads.add(w);
                new Thread(w).start();
                id ++;
            }
            catch (Exception e) {
                System.out.println("Could not connect");
            }
        }
    }

}

