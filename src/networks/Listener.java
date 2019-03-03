package networks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Listener extends Thread
{
  
  DatagramSocket socket = null;
  private int port;
  private boolean running;
  private PacketList packet_list;
  
  public Listener(int port, PacketList packet_list)
  {
    this.port = port;
    this.running = false;
    this.packet_list = packet_list;
  }
  
  public void run() 
  {
    System.out.println("Listener on port " + this.port);
    this.running = true;
    while(running)
    {
      try 
      {
        socket = new DatagramSocket(this.port);
        byte[] incomingData = new byte[1024];
        DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
        socket.receive(incomingPacket);

        PacketList packet_list = (PacketList)deserializePacketList(incomingPacket.getData());
        
        InetAddress IPAddress = incomingPacket.getAddress();
        int port = incomingPacket.getPort();
        
        System.out.println("Packets received from [" + IPAddress.getHostAddress() + ":" + port + "]");
        packet_list.display();
        
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
  
  public void kill()
  {
    this.running = false;
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
