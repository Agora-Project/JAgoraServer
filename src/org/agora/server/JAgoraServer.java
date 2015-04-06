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

package org.agora.server;

import java.net.Socket;
import java.util.concurrent.*;

import org.agora.server.queries.*;
import org.bson.BasicBSONObject;

public interface JAgoraServer {

  public QueryResponder getResponder(int operation);

  public DatabaseConnection createDatabaseConnection();

  /**
   * Initiates login and session management for the given user.
   *
   * @param user The username
   * @param userID The userID (from the DB)
   * @return The new UserSession
   */
  public UserSession userLogin(String user, int userID, int userType);

  public UserSession getSession(int userID);

  public boolean verifySession(int userID, String sessionID);

  public boolean verifySession(BasicBSONObject query);

  public boolean logoutUser(int userID);

  public BlockingQueue<Socket> getRequestQueue();
}
