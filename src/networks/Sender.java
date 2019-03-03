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
import java.util.Random;

public class Sender extends Thread
{

  DatagramSocket Socket;
  private String host;
  private int port;
  private boolean running;
  private byte[] data;
  private PacketList packet_list;
  private Random rand;

  public Sender(String host, int port, PacketList packet_list) 
  {
    this.host = host;
    this.port = port;
    this.running = false;
    this.packet_list = packet_list;
    this.data = serializePacketList();
    this.rand = new Random();
  }

  public void run()
  {
    System.out.println("Sender to " + this.host + " on port " + this.port);
    this.running = true;
    while(running)
    {
      try
      {
        Thread.sleep(rand.nextInt(10000) + 5000); // Sleep 5 - 15 seconds
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
      try
      {
        Socket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(this.host);
        this.data = serializePacketList();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, this.port);
        Socket.send(sendPacket);
        System.out.println("Packets sent to [" + this.host + ":" + this.port + "]\n");
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
    }
  }
  
  public void kill()
  {
    this.running = false;
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
      e.printStackTrace();
    }
    finally
    {
      try
      {
        bos.close();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    return null;
  }
  
  
}
