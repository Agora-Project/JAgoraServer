package org.agora.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.agora.graph.Graph;
import org.agora.graph.Node;
import org.agora.graph.Edge;
import org.agora.graph.NodeID;
import org.agora.server.logging.Log;
import org.bson.BSONDecoder;
import org.bson.BSONEncoder;
import org.bson.BSONObject;
import org.bson.BasicBSONDecoder;
import org.bson.BasicBSONEncoder;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;

public class JAgoraComms {
  
  
  public static BSONObject readBSONObjectFromSocket(Socket s) {
    try {
      InputStream is = s.getInputStream();
      BSONDecoder bdec = new BasicBSONDecoder();
      BSONObject bson = bdec.readObject(is);
      return bson;
    } catch (IOException e) {
      Log.error("Could not read BSON object from socket " + s);
      Log.error(e.getMessage());
    }
    
    return null;
  }
  
  
  public static boolean writeBSONObjectToSocket(Socket s, BSONObject bson) {
    BSONEncoder benc = new BasicBSONEncoder();
    byte[] b = benc.encode(bson);
    
    try {
      s.getOutputStream().write(b);
      return true;
    } catch (IOException e) {
      Log.error("Could not write BSON object to socket " + s);
      Log.error(e.getMessage());
    }
    
    return false;
  }
  
  public static BSONObject BSONiseNodeID(NodeID nodeID) {
    BSONObject bson = new BasicBSONObject();
    bson.put("source", nodeID.getSource());
    bson.put("id", nodeID.getNumber());
  }
  
  public static BSONObject BSONiseNode(Node node) {
    BSONObject bson = new BasicBSONObject();
    bson.put("id", BSONiseNodeID(node.getID()));
    bson.put("posterName", node.posterName);
    bson.put("posterID", node.posterID);
    bson.put("date", node.date.toString());
    bson.put("acceptability", node.acceptability);
    bson.put("threadID", node.threadID);
    return bson;
  }
  
  public static BSONObject BSONiseEdge(Edge edge) {
    BSONObject bson = new BasicBSONObject();
    bson.put("origin", BSONiseNodeID(edge.getOrigin().getID()));
    bson.put("target", BSONiseNodeID(edge.getTarget().getID()));
    return bson;
  }
  
  public static BSONObject BSONiseGraph(Graph graph) {
    BSONObject bsonGraph = new BasicBSONObject();
    
    // Add nodes.
    BasicBSONList bsonNodeList = new BasicBSONList();
    Node[] nodes = graph.getNodes();
    for (int i = 0; i < nodes.length; i++) {
      bsonNodeList.add(BSONiseNode(nodes[i]));
    }
    
    bsonGraph.put("nodes", bsonNodeList);
    
    // Add edges.
    BasicBSONList bsonEdgeList = new BasicBSONList();
    for (Edge e: graph.edgeMap.values()) {
      bsonNodeList.add(BSONiseEdge(e));
    }
    
    bsonGraph.put("edges", bsonEdgeList);
    
    return bsonGraph;
  }
}




