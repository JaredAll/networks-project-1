package networks;

import java.util.Vector;

public abstract class Node extends Thread
{

  protected Vector<String> socket_list = new Vector<String>();
  protected int socket;

  public Node(Vector<String> socket_list, int socket)
  {
    this.socket_list = socket_list;
    this.socket = socket;
  }
  
}
