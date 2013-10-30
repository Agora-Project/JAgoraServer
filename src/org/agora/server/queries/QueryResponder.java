package org.agora.server.queries;

import org.agora.server.JAgoraServer;
import org.bson.BSONObject;

public interface QueryResponder {
  BSONObject respond(BSONObject query, JAgoraServer server);
}
