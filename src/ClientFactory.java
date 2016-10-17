import org.java_websocket.WebSocket;

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

    int id;



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
        id = 0;
        Logger.logMessage("[Info]: Listening for clients on port: " + jServer.portNumber);
        while (isRunning) {
            try {
                ClientWorker w;
                w = new ClientWorker(jServer, id, jServer.serverSocket.accept());
                Logger.logMessage("[Info]: Client joined, assigning id" + id);
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

    public void addWebClient(WebSocket webSocket) {
        try {
            ClientWorker w;
            w = new ClientWorker(jServer, id, webSocket);
            Logger.logMessage("[Info]: Client joined, assigning id" + id);
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

