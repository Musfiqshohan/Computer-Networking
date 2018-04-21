import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.net.*;
import java.util.ArrayList;




class SendThread2 extends Thread{


    int sendToPort;
    String mynode;
    public EchoClient client;
    public boolean running;         //##
    public boolean isAlive=false;  // :(
    public int killed=0;       // :(


    SendThread2(String mynode, String sendToPort)
    {
        try {

            this.sendToPort=Integer.valueOf(sendToPort) ;
            this.mynode=mynode;
            client = new EchoClient();

        }catch (Exception e){
            System.out.println(e);}
    }

    public void run()
    {

        try {
            running= true;
            while(running) {


                // int sendToPort = Integer.valueOf(neighbour.get(i).port);
                // System.out.println("Sending to " + sendToPort);

                String sendData = "";
                for (int j = 0; j < 10; j++) {
                    sendData = Server2.myrouter.routingTable[j].retNodeInfo();
                    sendData = mynode + " " + sendData;
                    // System.out.println("s: " + sendData);
                    client.sendEcho(sendData, sendToPort);
                    isAlive=true;   //:(
                    killed=0;

                }
                System.out.println("sending to port: "+sendToPort);
                Thread.sleep(4000);  // :(

            }


        }catch (Exception e) {
            System.out.println(e);
        }


    }

}




public class Server2 extends  Thread{



    public static Router myrouter;
    public static EchoClient client;

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[100];
    public static ArrayList<Neighbour>neighbour= new ArrayList<Neighbour>();




    public Server2(int port) throws Exception {
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


                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                String received=data(buf);

                updateRoutingTable(received);

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

            for(int i=0;i<neighbour.size();i++) {

                int sendToPort=Integer.valueOf(neighbour.get(i).port);
                System.out.println("Sending to "+sendToPort);

                String sendData = "";
                for (int j = 0; j < 10; j++) {
                    sendData = myrouter.routingTable[j].retNodeInfo();
                    sendData = mynode + " " + sendData;
                    System.out.println("s: "+sendData);
                    client.sendEcho(sendData, sendToPort);

                }

            }


        }catch (Exception e) {
            System.out.println(e);
        }

    }


    public static int updateRoutingTable(String line) {

        try {



            String Info[] = line.split(" ");
            int cost= Integer.valueOf(Info[3]);
            int sender=Integer.valueOf(Info[0].charAt(0)-'A');
            int to=Integer.valueOf(Info[1].charAt(0)-'A');

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


    public  static  void prepareRoutingTable(String filename)
    {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            line = br.readLine();
            int nodes = Integer.valueOf(line);

            for (int i = 0; i < nodes; i++) {
                line = br.readLine();
                String input[] = line.split(" ");
                int dest = input[0].charAt(0) - 65;
                int cost = Integer.valueOf(input[1]);

                myrouter.routingTable[dest] = new NodeInfo(input[0], input[0], cost);
                neighbour.add(new Neighbour(input[0], input[2], cost));

            }

        }catch (Exception e){
            System.out.println(e);
        }

    }



    public static void main(String[] args) throws  Exception {


        String mynode = "B";
        int mynodeIdx = mynode.charAt(0) - 65;
        String myport ="2001";
        myrouter = new Router(mynodeIdx, myport);


        prepareRoutingTable("configB.txt");



        client=new EchoClient();

        Server2 server= new Server2(Integer.valueOf(myport));
        server.start();

        ///Under Construction

        Thread.sleep(5000);
        SendThread2 sendThread[]= new SendThread2[15];
        for(int i=0;i<neighbour.size();i++)  //neighbour.size()
        {
            sendThread[i]=new SendThread2(mynode, neighbour.get(i).port);
            sendThread[i].start();

        }




        boolean running=true;


        System.out.println("start checking");
        while(running) {

            Thread.sleep(3000);
            System.out.println("Mystatus:");
            myrouter.printRouter();


            ///Construction
            for(int i=0;i<neighbour.size();i++)
            {
                if(sendThread[i].isAlive==false) {
                    sendThread[i].running = false;

                    if(sendThread[i].killed==0) {
                        sendThread[i] = new SendThread2(mynode, neighbour.get(i).port);
                        sendThread[i].killed++;
                        sendThread[i].start();
                        System.out.println("neighbour "+i+" killed once");
                    }else{
                        System.out.println("neighbour "+i+" killed twice hence terminating");
                    }
                }
                else {sendThread[i].isAlive=false; }
            }
            ///Construction



        }


    }


}



