import javax.xml.soap.Node;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class Client2 {

    public static Socket s1, s2;
    public static ServerSocket ss;
    public static DataInputStream is;
    public static DataOutputStream os;
    public static Router myrouter;




    public static void establishConnection() {
        try {
            //  s1 = new Socket("127.0.0.1", 1234);  //send
            ss = new ServerSocket(1234);   //receive
            s2 = ss.accept();
            is = new DataInputStream(s2.getInputStream());
            os = new DataOutputStream(s2.getOutputStream());
        } catch (Exception e) {
        }

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
                System.out.println(sendData);

            }
            // os.writeBytes("----");

        }catch (Exception e) {}


    }


    public static void receiveRoutingTable() {
        try {

            //System.out.println("hi");


//

            for (int j = 0; j < 10; j++) {
                String line = is.readUTF();
              //  System.out.println(line);


                String Info[] = line.split(" ");
                int cost= Integer.valueOf(Info[2]);


               // System.out.println(j+"->"+myrouter.routingTable[j].retNodeInfo()+ " "+(2+ cost));
                if(myrouter.routingTable[j].cost>2+ cost)   //need change
                {
                    myrouter.routingTable[j].cost=2+ cost;
                    myrouter.routingTable[j].nextHop="A";
                }



            }
           // System.out.println();


        } catch (Exception e) {
            System.out.println(e);
        }

    }


    public static void main(String args[]) throws IOException {

       // establishConnection();


       // Scanner sc= new Scanner(System.in);
        String mynode ="B"; // sc.next();
        int mynodeIdx = mynode.charAt(0) - 65;
        String port ="2001"; // (sc.next());

        myrouter = new Router(mynodeIdx, port);






        BufferedReader br = new BufferedReader(new FileReader("configB.txt"));
        String line;
        line = br.readLine();
        int nodes = Integer.valueOf(line);

        for (int i = 0; i < nodes; i++) {
            line = br.readLine();
           // System.out.println(line);

            String input[] = line.split(" ");
            int dest = input[0].charAt(0) - 65;
             int cost = Integer.valueOf(input[1]);

            myrouter.routingTable[dest] = new NodeInfo(input[0], input[0], cost);


        }



        myrouter.printRouter();
        System.out.println();


//
         establishConnection();
        receiveRoutingTable();

        myrouter.printRouter();

        // sendRoutingTable();


        os.close();
        is.close();
        s2.close();

    }

}
