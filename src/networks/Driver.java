package networks;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class Driver
{

  public static void main(String[] args) throws IOException
  {

    // Read in sockets.txt and store in socket_list
    FileReader in = null;
    Vector<String> node_list = new Vector<String>();
    try
    {
      in = new FileReader("sockets.txt");
      int c;
      String str = "";
      while((c = in.read()) != -1)
      {
         str += (char)c;
         if((char)c == '\n')
         {
           node_list.add(str);
           str = "";
         }
      }
    }
    finally
    {
      if (in != null)
      {
         in.close();
      }
    }
    
    // Selecting mode: P2P, Server, or Client
    Scanner reader = new Scanner(System.in);
    int mode = 0;
    boolean mode_selected = false;
    while(!mode_selected)
    {
      System.out.println("Select mode: \n  1. P2P\n  2. Server\n  3. Client");
      try
      {
        mode = reader.nextInt();
      }
      catch(Exception e)
      {
        System.out.println("Invalid input\n");
        reader.nextLine(); // Clear input buffer
        continue;
      }
      if(mode < 1 || mode > 3)
      {
        System.out.println("Incorrect selection\n");
        continue;
      }
      mode_selected = true;
    }
    
    // Selecting socket
    int nodeNum = 0;
    boolean node_selected = false;
    
    while(!node_selected)
    {
      System.out.println("Select node:");
      for(int i = 1; i <= node_list.size(); i++)
      {
        System.out.println("  " + i + ". " + node_list.get(i - 1));
      }
      try
      {
        nodeNum = reader.nextInt() - 1;
      }
      catch(Exception e)
      {
        System.out.println("Invalid input\n");
        reader.nextLine(); // Clear input buffer
        continue;
      }
      if(nodeNum < 0 || nodeNum >= node_list.size())
      {
        System.out.println("Incorrect selection\n");
        continue;
      }
      node_selected = true;
    }
    
    // Starting correct node type
    Node node = null;
    switch(mode)
    {
      case 1: // P2PNode
        node = new P2PNode(node_list, nodeNum);
        node.start();
        break;
      case 2: // ServerNode
        node = new ServerNode(node_list, nodeNum);
        node.start();
        break;
      case 3: // ClientNode
        node = new ClientNode(node_list, nodeNum);
        node.start();
        break;
    }
    
    // Post start options
    int input = 0;
    boolean option_selected = false;
    while(!option_selected)
    {
      // TODO: Add more options? Do we need more post options?
      System.out.println("\n1. Exit");
      try
      {
        input = reader.nextInt();
      }
      catch(Exception e)
      {
        System.out.println("Invalid input\n");
        reader.nextLine(); // Clear input buffer
        continue;
      }
      if(input < 1 || input > 1)
      {
        System.out.println("Incorrect selection\n");
        continue;
      }
      switch(input)
      {
        case 1: // Exit
          switch(mode)
          {
            case 1: // P2PNode
              ((P2PNode)node).kill();
              break;
            case 2: // ServerNode
              ((ServerNode)node).kill();
              break;
            case 3: // ClientNode
              ((ClientNode)node).kill();
              break;
          }
          option_selected = true;
          break;
      }
    }
    
    reader.close();

  }

}
