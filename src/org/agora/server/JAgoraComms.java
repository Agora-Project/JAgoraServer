package org.agora.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Date;

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
  
  // ***********************
  // **** Communication ****
  // ***********************
  public static BSONObject readBSONObjectFromSocket(Socket s) {
    try {
      InputStream is = s.getInputStream();
      BSONDecoder bdec = new BasicBSONDecoder();
      return bdec.readObject(is);
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
  
  
 // ***********************
 // **** Serialisation ****
 // ***********************
  
  public static BSONObject BSONiseNodeID(NodeID nodeID) {
    BSONObject bson = new BasicBSONObject();
    bson.put("source", nodeID.getSource());
    bson.put("id", nodeID.getLocalID());
    return bson;
  }
  
  public static BSONObject BSONiseNode(Node node) {
    BSONObject bson = new BasicBSONObject();
    bson.put("id", BSONiseNodeID(node.getID()));
    bson.put("posterName", node.getPosterName());
    bson.put("posterID", node.getPosterID());
    bson.put("date", node.getDate().toString());
    bson.put("acceptability", node.getAcceptability());
    bson.put("threadID", node.getThreadID());
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
  
 /*
 *************************
 **** Deserialization ****
 *************************
 */

  public static NodeID deBSONiseNodeID(BSONObject bsonNodeID) {
    return new NodeID(
        (String)bsonNodeID.get("source"),
        (Integer)bsonNodeID.get("id")
    );
  }
  
  public static Node deBSONiseNode(BSONObject bsonNode) {
    NodeID nodeID = deBSONiseNodeID((BSONObject)bsonNode.get("id"));
    Node node = new Node(nodeID);
    
    node.setPosterID((Integer)bsonNode.get("id"));
    node.setPosterName((String)bsonNode.get("posterName"));
    // TODO: this is gong to break
    node.setDate((Date)bsonNode.get("date"));
    node.setAcceptability((Integer)bsonNode.get("acceptability"));
    node.setThreadID((Integer)bsonNode.get("threadID"));

    return node;
  }
  
  public static Edge deBSONiseEdge(BSONObject bsonEdge, Graph graph) {
    NodeID originID = deBSONiseNodeID((BSONObject)bsonEdge.get("origin"));
    NodeID targetID = deBSONiseNodeID((BSONObject)bsonEdge.get("target"));


    // Check whether the origin nodes are or are not in the graph.
    // If they are not, simply add a node containing only an ID.
    // That should be enough to ask for it in case it's interesting.
    Node originNode = null;
    Node targetNode = null;

    if (graph.isInGraph(originID)) originNode = graph.getNodeByID(originID);
    else                           originNode = new Node(originID);

    if (graph.isInGraph(targetID)) targetNode = graph.getNodeByID(targetID);
    else                           targetNode = new Node(targetID);

    return new Edge(originNode, targetNode);
  }
  
  public static Graph deBSONiseGraph(BSONObject bsonGraph) {
    Graph graph = new Graph();
    BasicBSONList nodes = (BasicBSONList)bsonGraph.get("nodes");
    for (Object n: nodes)
      graph.addNode(deBSONiseNode((BSONObject) n));

    BasicBSONList edges = (BasicBSONList)bsonGraph.get("edges");
    for (Object e : edges)
      graph.addEdge(deBSONiseEdge((BSONObject) e, graph));

    return graph;
  }
}




