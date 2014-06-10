package org.agora.server.database;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.agora.graph.JAgoraNodeID;
import org.agora.server.DatabaseConnection;
import org.bson.BSONEncoder;
import org.bson.BSONObject;
import org.bson.BasicBSONEncoder;
import org.bson.BasicBSONObject;

/**
 *
 * @author greg
 */
public class DBEditArgument {
    
    protected static String EDIT_QUERY = "UPDATE arguments SET content = ? WHERE arg_id = ?;";
    
    public static boolean editArgumentOnDB(BSONObject content, JAgoraNodeID nodeID, int userID, DatabaseConnection dbc) throws SQLException {
        PreparedStatement ps = dbc.prepareStatement(EDIT_QUERY);
        
        BSONEncoder benc = new BasicBSONEncoder();
        byte[] b = benc.encode(content); 
    
        ps.setBinaryStream(1, new ByteArrayInputStream(b));
        
        ps.setInt(2, nodeID.getLocalID());
        
        //ps.setInt(3, userID);
        
        ps.addBatch();
    
        int[] res = ps.executeBatch();
    
        ps.close();
        dbc.close();

        return res[0] != 0;
    }

}
