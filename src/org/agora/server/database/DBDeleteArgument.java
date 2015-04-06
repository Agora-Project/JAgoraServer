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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.agora.graph.JAgoraArgumentID;
import org.agora.server.DatabaseConnection;



public class DBDeleteArgument {
  
  protected static String DELETE_ARGUMENT_QUERY = "DELETE FROM arguments WHERE arg_id = ?;";
  
  protected static String DELETE_ATTACKS_QUERY = "DELETE FROM arguments WHERE (arg_ID_attacker = ? " + 
                                                "AND source_ID_attacker = ?) OR (arg_ID_defender = ? AND source_ID_defender = ?)";
  
  public static boolean deleteArgumentFromDB(JAgoraArgumentID id, int userID, DatabaseConnection dbc) throws SQLException {
    
    PreparedStatement ps = dbc.prepareStatement(DELETE_ARGUMENT_QUERY);
    ps.setInt(1, id.getLocalID());
    
    ps.addBatch();
    
    int[] res = ps.executeBatch();
    
    if(res[0] == 0) return false;
    
    ps = dbc.prepareStatement(DELETE_ATTACKS_QUERY);
    ps.setInt(1, id.getLocalID());
    ps.setString(2, id.getSource());
    ps.setInt(3, id.getLocalID());
    ps.setString(4, id.getSource());
    
    ps.addBatch();
    
    res = ps.executeBatch();
    
    ps.close();
    dbc.close();

    return res[0] != 0;
  }
  
}
