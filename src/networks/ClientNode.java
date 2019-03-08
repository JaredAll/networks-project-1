package networks;

import java.util.Vector;
import java.util.Timer;
import java.util.TimerTask;

public class ClientNode extends Node
{
  
  private Vector<Sender> senders = new Vector<Sender>();
  private Listener listener;
  private Vector<Integer> reportingNodes = new Vector<Integer>();
  private int serverNodeNum;
  private boolean isServer = false;
  
  public ClientNode(Vector<String> node_list, int nodeNum)
  {
    super(node_list, nodeNum);
    this.server = false;
    for(int i = 0; i < this.packet_list.getLength(); i++)
    {
      if( i == 0 )
      {
        reportingNodes.add(i);
      }
      if(i == this.nodeNum)
      {
        packet_list.setPacket(i, new Packet(this.ip_list.get(i), this.port_list.get(i), 1, 0));
        setIP(this.ip_list.get(i));
      }
      else
      {
        packet_list.setPacket(i, new Packet(this.ip_list.get(i), this.port_list.get(i), -1, -1)); 
      }
    }
    serverNodeNum = 0;
  }

  public void run() 
  {
    for(int i = 0; i < this.ip_list.size(); i++)
    {
      if(i == nodeNum)
      {
        listener = new Listener(this.IPAddress, this.port_list.get(i), this.packet_list, 
            reportingNodes, nodeNum);
        listener.start();
      }
    }

    //only create sender if you are not going to be the first server
    if( serverNodeNum != nodeNum )
    {
      senders.add( new Sender( this.ip_list.get( serverNodeNum ), 
          this.port_list.get( serverNodeNum ), this.packet_list ) );
      senders.get( senders.size() - 1 ).start();
    }
    
    //remain a client until the listener says to change
    try
    {
      Thread.sleep(500);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
    System.out.println();
    
    
    
    //periodically check for quorum votes and results
    Timer timer = new Timer();
    TimerTask serverCheck = new TimerTask()
    {
      public void run()
      {
        if( listener.becomeServer() && !isServer )
        {
          //if listener says to become server, add senders and start them up
          createSenders();
          serverNodeNum = nodeNum;
          isServer = true;
        }
        
        boolean castingVote = listener.castQuorum();
        
        if( castingVote )
        {
          int newServerNodeNum = 0;
          
          if( serverNodeNum == nodeNum )
          {
            newServerNodeNum = nodeNum;
          }
          else
          {
            newServerNodeNum = serverNodeNum + 1;
          }
          
          if( newServerNodeNum == nodeNum )
          {
            listener.voteForSelf();
          }
          else if( newServerNodeNum != serverNodeNum )
          {
            System.out.println(newServerNodeNum);
            senders.add( new Sender( getIpList().get( newServerNodeNum ), 
                getPortList().get( newServerNodeNum ), getPacketList() ) );
            senders.get(senders.size() - 1).start();
          }
        }
        
        //only maintain one sender if you are client 
        if( !listener.becomeServer() && !isServer )
        {
          System.out.println("I am client");
          for( int i = 0; i < senders.size(); i++ )
          {
            senders.get(i).kill();
          }
          senders.clear();
          
          senders.add( new Sender( getIpList().get( serverNodeNum ), 
              getPortList().get( serverNodeNum ), getPacketList() ) );
          senders.get(senders.size() - 1).start();
          
        }
          
      }
    };
    
    long delay = 45000L;
    long period = 45000L;
    timer.scheduleAtFixedRate(serverCheck, delay, period);
    
  }
  
  public void kill()
  {
    listener.kill();
    for(int i = 0; i < senders.size(); i++)
    {
      senders.get(i).kill();
    }
  }
  
  /**
   * creates senders if client is to become server
   */
  private void createSenders()
  {
    
    for( int i = 0; i < senders.size(); i++ )
    {
      senders.get(i).kill();
    }
    senders.clear();
    
    for( int i = 1; i < ip_list.size(); i++ )
    {
      if( i != serverNodeNum )
      {
        senders.add( new Sender( this.ip_list.get(i), this.port_list.get(i), this.packet_list ));
      }
    }
    
    for( int i = 0; i < senders.size(); i++ )
    {
      senders.get(i).start();
    }
  }
  
  private Vector<String> getIpList()
  {
    return ip_list;
  }
  
  private Vector<Integer> getPortList()
  {
    return port_list;
  }
  
  private PacketList getPacketList()
  {
    return packet_list;
  }
  
}
