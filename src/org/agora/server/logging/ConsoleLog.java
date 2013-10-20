package org.agora.server.logging;

import java.io.PrintWriter;

public class ConsoleLog extends WriterLog {
  public ConsoleLog() {
    super(new PrintWriter(System.out), new PrintWriter(System.err), new PrintWriter(System.out));
  }
}
