package networks;

import java.util.Vector;

public class ServerNode extends Node
{

  private Vector<Sender> senders = new Vector<Sender>();
  private Listener listener;
  
  public ServerNode(Vector<String> socket_list, int socket_number)
  {
    super(socket_list, socket_number);
    this.server = true;
  }

  public void run() 
  {
    for(int i = 0; i < this.ip_list.size(); i++)
    {
      if(i == socket_number)
      {
        listener = new Listener(this.port_list.get(i));
      }
      else
      {
        //senders.add(new Sender(this.ip_list.get(i), this.port_list.get(i)));
      }
    }
    // TODO: Code to start sender and listener threads
  }
  
  public void kill()
  {
    // TODO: Code to kill all threads
  }
  
}