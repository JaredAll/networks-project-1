package networks;

import java.util.Vector;

public class ServerNode extends Node
{

  private Vector<Sender> senders = new Vector<Sender>();
  private Listener listener;
  
  public ServerNode(Vector<String> socket_list, int socket)
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
      }
      else
      {
        senders.add(new Sender(this.socket_list.get(i).split("\\s+")[0], Integer.parseInt(this.socket_list.get(i).split("\\s+")[1])));
      }
    }
    // TODO: Code to start sender and listener threads
  }
  
  public void kill()
  {
    // TODO: Code to kill all threads
  }
  
}