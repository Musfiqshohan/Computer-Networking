import javax.xml.soap.Node;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

class NodeInfo {
    String nextHop, dest;
    int cost;

    NodeInfo(String dest, String nextHop, int cost) {
        this.dest = dest;
        this.nextHop = nextHop;
        this.cost = cost;
    }

    void printNodeInfo()
    {
        System.out.println(dest+" "+nextHop+" "+cost);
    }


}

class Router {

    String routername;
    int port, cost;
    NodeInfo routingTable[] = new NodeInfo[10];


    Router(int idx, String routername, String cost, String port) {
        this.routername = routername;
        this.cost = Integer.valueOf(cost);
        this.port = Integer.valueOf(port);

        for (int i = 0; i < 10; i++) {
            String dest = "";
            dest += (char) (i + 65);

            if (i == idx)
                routingTable[i] = new NodeInfo(dest, dest, 0);

            routingTable[i] = new NodeInfo(dest, "-", 100000);
        }
    }



    void printRouter()
    {
        System.out.println(routername+" "+port);
        for(int i=0;i<10;i++)
        {
            routingTable[i].printNodeInfo();
        }
    }


}

public class Client {



    public static void main(String args[]) throws IOException {


        Scanner sc= new Scanner(System.in);
      //  String mynode = args[0];
        String mynode = sc.next();
        int mynodeIdx = mynode.charAt(0) - 65;
        System.out.println("hi");
        //int port = Integer.valueOf(args[1]);
        String port = (sc.next());

        //Router myrouter = new Router(mynodeIdx,args[0], "0", args[1]);
        Router myrouter = new Router(mynodeIdx, mynode , "0", port);

        //String filename = args[2];

        Router neighbour[] = new Router[10];
    //    for(int i=0;i<10;i++) neighbour[i]=new Router();


        BufferedReader br = new BufferedReader(new FileReader("configA.txt"));
        String line;
        line = br.readLine();
        int nodes = Integer.valueOf(line);

        for (int i = 0; i < nodes; i++) {
            line = br.readLine();
            System.out.println(line);

            String input[] = line.split(" ");
            int dest = input[0].charAt(0) - 65;
            neighbour[dest] = new Router(dest,input[0], input[1], input[2]);
            int cost = Integer.valueOf(input[1]);

            myrouter.routingTable[dest] = new NodeInfo(input[0], input[0], cost);

            neighbour[dest].routingTable[mynodeIdx] = new NodeInfo(mynode, mynode, cost);

        }



//        for(int i=0;i<3;i++)
//        {
//            neighbour[i].printRouter();
//            System.out.println();
//
//        }


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
