package networks;

import java.util.Vector;

public class P2PNode extends Node
{
  
  private Vector<Sender> senders = new Vector<Sender>();
  private Listener listener;
  
  public P2PNode(Vector<String> socket_list, int socket)
  {
    super(socket_list, socket);
  }

  public void run() 
  {
    for(int i = 0; i < this.socket_list.size(); i++)
    {
      if(i == socket)
      {
        listener = new Listener(Integer.parseInt(this.socket_list.get(i).split("\\s+")[1]));
        System.out.println("Listener on port " + Integer.parseInt(this.socket_list.get(i).split("\\s+")[1]));
      }
      else
      {
        senders.add(new Sender(this.socket_list.get(i).split("\\s+")[0], Integer.parseInt(this.socket_list.get(i).split("\\s+")[1])));
        System.out.println("Sender to " + this.socket_list.get(i).split("\\s+")[0] + " on port " + Integer.parseInt(this.socket_list.get(i).split("\\s+")[1]));
      }
    }
    listener.start();
    for(int i = 0; i < senders.size(); i++)
    {
      senders.get(i).start();
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
