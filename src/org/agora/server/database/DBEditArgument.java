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
import java.sql.SQLException;
import org.agora.graph.JAgoraArgumentID;
import org.agora.server.DatabaseConnection;
import org.bson.BSONEncoder;
import org.bson.BSONObject;
import org.bson.BasicBSONEncoder;

/**
 *
 * @author greg
 */
public class DBEditArgument {
    
    protected static String EDIT_QUERY = "UPDATE arguments SET content = ? WHERE arg_id = ? AND user_id = ?;";
    
    public static boolean editArgumentOnDB(BSONObject content, JAgoraArgumentID nodeID, int userID, DatabaseConnection dbc) throws SQLException {
        PreparedStatement ps = dbc.prepareStatement(EDIT_QUERY);
        
        BSONEncoder benc = new BasicBSONEncoder();
        byte[] b = benc.encode(content); 
    
        ps.setBinaryStream(1, new ByteArrayInputStream(b));
        
        ps.setInt(2, nodeID.getLocalID());
        
        ps.setInt(3, userID);
        
        ps.addBatch();
    
        int[] res = ps.executeBatch();
    
        ps.close();
        dbc.close();

        return res[0] != 0;
    }

}
