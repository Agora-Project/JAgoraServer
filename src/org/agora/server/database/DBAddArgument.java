/*

Copyright (C) 2015 Agora Communication Corporation

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

package org.agora.server.database;

import java.io.ByteArrayInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.agora.server.DatabaseConnection;
import org.agora.server.Options;
import org.bson.BSONEncoder;
import org.bson.BSONObject;
import org.bson.BasicBSONEncoder;
import org.bson.BasicBSONObject;

public class DBAddArgument {

  protected static String ADD_QUERY = "INSERT INTO arguments (source_ID, thread_ID, user_ID, content) VALUES (?, ?, ?, ?);";
  
  protected static String CHECK_LATEST_ARGUMENT_QUERY = "SELECT LAST_INSERT_ID();";
  
  public static BasicBSONObject addArgumentToDB(BSONObject content, int threadID, int userID, DatabaseConnection dbc) throws SQLException {
    
    PreparedStatement ps = dbc.prepareStatement(ADD_QUERY);
    ps.setString(1, Options.SERVER_URL);
    ps.setInt(2, threadID);
    ps.setInt(3, userID);
    
    BSONEncoder benc = new BasicBSONEncoder();
    byte[] b = benc.encode(content); 
    
    ps.setBinaryStream(4, new ByteArrayInputStream(b));
    
    ps.addBatch();
    
    ps.executeBatch();
    ps = dbc.prepareStatement(CHECK_LATEST_ARGUMENT_QUERY);
    
    ResultSet res = ps.executeQuery();
    
    BasicBSONObject ret = new BasicBSONObject();
    ret.put("Source", Options.SERVER_URL);
    res.next();
    ret.put("ID", res.getInt(1));
    ps.close();
    dbc.close();

    return ret;
  }
}
