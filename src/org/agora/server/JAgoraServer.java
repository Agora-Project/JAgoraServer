package org.agora.server;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;

public class JAgoraServer implements Daemon {

  @Override
  public void destroy() {
    System.out.println("[JAgoraServer] Destroying!");
  }

  @Override
  public void init(DaemonContext context) throws DaemonInitException, Exception {
    System.out.println("[JAgoraServer] initialising!");
  }

  @Override
  public void start() throws Exception {
    System.out.println("[JAgoraServer] Starting!");
  }

  @Override
  public void stop() throws Exception {
    System.out.println("[JAgoraServer] Stopping!");
  }

}
