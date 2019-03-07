package networks;

import java.util.Vector;
import java.util.Timer;
import java.util.TimerTask;

public class ClientNode extends Node
{
  
  private Vector<Sender> senders = new Vector<Sender>();
  private Listener listener;
  private Vector<Integer> reportingNodes = new Vector<Integer>();
  
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
    
    int serverNodeNum = 0;
    senders.add( new Sender( this.ip_list.get( serverNodeNum ), 
        this.port_list.get( serverNodeNum ), this.packet_list ) );
    senders.get( serverNodeNum ).start();
    
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
    TimerTask becomeServerCheck = new TimerTask()
    {
      public void run()
      {
        if( listener.becomeServer() )
        {
          //if listener says to become server, add senders and start them up
          createSenders();
        }
        
        if( listener.castQuorum() )
        {
          int newServerNodeNum = 0;
          //determine new server node number
          if( newServerNodeNum == serverNodeNum )
          {
            newServerNodeNum += 1;
          }
          
          if( newServerNodeNum == nodeNum )
          {
            newServerNodeNum += 1;
          }
          senders.add( new Sender( getIpList().get( newServerNodeNum ), 
              getPortList().get( newServerNodeNum ), getPacketList() ) );
        }
        
        if( !listener.castQuorum() && !listener.becomeServer())
        {
          senders.clear();
          senders.add( new Sender( getIpList().get( serverNodeNum ), 
              getPortList().get( serverNodeNum ), getPacketList() ) );
        }
          
      }
    };
    
    long delay = 45000L;
    long period = 45000L;
    timer.scheduleAtFixedRate(becomeServerCheck, delay, period);
    
  }
  
  public void kill()
  {
    listener.kill();
    for(int i = 0; i < senders.size(); i++)
    {
      senders.get(i).kill();
    }
  }
  
  private void createSenders()
  {
    for( int i = 1; i < ip_list.size(); i++ )
    {
      senders.add( new Sender( this.ip_list.get(i), this.port_list.get(i), this.packet_list ));
    }
    
    for( int i = 1; i < ip_list.size(); i++ )
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
