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

public class DBAddAttack {
  
  
  protected static String ADD_QUERY = "INSERT INTO attacks (arg_ID_attacker, source_ID_attacker,"
                                                         + "arg_ID_defender, source_ID_defender,"
                                                         + "user_ID)"
                                                         + "VALUES (?, ?, ?, ?, ?);";
  
  
  
  public static boolean addAttackToDB(int userID, JAgoraArgumentID attacker, JAgoraArgumentID defender, DatabaseConnection dbc) throws SQLException {
    // TODO: These checks should somehow say that the record already exists.
    if (!DBChecks.argumentExists(attacker, dbc))        return false;
    if (!DBChecks.argumentExists(defender, dbc))        return false;
    if (DBChecks.attackExists(attacker, defender, dbc)) return false;
    
    PreparedStatement ps = dbc.prepareStatement(ADD_QUERY);
    ps.setInt(1, attacker.getLocalID());
    ps.setString(2, attacker.getSource());
    ps.setInt(3, defender.getLocalID());
    ps.setString(4, defender.getSource());
    ps.setInt(5, userID);
    
    ps.addBatch();
    
    int[] res = ps.executeBatch();
    
    ps.close();
    dbc.close();

    return res[0] != 0;
  }
}
