package networks;

import java.io.Serializable;
import java.time.Instant;

public class Packet implements Serializable
{

  private static final long serialVersionUID = 1L;
  private final String ip;
  private final int port;
  private int active;
  private int server;
  private Instant timestamp;
  
  public Packet(String ip, int port, int active, int server)
  {
    this.ip = ip;
    this.port = port;
    this.setActiveFlag(active);
    this.setServerFlag(server);
    this.timestamp = Instant.now();
  }
  
  public void setActiveFlag(int flag)
  {
    if(flag < -1 || flag > 1)
    {
      this.active = -1;
    }
    else
    {
      this.active = flag;
    }
    this.timestamp = Instant.now();
  }
  
  public void setServerFlag(int flag)
  {

    if(flag < -1 || flag > 1)
    {
      this.server = -1;
    }
    else
    {
      this.server = flag;
    }
    this.timestamp = Instant.now();
  }
  
  public String getIP()
  {
    return this.ip;
  }
  
  public int getPort()
  {
    return this.port;
  }
  
  public int isActive()
  {
    return this.active;
  }
  
  public int isServer()
  {
    return this.server;
  }
  
  public Instant getTimestamp()
  {
    return this.timestamp;
  }
  
}
