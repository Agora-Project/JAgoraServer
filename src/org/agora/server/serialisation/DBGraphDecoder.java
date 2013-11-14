package org.agora.server.serialisation;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.agora.graph.JAgoraEdge;
import org.agora.graph.JAgoraGraph;
import org.agora.graph.JAgoraNode;
import org.agora.graph.JAgoraNodeID;
import org.agora.graph.VoteInformation;
import org.bson.BSONDecoder;
import org.bson.BSONObject;
import org.bson.BasicBSONDecoder;

public class DBGraphDecoder {
  
  protected JAgoraGraph graph;
  
  public DBGraphDecoder() { initialise(); }
  
  public void initialise() { graph = new JAgoraGraph(); }
  
  public JAgoraGraph getGraph() { return graph; }
  
  
  /**
   * Constructs a single node using the current row of the given ResultSet.
   * @param rs
   * @return What you'd expect :D
   * @throws SQLException
   */
  public JAgoraNode loadNodeFromResultSet(ResultSet rs) throws SQLException {
    JAgoraNode node = new JAgoraNode();
    
    // ID
    JAgoraNodeID id = new JAgoraNodeID();
    id.setSource(rs.getString("source_ID"));
    id.setLocalID(rs.getInt("arg_ID"));
    node.construct(id);
    
    // Content
    // TODO: inefficiency ->
    //       we are decoding into BSONObject and then re-encoding into byte[]
    //       to send over the network.
    BSONDecoder bdec = new BasicBSONDecoder();
    byte[] contentBytes = rs.getBytes("content");
    BSONObject contentBSON = bdec.readObject(contentBytes);
    node.setContent(contentBSON);
    
    // Other stuff
    node.setDate(rs.getDate("date"));
    node.setAcceptability(rs.getBigDecimal("acceptability"));
    node.setThreadID(rs.getInt("thread_ID"));
    node.setPosterID(rs.getInt("poster_ID"));
    node.setPosterName(rs.getString("username"));
    
    // Vote information
    node.setVotes(loadVoteInformationFromResultSet(rs));
    
    return node;
  }
  
  /**
   * Constructs nodes and populates the graph from the given query.
   * The query MUST include the username of the user associated with
   * the userID. It must also include all the aggregated vote information in
   * columns called 'positive_votes' and 'negative_votes'.
   * @param rs The query!
   * @return Whether the load was successful.
   * @throws SQLException
   */
  public boolean loadNodesFromResultSet(ResultSet rs) throws SQLException {
    if (graph == null)
      return false;
    
    while (rs.next())
      graph.addNode(loadNodeFromResultSet(rs));
    
    return true;
  }
  
  /**
   * Loads attacks from the ResultSet. It must also include all the aggregated
   * vote information in columns called 'positive_votes' and 'negative_votes'.
   * @param rs
   * @return
   * @throws SQLException
   */
  public boolean loadNodesAttacksFromResultSet(ResultSet rs) throws SQLException {
    // TODO: add user who posted the attack to information
    if (graph == null)
      return false;
    
    while (rs.next()) {
      JAgoraNodeID originID = new JAgoraNodeID(
          rs.getString("source_ID_attacker"),
          rs.getInt("arg_ID_attacker"));
      JAgoraNodeID targetID = new JAgoraNodeID(
          rs.getString("source_ID_attacker"),
          rs.getInt("arg_ID_attacker"));
      JAgoraEdge edge = new JAgoraEdge(graph.getNodeByID(originID),
                                       graph.getNodeByID(targetID));
      edge.setVotes(loadVoteInformationFromResultSet(rs));
      
      graph.addEdge(edge);
    }
    
    return true;
  }
  
  public VoteInformation loadVoteInformationFromResultSet(ResultSet rs) throws SQLException {
    return new VoteInformation(rs.getInt("positive_votes"), rs.getInt("negative_votes"));
  }
}
