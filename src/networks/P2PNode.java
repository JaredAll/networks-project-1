package networks;

import java.util.Vector;

public class P2PNode extends Node
{
  
  private Vector<Sender> senders = new Vector<Sender>();
  private Listener listener;
  private Vector<Integer> reportingNodes = new Vector<Integer>();
  
  public P2PNode(Vector<String> node_list, int paramNodeNum)
  {
    super(node_list, paramNodeNum);
    this.server = false;
    
    //initialize packets
    for(int i = 0; i < this.packet_list.getLength(); i++)
    {
      if(i == this.nodeNum)
      {
        packet_list.setPacket(i, new Packet(this.ip_list.get(i), this.port_list.get(i), 1, -1));
        setIP( this.ip_list.get(i) );
      }
      else
      {
        packet_list.setPacket(i, new Packet(this.ip_list.get(i), this.port_list.get(i), -1, -1)); 
        reportingNodes.add(i);
      }
    }
  }

  public void run() 
  {
    for(int i = 0; i < this.ip_list.size(); i++)
    {
      if(i == this.nodeNum)
      {
        listener = new Listener(this.IPAddress, this.port_list.get(i), this.packet_list, 
            reportingNodes, nodeNum);
        listener.start();
      }
      else
      {
        senders.add(new Sender(this.ip_list.get(i), this.port_list.get(i), this.packet_list));
        senders.get(senders.size() - 1).start();
      }
    }
    System.out.println();
  }
  
  public void kill()
  {
    listener.kill();
    for(int i = 0; i < senders.size(); i++)
    {
      senders.get(i).kill();
    }
  }
  
}
