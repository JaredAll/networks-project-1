package networks;

import java.util.Vector;

public class P2PNode extends Node
{
  
  private Vector<Sender> senders = new Vector<Sender>();
  private Listener listener;
  
  public P2PNode(Vector<String> socket_list, int socket_number)
  {
    super(socket_list, socket_number);
    this.server = false;
    for(int i = 0; i < this.packet_list.getLength(); i++)
    {
      if(i == this.socket_number)
      {
        packet_list.setPacket(i, new Packet(this.ip_list.get(i), this.port_list.get(i), 1, -1));        
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
      if(i == socket_number)
      {
        listener = new Listener(this.port_list.get(i));
        System.out.println("Listener on port " + this.port_list.get(i));
        listener.start();
      }
      else
      {
        senders.add(new Sender(this.ip_list.get(i), this.port_list.get(i), this.packet_list));
        System.out.println("Sender to " + this.ip_list.get(i) + " on port " + this.port_list.get(i));
        senders.get(senders.size() - 1).start();
      }
    }
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
