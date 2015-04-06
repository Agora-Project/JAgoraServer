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

public class DBAddVote {

  protected static String ADD_ARGUMENT_VOTE_QUERY = "INSERT INTO votes (type, user_ID, source_ID, arg_ID) VALUES (?, ?, ?, ?);";
  
  public static boolean addArgumentVoteToDB(JAgoraArgumentID nodeID, int voteType, int userID, DatabaseConnection dbc) throws SQLException {
    // TODO: return error somehow.
    if(!DBChecks.argumentExists(nodeID, dbc))
      return false;
    
    PreparedStatement ps = dbc.prepareStatement(ADD_ARGUMENT_VOTE_QUERY);
    ps.setInt(1, voteType);
    ps.setInt(2, userID);
    ps.setString(3, nodeID.getSource());
    ps.setInt(4, nodeID.getLocalID());
    
    ps.addBatch();
    
    int[] res = ps.executeBatch();
    
    ps.close();
    dbc.close();
    
    // TODO: can't it happen that res.length == 0?
    return res[0] != 0;
  }
  
  
  protected static String ADD_ATTACK_VOTE_QUERY = "INSERT INTO votes (type, user_ID, source_ID_attacker, arg_ID_attacker, source_ID_defender, arg_ID_defender) VALUES (?, ?, ?, ?, ?, ?);";
  
  public static boolean addAttackVoteToDB(JAgoraArgumentID attackerID, JAgoraArgumentID defenderID, int voteType, int userID, DatabaseConnection dbc) throws SQLException {
    // TODO: return error somehow.
    if(!DBChecks.attackExists(attackerID, defenderID, dbc)) return false;
    
    PreparedStatement ps = dbc.prepareStatement(ADD_ATTACK_VOTE_QUERY);
    ps.setInt(1, voteType);
    ps.setInt(2, userID);
    ps.setString(3, attackerID.getSource());
    ps.setInt(4, attackerID.getLocalID());
    ps.setString(5, defenderID.getSource());
    ps.setInt(6, defenderID.getLocalID());
    
    ps.addBatch();
    
    int[] res = ps.executeBatch();
    
    ps.close();
    dbc.close();
    
    // TODO: can't it happen that res.length == 0?
    return res[0] != 0;
  }
}
