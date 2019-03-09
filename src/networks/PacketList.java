package networks;

import java.io.Serializable;
import java.net.InetAddress;

public class PacketList implements Serializable
{

  private static final long serialVersionUID = 1L;
  private Packet[] packet_list;
  
  public PacketList(int size)
  {
    this.packet_list = new Packet[size];
  }
  
  public void setPacket(int i, Packet packet)
  {
    this.packet_list[i] = packet;
  }
  
  public Packet getPacket(int i)
  {
    return this.packet_list[i];
  }
  
  public int getLength()
  {
    return this.packet_list.length;
  }
  
  public int findPacket(String IPAddress)
  {
    int packetLocation = 0;
    for( int i = 0; i < packet_list.length; i++ )
    {
      if( packet_list[ i ].getIP().equals( IPAddress ) )
      {
        packetLocation = i;
      }
    }
    return packetLocation;
  }
  
  public void display()
  {
    System.out.println("Packet:");
    for(int i = 0; i < packet_list.length; i++)
    {
      System.out.println("  [" + packet_list[i].getIP() + ":" + packet_list[i].getPort()
          + "]\n  Active: " + packet_list[i].isActive()
          + "\n  " + packet_list[i].getTimestamp().toString() + "\n");
    }
  }

}
