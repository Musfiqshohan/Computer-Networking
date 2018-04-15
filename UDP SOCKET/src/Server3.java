import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;



public class Server3 {

    public static void main(String[] args) throws  Exception {



        EchoServer server= new EchoServer(2223);
        server.start();

        boolean running=true;

        while(running) {
            Thread.sleep(5000);
            EchoClient client = new EchoClient();
            System.out.println(client.sendEcho("hello to Server1 from server3", 4445));
        }

    }


}



