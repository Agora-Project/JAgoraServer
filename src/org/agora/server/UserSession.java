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

public class UserSession {

  protected String username;
  protected int userID;
  protected String sessionID;
  protected int userType;
  
  public UserSession(String username, int userID, String sessionID, int userType) {
    this.username = username;
    this.userID = userID;
    this.sessionID = sessionID;
    this.userType = userType;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + userID;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    UserSession other = (UserSession) obj;
    if (userID != other.userID)
      return false;
    return true;
  }

  public String getUsername() { return username; }
  public int getUserID() { return userID; }
  public String getSessionID() { return sessionID; }
  public int getUserType() { return userType; }
  
  public boolean hasPostingPrivilege() { return userType != 1; }
  public boolean hasModeratingPrivilege() { return userType == 2; }
}
