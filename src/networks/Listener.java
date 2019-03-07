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
    
    TimerTask timeout = new TimerTask()
    {
      public void run()
      {
        System.out.print("WE have entered the run\n");
        int inactive = 0;
        for( int i = 0; i < packet_list.getLength(); i++ )
        {
          System.out.println(i);
          if( updatedPackets != null && !updatedPackets.contains(i) && reportingNodes.contains(i) )
          {
            System.out.println("^this is being set to inactive");
            packet_list.getPacket(i).setActiveFlag( inactive );              
          }
        }
        
        //if no packet received from Server, cast vote for new server
        if( updatedPackets.size() == 1 )
        {
          castQuorum = true;
        }
        else
        {
          castQuorum = false;
        }
        
        if( updatedPackets != null )
        {
          clearUpdatedPackets();
        }
        
        if( quorum > packet_list.getLength() / 2)
        {
          kill();
          becomeServer = true;
        }
        else
        {
          quorum = 0;
        }
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
      
      markUpdatedPacket( packet_list.findPacket(host));
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
        
        System.out.println("IP address " + IPAddress.getHostAddress() + " found at " + packet_list.findPacket(IPAddress.getHostAddress()));
        markUpdatedPacket( packet_list.findPacket( IPAddress.getHostAddress() ) );
        
        if( silentNodes.contains(packet_list.findPacket(IPAddress.getHostAddress())) )
        {
          quorum += 1;
        }
        
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
  
  private void copyPacket(int packetLocation, Packet newPacket)
  {
    packet_list.setPacket( packetLocation, newPacket );
  }
  
  public void kill()
  {
    this.running = false;
  }
  
  public boolean castQuorum()
  {
    return castQuorum;
  }
  
  private void updatePacket( int packetLocation )
  {
    int active = 1;
    packet_list.getPacket(packetLocation).setActiveFlag( active );
  }
  
  private void markUpdatedPacket( int packetPos )
  {
    updatedPackets.add(packetPos);
  }
  
  private void clearUpdatedPackets()
  {
    updatedPackets.clear();
  }
  
  public boolean becomeServer()
  {
    return becomeServer;
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
