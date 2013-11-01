package org.agora.server;

public class UserSession {

  protected String username;
  protected int userID;
  protected String token;
  
  public UserSession(String username, int userID, String token) {
    this.username = username;
    this.userID = userID;
    this.token = token;
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
  public String getToken() { return token; }
}
