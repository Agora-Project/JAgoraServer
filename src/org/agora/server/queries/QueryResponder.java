package org.agora.server.queries;

import org.agora.server.JAgoraServer;
import org.bson.BasicBSONObject;

public interface QueryResponder {
  BasicBSONObject respond(BasicBSONObject query, JAgoraServer server);
}
