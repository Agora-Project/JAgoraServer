package org.agora.server.queries;

import com.mongodb.DBDecoder;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.agora.graph.JAgoraArgumentID;
import org.agora.lib.BSONGraphDecoder;
import org.agora.lib.IJAgoraLib;
import org.agora.logging.Log;
import org.agora.server.JAgoraServer;
import org.agora.server.database.DBEditArgument;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

/**
 *
 * @author greg
 */
public class EditArgumentResponder implements QueryResponder {

    @Override
    public BasicBSONObject respond(BasicBSONObject query, JAgoraServer server) {
        BasicBSONObject bsonResponse = new BasicBSONObject();
        
        boolean verified = server.verifySession(query);
    
        if (!verified) {
            bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
            bsonResponse.put(IJAgoraLib.REASON_FIELD, "Invalid session ID.");
            return bsonResponse;
        }
        
        try {
            BSONObject content = (BSONObject)query.get(IJAgoraLib.CONTENT_FIELD);
            int userID = query.getInt(IJAgoraLib.USER_ID_FIELD);
            JAgoraArgumentID nodeID = new BSONGraphDecoder().deBSONiseNodeID((BasicBSONObject)query.get(IJAgoraLib.ARGUMENT_ID_FIELD));
            
            boolean res = DBEditArgument.editArgumentOnDB(content, nodeID, userID, server.createDatabaseConnection());
            if (res) {
                bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_OK);
            } else {
                bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
                bsonResponse.put(IJAgoraLib.REASON_FIELD, "Attack already existed (with high probability!)");
            }
      
            return bsonResponse;
        } catch (SQLException e) {
            Log.error("[EditArgumentResponder] Could not execute edit argument query ("+e.getMessage()+")");
            bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
            bsonResponse.put(IJAgoraLib.REASON_FIELD, "Server failure.");
            return bsonResponse;
        } finally {
            
        }
    }

}
