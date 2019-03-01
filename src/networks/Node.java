package networks;

public abstract class Node extends Thread
{

  protected boolean running;

  public Node()
  {
    this.running = false;
  }
  
  public void kill()
  {
    this.running = false;
  }
  
}
