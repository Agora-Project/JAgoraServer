
package org.agora.server;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.agora.lib.IJAgoraLib;
import org.agora.lib.JAgoraComms;
import org.agora.server.queries.AddArgumentResponder;
import org.agora.server.queries.AddArgumentVoteResponder;
import org.agora.server.queries.AddAttackResponder;
import org.agora.server.queries.AddAttackVoteResponder;
import org.agora.server.queries.EditArgumentResponder;
import org.agora.server.queries.LoginResponder;
import org.agora.server.queries.LogoutResponder;
import org.agora.server.queries.QueryArgumentByIDResponder;
import org.agora.server.queries.QueryResponder;
import org.agora.server.queries.RegisterResponder;
import org.agora.server.queries.ThreadByIDResponder;
import org.agora.server.queries.ThreadListResponder;
import org.bson.BSONEncoder;
import org.bson.BasicBSONEncoder;
import org.bson.BasicBSONObject;

/**
 *
 * @author angle
 */
@ServerEndpoint(value = "/Agora/Websocket")
public class JAgoraWebSocketServlet implements JAgoraServer{
    
    private Session session;
    
    protected Map<Integer, QueryResponder> responders;
    
    public JAgoraWebSocketServlet() {
        
    }
    
    @OnOpen
    public void start(Session session) {
        this.session = session;
        
        initialiseResponders();
        
        readConfigurationFiles();
    }

    @OnClose
    public void end() {
 	
    }
 	
    @OnMessage
    public boolean incoming(byte[] message) {
 	BasicBSONObject request = JAgoraComms.readBSONObjectFromStream(
                new DataInputStream(new ByteArrayInputStream(message)));
        if (request == null)
            return false;
    
        BasicBSONObject response = processBSONRequest(request);
        if (response == null)
            return false;
        
        BSONEncoder benc = new BasicBSONEncoder();
        byte[] b = benc.encode(response);
        session.getAsyncRemote().sendObject(ByteBuffer.wrap(b));
        return true;
    }
    
    public BasicBSONObject processBSONRequest(BasicBSONObject request) {
    int requestType = (Integer)request.get(IJAgoraLib.ACTION_FIELD);
    QueryResponder r = this.getResponder(requestType);
    if (r == null) {
      BasicBSONObject response = new BasicBSONObject();
      response.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
      response.put(IJAgoraLib.REASON_FIELD, "Cannot handle this request.");
      return response;
    }
    return r.respond(request, this);
  }
 	
    @OnError
    public void onError(Throwable t) throws Throwable {
    }

    @Override
    public QueryResponder getResponder(int operation) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DatabaseConnection createDatabaseConnection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public UserSession userLogin(String user, int userID, int userType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public UserSession getSession(int userID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean verifySession(int userID, String sessionID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean verifySession(BasicBSONObject query) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean logoutUser(int userID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BlockingQueue<Socket> getRequestQueue() {
        throw new UnsupportedOperationException("Will never be supported, use JAgoraSocketServer instead."); //To change body of generated methods, choose Tools | Templates.
    }
    
    protected void readConfigurationFiles() {
      Options.readDBConfFromFile();
      Options.readAgoraConfFromFile();
    }
    
    protected void initialiseResponders() {
    responders = new HashMap<Integer, QueryResponder>();
    responders.put(IJAgoraLib.LOGIN_ACTION, new LoginResponder());
    responders.put(IJAgoraLib.LOGOUT_ACTION, new LogoutResponder());
    responders.put(IJAgoraLib.QUERY_BY_THREAD_ID_ACTION, new ThreadByIDResponder());
    responders.put(IJAgoraLib.ADD_ARGUMENT_ACTION, new AddArgumentResponder());
    responders.put(IJAgoraLib.ADD_ATTACK_ACTION, new AddAttackResponder());
    responders.put(IJAgoraLib.ADD_ARGUMENT_VOTE_ACTION, new AddArgumentVoteResponder());
    responders.put(IJAgoraLib.ADD_ATTACK_VOTE_ACTION, new AddAttackVoteResponder());
    responders.put(IJAgoraLib.REGISTER_ACTION, new RegisterResponder());
    responders.put(IJAgoraLib.QUERY_THREAD_LIST_ACTION, new ThreadListResponder());
    responders.put(IJAgoraLib.EDIT_ARGUMENT_ACTION, new EditArgumentResponder());
    responders.put(IJAgoraLib.QUERY_BY_ARGUMENT_ID_ACTION, new QueryArgumentByIDResponder());
  }
    
}
