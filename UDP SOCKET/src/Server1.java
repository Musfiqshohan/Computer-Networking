import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.net.*;



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


public class Server1 extends Thread{



    public static Router myrouter;
    public static EchoClient client;
    public static EchoServer server;

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[100];


    public Server1(int port) throws Exception {
        socket = new DatagramSocket(port);
    }

    public void run() {


        try {
            running = true;

            int cnt=0;
            while (running) {

                buf= new byte[100];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                //System.out.println("Client:-" + data(buf));

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                String received=data(buf); // new String(packet.getData(), 0, packet.getLength());
                //System.out.println(received);

                updateRoutingTable(received);


                //  System.out.println("iterate: "+(++cnt));
                // System.out.println("r:  " +received);

                if (received.equals("end")) {
                    running = false;
                    continue;
                }
                socket.send(packet);
            }
            socket.close();
        } catch (Exception e) {
        }
    }


    public static String data(byte[] a)
    {
        if (a == null)
            return null;
        String ret = new String();
        int i = 0;
        while (a[i] != 0)
        {
            ret+=((char) a[i]);
            i++;
        }
        return ret;
    }







    public static void sendRoutingTable(String mynode)
    {
        try {
            String sendData="";
            for(int j=0;j<10;j++)
            {
                sendData= myrouter.routingTable[j].retNodeInfo();
                sendData=mynode+" "+sendData;


                //  osr.writeBytes(sendData);
                client.sendEcho(sendData, 2002);

                //oss.writeUTF(sendData);
                //System.out.println("->"+sendData);

            }
            // osr.writeBytes("----");

        }catch (Exception e) {}

    }


    public static int updateRoutingTable(String line) {

        try {


            //String Info[]= new String[4]
            String Info[] = line.split(" ");
            int cost= Integer.valueOf(Info[3]);
            int sender=Integer.valueOf(Info[0].charAt(0)-'A');
            int to=Integer.valueOf(Info[1].charAt(0)-'A');


            //System.out.println(Info[0]+" "+Info[1]+" "+Info[2]+"->"+Info[3]);
            //System.out.println(j+"->"+myrouter.routingTable[j].retNodeInfo()+ " "+(2+ cost));
                if(myrouter.routingTable[to].cost> myrouter.routingTable[sender].cost+ cost)   //need change
                {
                    myrouter.routingTable[to].cost=myrouter.routingTable[sender].cost+ cost;
                    myrouter.routingTable[to].nextHop=myrouter.routingTable[sender].dest;

                }




        } catch (Exception e) {
            System.out.println(e);
        }
        return 0;

    }


    public  static  void prepareRoutingTable()
    {
        try {
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

        }catch (Exception e){
            System.out.println(e);
        }

    }






    public static void main(String[] args) throws  Exception {


        String mynode = "A";
        int mynodeIdx = mynode.charAt(0) - 65;
        String port ="2001";
        myrouter = new Router(mynodeIdx, port);


        prepareRoutingTable();



           client=new EchoClient();

        Server1 server= new Server1(2001);
        server.start();

        boolean running=true;

        while(running) {

            Thread.sleep(3000);

            myrouter.printRouter();
            sendRoutingTable(mynode);

       // System.out.println(client.sendEcho("hello to Server2", 2002));
        //System.out.println(client.sendEcho("hello to Server3", 2223));

        }

    }


}



