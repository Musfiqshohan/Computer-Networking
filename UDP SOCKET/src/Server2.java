import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;



public class Server2 {

    public static void main(String[] args) throws  Exception {


//
//        EchoClient client=new EchoClient();
//
//        System.out.println(client.sendEcho("hello",4445));

        EchoServer server= new EchoServer(2222);
        server.start();

        boolean running=true;

        while(running) {
            Thread.sleep(5000);
            EchoClient client = new EchoClient();
            System.out.println(client.sendEcho("hello to Server1 from server2", 4445));
        }

    }


}



