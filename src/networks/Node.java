package networks;

import java.util.Vector;

public abstract class Node extends Thread
{

  protected Vector<String> ip_list = new Vector<String>();
  protected Vector<Integer> port_list = new Vector<Integer>();
  protected PacketList packet_list;
  protected byte[] packet_list_data;
  protected int socket_number;
  protected boolean active;
  protected boolean server;
  
  public Node(Vector<String> socket_list, int socket_number)
  {
    for(int i = 0; i < socket_list.size(); i++)
    {
      this.ip_list.add(socket_list.get(i).split("\\s+")[0]);
      this.port_list.add(Integer.parseInt(socket_list.get(i).split("\\s+")[1]));
    }
    this.socket_number = socket_number;
    this.active = true;
    this.packet_list_data = null;
    this.packet_list = new PacketList(ip_list.size());
  }
  
}
