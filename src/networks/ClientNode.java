package networks;

import java.util.Vector;

public class ClientNode extends Node
{
  
  private Sender sender;
  
  public ClientNode(Vector<String> socket_list, int socket)
  {
    super(socket_list, socket);
    this.sender = new Sender("localhost", 2323);
  }

  public void run() 
  {
    for(int i = 0; i < this.socket_list.size(); i++)
    {
      if(i != socket)
      {
        sender = new Sender(this.socket_list.get(i).split("\\s+")[0], Integer.parseInt(this.socket_list.get(i).split("\\s+")[1]));
        break;
      }
    }
    // TODO: Code to start sender thread
  }
  
  public void kill()
  {
    // TODO: Code to kill sender thread
  }
  
}
