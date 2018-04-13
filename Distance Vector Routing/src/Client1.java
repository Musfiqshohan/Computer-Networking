import javax.xml.soap.Node;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

class NodeInfo {
    String dest,nextHop ;
    int cost;

    NodeInfo(String dest, String nextHop, int cost) {
        this.dest = dest;
        this.nextHop = nextHop;
        this.cost = cost;
    }

    String retNodeInfo()
    {
        String ret="";
        ret=dest+" "+nextHop+" "+cost;
        return ret;
    }


}

class Router {

    int port,Rid;

    NodeInfo routingTable[] = new NodeInfo[10];


    Router(int myid, String port) {
        this.Rid = myid;
        this.port = Integer.valueOf(port);

        for (int i = 0; i < 10; i++) {
            String dest = "";
            dest += (char) (i + 65);

            if (i == myid) {
                routingTable[i] = new NodeInfo(dest, dest, 0);
            }
            else
            routingTable[i] = new NodeInfo(dest, "X", 100000);
        }
    }



    void printRouter()
    {
        System.out.println(Rid+" "+port);
        for(int i=0;i<10;i++)
        {
            System.out.println(routingTable[i].retNodeInfo());
        }
    }


}

public class Client1 {

    public static Socket s1;
    public static DataInputStream is;
    public static DataOutputStream os;
    public static Router myrouter;




    public static void establishConnection()  {
        try {
            s1 = new Socket("127.0.0.1", 1234);
            is = new DataInputStream(s1.getInputStream());
            os = new DataOutputStream(s1.getOutputStream());
        }catch (Exception e){}

    }

    public static void sendRoutingTable()
    {
        try {
          String sendData="";
            for(int j=0;j<10;j++)
            {
                sendData= myrouter.routingTable[j].retNodeInfo();

                  //  os.writeBytes(sendData);
                    os.writeUTF(sendData);
                    System.out.println("->"+sendData);

            }
           // os.writeBytes("----");

        }catch (Exception e) {}


    }



    public static void main(String args[]) throws IOException {

       // establishConnection();


        //Scanner sc= new Scanner(System.in);
        String mynode = "A"; //sc.next();
        int mynodeIdx = mynode.charAt(0) - 65;
        String port ="2000"; // (sc.next());

        myrouter = new Router(mynodeIdx, port);




        BufferedReader br = new BufferedReader(new FileReader("configA.txt"));
        String line;
        line = br.readLine();
        int nodes = Integer.valueOf(line);

        for (int i = 0; i < nodes; i++) {
            line = br.readLine();
            //System.out.println(line);

            String input[] = line.split(" ");
            int dest = input[0].charAt(0) - 65;
            int cost = Integer.valueOf(input[1]);

            myrouter.routingTable[dest] = new NodeInfo(input[0], input[0], cost);

        }





       // myrouter.printRouter();

        establishConnection();

        sendRoutingTable();



//
//
//        is.close();
//        os.close();
//        s1.close();
    }

}
