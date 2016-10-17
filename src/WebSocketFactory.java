import org.java_websocket.WebSocket;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * Created by Evan on 10/4/2016.
 */
public class WebSocketFactory extends WebSocketServer {
    JServer jServer;
    public WebSocketFactory(int port, JServer jServer) {
        super(new InetSocketAddress(port));
        this.jServer = jServer;
    }

    @Override
    public void onOpen(WebSocket client, ClientHandshake handshake) {
        client.send("You have connected");
        System.out.println("got a connection: " + client.getRemoteSocketAddress().getAddress().getHostAddress());
        jServer.clientFactory.addWebClient(client);

    }
    @Override
    public void onClose (WebSocket client, int code, String reason, boolean remote) {
        this.sendToAll("lost a client.");
        System.out.println(code + ", " + reason + ", " + remote);
        jServer.removeClient(jServer.getClient(client).id);
    }
    @Override
    public void onMessage(WebSocket client, String message) {
        if (!(message.equals("a"))) {
            //this.sendToAll( message );

            jServer.getClient(client).parseMessage(message);
            //System.out.println(jServer.getClient(client));
        }

    }
    @Override
    public void onFragment(WebSocket client, Framedata fragment) {

    }
    @Override
    public void onError(WebSocket client, Exception ex) {
        ex.printStackTrace();
    }

    public void sendToAll( String text ) {
        Collection<WebSocket> con = connections();
        synchronized ( con ) {
            for( WebSocket c : con ) {
                c.send( text );
            }
        }

    }


}
