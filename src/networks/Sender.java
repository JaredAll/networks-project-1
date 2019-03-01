package networks;

import java.io.IOException;
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
  private String message;

  public Sender(String host, int port) 
  {
    this.host = host;
    this.port = port;
    this.running = false;
    this.message = "Hotdogs";
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
        byte[] incomingData = new byte[1024];
        byte[] data = this.message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, this.port);
        Socket.send(sendPacket);
        System.out.println("Message sent from client");
        DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
        Socket.receive(incomingPacket);
        String response = new String(incomingPacket.getData());
        System.out.println("Response from server:" + response);
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
  
}
