import javax.xml.soap.Node;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

class NodeInfo{
    String nextHop,dest;
    int cost;
    NodeInfo(String dest,String nextHop, int cost)
    {
        this.dest=dest;
        this.nextHop=nextHop;
        this.cost=cost;
    }

}

class Router{

    String routername;
    int port,cost;
    NodeInfo routingTable[]=new NodeInfo[10];


    Router(String routername, String cost, String port)
    {
        this.routername=routername;
        this.cost=Integer.valueOf(cost);
        this.port=Integer.valueOf(port);

        for(int i=0;i<10;i++)
        {
            String dest="";
            dest+=(char)(i+65);
            routingTable[i]=new NodeInfo( dest , "-" , 100000);
        }
    }

}

public class Client {





    public static void main(String args[]) throws IOException {


        Router mine= new Router(args[0], "0", args[1]);

        String filename=args[2];

        Router neighbour[]=new Router[10];


        BufferedReader br= new BufferedReader(new FileReader("configA.txt"));
        String line;
        line=br.readLine();
        int nodes= Integer.valueOf(line);

        for(int i=0;i<nodes;i++)
        {
            line=br.readLine();
            String input[]=line.split(" ");
            neighbour[i]=new Router(input[0], input[1], input[2]);
        }





//        Socket s1 = new Socket("127.0.0.1", 1234);
//        Scanner sc= new Scanner(System.in);
//
//        DataInputStream is;
//        DataOutputStream os;
//
//        is = new DataInputStream(s1.getInputStream());
//        os = new DataOutputStream(s1.getOutputStream());
//
//
//
//        String line = is.readLine();
//
//
//        System.out.println(line);
//
//        String out;
//        out=sc.next();
//
//        os.writeBytes(out);
//        os.writeBytes("from me");
//
//
//        is.close();
//        os.close();
//        s1.close();
    }

}
