package networks;

import java.util.Vector;

public class ClientNode extends Node
{
  
  private Sender sender;
  
  public ClientNode(Vector<String> socket_list, int socket_number)
  {
    super(socket_list, socket_number);
    this.server = false;
  }

  public void run() 
  {
    for(int i = 0; i < this.ip_list.size(); i++)
    {
      if(i != socket_number)
      {
        //this.sender = new Sender(this.ip_list.get(i), this.port_list.get(i));
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
