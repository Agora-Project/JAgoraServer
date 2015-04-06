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

package org.agora.server.queries;

import java.sql.Statement;
import java.util.ArrayList;
import org.agora.graph.JAgoraThread;
import org.agora.lib.BSONThreadListEncoder;
import org.agora.lib.IJAgoraLib;
import org.agora.logging.Log;
import org.agora.server.DatabaseConnection;
import org.agora.server.JAgoraServer;
import org.agora.server.database.DBGraphDecoder;
import org.bson.BasicBSONObject;

/**
 *
 * @author greg
 */
public class ThreadListResponder implements QueryResponder {

  @Override
  public BasicBSONObject respond(BasicBSONObject query, JAgoraServer server) {

    //boolean verified = server.verifySession(query);
    BasicBSONObject bsonResponse = new BasicBSONObject();

    try (DatabaseConnection dbc = server.createDatabaseConnection()) {
      if (dbc == null) {
        Log.error("[ThreadListResponder] Could not connect to database.");
        bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
        bsonResponse.put(IJAgoraLib.REASON_FIELD, "Server failure.");
        return bsonResponse;
      }

      Statement s = dbc.produceStatement();
      if (s == null) {
        Log.error("[ThreadListResponder] Could not create statement.");
        bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
        bsonResponse.put(IJAgoraLib.REASON_FIELD, "Server failure.");
        return bsonResponse;
      }

      ArrayList<JAgoraThread> threads;
      DBGraphDecoder dgd = new DBGraphDecoder();
      threads = dgd.getThreads(s);

      BSONThreadListEncoder enc = new BSONThreadListEncoder();
      BasicBSONObject bsonThreads = enc.BSONiseThreadList(threads);

      // Add it to the response
      bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_OK);
      bsonResponse.put(IJAgoraLib.THREAD_LIST_FIELD, bsonThreads);
      return bsonResponse;

    } catch (Exception e) {
      Log.error("[ThreadListResponder] Could not execute query (" + e.getMessage() + ")");
      bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
      bsonResponse.put(IJAgoraLib.REASON_FIELD, "Server failure.");
      return bsonResponse;
    }
  }

}
