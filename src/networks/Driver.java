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
    Vector<String> socket_list = new Vector<String>();
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
           socket_list.add(str);
           str = "";
         }
      }
    } finally
    {
      if (in != null)
      {
         in.close();
      }
    }
    
    // Verify sockets are read in properly
    /*
    for(int i = 0; i < socket_list.size(); i++)
    {
      System.out.println(socket_list.get(i));
    }
    */
    
    // Selecting mode: P2P, Server, or Client
    Scanner reader = new Scanner(System.in);
    Node node = null;
    int input = 0;
    boolean mode_selected = false;
    while(!mode_selected)
    {
      System.out.println("Select mode: \n  1. P2P\n  2. Server\n  3. Client");
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
      if(input < 1 || input > 3)
      {
        System.out.println("Incorrect selection\n");
        continue;
      }
      mode_selected = true;
    }
    
    // Starting correct node type
    switch(input)
    {
      case 1: // P2PNode
        node = new P2PNode();
        node.start();
        break;
      case 2: // ServerNode
        node = new ServerNode();
        node.start();
        break;
      case 3: // ClientNode
        node = new ClientNode();
        node.start();
        break;
    }
    
    // Post start options
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
          node.kill();
          option_selected = true;
      }
    }
    
    reader.close();

  }

}
