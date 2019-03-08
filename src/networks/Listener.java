package networks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Timer;
import java.util.Vector;
import java.util.TimerTask;

public class Listener extends Thread
{
  
  DatagramSocket socket = null;
  private int port;
  private boolean running;
  private PacketList packet_list;
  private String host;
  private Vector<Integer> updatedPackets;
  private Vector<Integer> reportingNodes;
  private Vector<Integer> silentNodes;
  private int quorum;
  private boolean castQuorum;
  private boolean becomeServer = false;
  private int myNode;
  private boolean switchServer = false;
  
  public Listener(String paramHost, int port, PacketList packet_list, 
      Vector<Integer> paramReportingNodes, int paramMyNode )
  {
    this.port = port;
    this.running = false;
    this.packet_list = packet_list;
    host = paramHost;
    this.updatedPackets = new Vector<Integer>();
    reportingNodes = paramReportingNodes;
    silentNodes = new Vector<Integer>();
    quorum = 0;
    myNode = paramMyNode;
    castQuorum = false;
    
    
    //keep track of who to receive from
    for( int i = 0; i < packet_list.getLength(); i++ )
    {
      if( !reportingNodes.contains(i))
      {
        silentNodes.add(i);
      }
    }
  }
  
  public void run() 
  {
    System.out.println("Listener on (" + host + ", " + this.port + ")" );
    this.running = true;
    
    Timer timer = new Timer();
    
    
    /*
     * This task handles listener timeouts
     */
    TimerTask timeout = new TimerTask()
    {
      public void run()
      {
        int inactive = 0;
        for( int i = 0; i < packet_list.getLength(); i++ )
        {
          //if no packets received from last timeout, set node as inactive
          if( updatedPackets != null && !updatedPackets.contains(i) && i != myNode )
          {
            packet_list.getPacket(i).setActiveFlag( inactive );              
          }
        }
        
        //if no packet received from Server, cast vote for new server
        if( updatedPackets.size() == 0 )
        {
          castQuorum = true;
        }
        else
        {
          castQuorum = false;
        }
        
        if( quorum > packet_list.getLength() / 2)
        {
          becomeServer = true;
        }
        else
        {
          quorum = 0;
        }
        updatedPackets.clear();
      }
    };
    
    long delay = 30000L;
    long period = 30000L;
    timer.scheduleAtFixedRate(timeout, delay, period);

    while(running)
    { 
      //if this node becomes the server, it will update reporting and silent nodes
      if( becomeServer )
      {
        reportingNodes.clear();
        for( int i = 0; i < packet_list.getLength(); i++ )
        {
          if( i != myNode )
          {
            reportingNodes.add(i);
          }
        }
      }
      
      //a node always updates its own packet
      markUpdatedPacket( packet_list.findPacket(host) );
      try 
      {
        socket = new DatagramSocket(this.port);
        byte[] incomingData = new byte[1024];
        
            
        DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
        socket.receive(incomingPacket);

        PacketList new_packet_list = (PacketList)deserializePacketList(incomingPacket.getData());
        
        InetAddress IPAddress = incomingPacket.getAddress();
        int port = incomingPacket.getPort();
        
        System.out.println("Packets received from [" + IPAddress.getHostAddress() + ":" + port + "]");
        new_packet_list.display();
        
        //update incoming packets as they arrive 
        markUpdatedPacket( packet_list.findPacket( IPAddress.getHostAddress() ) );
        
        int senderPos = packet_list.findPacket(IPAddress.getHostAddress());
        
        //receive from someone who's supposed to be quiet
        if( silentNodes.contains( senderPos ) )
        {
          //and you voted, then they must be the server
          if( castQuorum )
          {
            reportingNodes.clear();
            silentNodes.clear();
            reportingNodes.add( senderPos );
            for( int i = 0; i < packet_list.getLength(); i++ )
            {
              if( i != senderPos )
              {
                silentNodes.add(i);
              }
            }
          }
          else //otherwise, they voted for you
          {
            quorum += 1;
          }
        }
        
        //copy info on silent nodes from the reporting nodes
        for( int i = 0; i < packet_list.getLength(); i++ )
        {
          if( updatedPackets.contains(i) )
          {
            for( int j = 0; j < silentNodes.size(); j++)
            {
              copyPacket( silentNodes.get( j ), new_packet_list.getPacket(silentNodes.get(j)));
            }
            updatePacket( i );
          }
        }
        socket.close();
      } 
      catch (SocketException e) 
      {
        e.printStackTrace();
      } 
      catch (IOException i) 
      {
        i.printStackTrace();
      }
    }
  }
  
  /**
   * adds the packet to the list in the location
   * @param packetLocation the location in the list
   * @param newPacket the packet to be inserted
   */
  private void copyPacket(int packetLocation, Packet newPacket)
  {
    packet_list.setPacket( packetLocation, newPacket );
  }
  
  /**
   * kills the listener thread
   */
  public void kill()
  {
    this.running = false;
  }
  
  /**
   * check to see if Listener wants to cast quorum vote
   * @return true if casting, false otherwise
   */
  public boolean castQuorum()
  {
    return castQuorum;
  }
  
  /**
   * set packet as active
   * @param packetLocation the packet to be updated
   */
  private void updatePacket( int packetLocation )
  {
    int active = 1;
    packet_list.getPacket(packetLocation).setActiveFlag( active );
  }
  
  /**
   * mark packet as updated
   * @param packetPos the position of the packet in the packetList
   */
  private void markUpdatedPacket( int packetPos )
  {
    updatedPackets.add(packetPos);
  }
  
  /**
   * cast quorum vote for yourself
   */
  public void voteForSelf()
  {
    quorum += 1;
  }
  
  /**
   * check to see if client is to become server
   * @return true if it is to become server, false otherwise
   */
  public boolean becomeServer()
  {
    return becomeServer;
  }
  
  /**
   * check to see if client needs to switch servers
   * @return true if it is to switch, false otherwise
   */
  public boolean switchServer()
  {
    return switchServer;
  }
  
  /**
   * determine who the server is 
   * @return the position of the server in the packetList
   */
  public int findServer()
  {
    int serverNum = 0;
    for( int i = 0; i < packet_list.getLength(); i++ )
    {
      if( updatedPackets.contains(i))
      {
        serverNum = i;
      }
    }
    return serverNum;
  }
  
  /**
   * check to see if the listener received a packet
   * @return true if it has, false otherwise
   */
  public boolean receivedPacket()
  {
    boolean receivedPacket = false;
    if( updatedPackets.size() > 0 )
    {
      receivedPacket = true;
    }
    return receivedPacket;
  }
  
  private Object deserializePacketList(byte[] data)
  {
    ByteArrayInputStream bis = new ByteArrayInputStream(data);
    ObjectInput in = null;
    try
    {
      in = new ObjectInputStream(bis);
      Object packet_list = in.readObject();
      return packet_list;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
    finally
    {
      try
      {
        if (in != null)
        {
          in.close();
        }
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    return null;
  }
  
}
