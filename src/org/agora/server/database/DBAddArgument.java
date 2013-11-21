package org.agora.server.database;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.agora.lib.IJAgoraLib;
import org.agora.server.DatabaseConnection;
import org.bson.BSONEncoder;
import org.bson.BSONObject;
import org.bson.BasicBSONEncoder;
import org.bson.BasicBSONObject;

public class DBAddArgument {

  public static boolean addArgumentToDB(BasicBSONObject request, DatabaseConnection dbc) throws SQLException {
    BSONObject content = (BSONObject)request.get(IJAgoraLib.CONTENT_FIELD);
    int threadID = request.getInt(IJAgoraLib.THREAD_ID_FIELD);
    int userID = request.getInt(IJAgoraLib.USER_ID_FIELD);
    
    PreparedStatement ps = dbc.prepareStatement("INSERT INTO arguments (thread_ID, user_ID, content) VALUES (?, ?, ?);");
    ps.setInt(1, threadID);
    ps.setInt(2, userID);
    
    BSONEncoder benc = new BasicBSONEncoder();
    byte[] b = benc.encode(content); 
    
    ps.setBinaryStream(3, new ByteArrayInputStream(b));
    
    ps.addBatch();
    
    int[] res = ps.executeBatch();
    // TODO Commits before it closes?
    dbc.close();

    return res[0] != 0;
  }
}
