
package org.agora.server;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;

/**
 *
 * @author angle
 */

public class JAgoraWebSocketServlet extends Endpoint {
    
    private Session session;
    
    @OnOpen
    public void start(Session session) {
        this.session = session;
    }

    @OnClose
    public void end() {
 	
    }
 	
    @OnMessage
    public void incoming(String message) {
 	
    }
 	
    @OnError
    public void onError(Throwable t) throws Throwable {
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
