package networks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Listener extends Thread
{
  
  DatagramSocket socket = null;
  private int port;
  private boolean running;
  
  public Listener(int port)
  {
    this.port = port;
    this.running = false;
  }
  
  public void run() 
  {
    this.running = true;
    while(running)
    {
      try 
      {
        socket = new DatagramSocket(this.port);
        byte[] incomingData = new byte[1024];
        
        DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
        socket.receive(incomingPacket);
        String message = new String(incomingPacket.getData());
        InetAddress IPAddress = incomingPacket.getAddress();
        int port = incomingPacket.getPort();
        
        System.out.println("Received message from client: " + message);
        System.out.println("Client IP:"+IPAddress.getHostAddress());
        System.out.println("Client port:"+port);
        
        String reply = "Thank you for the message";
        byte[] data = reply.getBytes();
        
        DatagramPacket replyPacket = new DatagramPacket(data, data.length, IPAddress, port);
        
        socket.send(replyPacket);
        Thread.sleep(2000);
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
