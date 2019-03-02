package networks;

import java.util.Vector;

public class ClientNode extends Node
{
  
  private Sender sender;
  private Listener listener;
  
  public ClientNode(Vector<String> socket_list, int socket_number)
  {
    super(socket_list, socket_number);
    this.server = false;
    for(int i = 0; i < this.packet_list.getLength(); i++)
    {
      if(i == this.socket_number)
      {
        packet_list.setPacket(i, new Packet(this.ip_list.get(i), this.port_list.get(i), 1, 0));        
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
        listener.start();
      }
    }
    sender = new Sender(this.ip_list.get(0), this.port_list.get(0), this.packet_list);
    sender.start();
    try
    {
      Thread.sleep(500);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
    System.out.println();
  }
  
  public void kill()
  {
    listener.kill();
    sender.kill();
  }
  
}
