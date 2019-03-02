package networks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Sender extends Thread
{

  DatagramSocket Socket;
  private String host;
  private int port;
  private boolean running;
  private byte[] data;
  private PacketList packet_list;

  public Sender(String host, int port, PacketList packet_list) 
  {
    this.host = host;
    this.port = port;
    this.running = false;
    this.setPacketList(packet_list);
  }

  public void run() 
  {
    this.running = true;
    while(running)
    {
      try
      {
        Socket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(this.host);
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, this.port);
        Socket.send(sendPacket);
        System.out.println("Message sent from client");
        Socket.close();
      }
      catch (UnknownHostException e) 
      {
        e.printStackTrace();
      } 
      catch (SocketException e) 
      {
        e.printStackTrace();
      } 
      catch (IOException e) 
      {
        e.printStackTrace();
      }
      try
      {
        Thread.sleep(5000);
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
    }
  }
  
  public void kill()
  {
    this.running = false;
  }
  
  public void setPacketList(PacketList packet_list)
  {
    this.packet_list = packet_list;
    this.data = serializePacketList();
  }
  
  private byte[] serializePacketList()
  {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutput out = null;
    try
    {
      out = new ObjectOutputStream(bos);   
      out.writeObject(packet_list);
      out.flush();
      return bos.toByteArray();
    }
    catch (IOException e)
    {
      // Ignore exception
    }
    finally
    {
      try
      {
        bos.close();
      }
      catch (IOException ex)
      {
        // Ignore close exception
      }
    }
    return null;
  }
  
  
}
