import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;



class EchoServer extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[40];


    public EchoServer(int port) throws Exception {
        socket = new DatagramSocket(port);
    }

    public void run() {


        try {
            running = true;

            while (running) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                //System.out.println("Client:-" + data(buf));

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                String received=data(buf); // new String(packet.getData(), 0, packet.getLength());

                System.out.println(received);

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

}


class EchoClient {
    private DatagramSocket socket;
    private InetAddress address;

    private byte[] buf = new byte[40];

    public EchoClient() throws  Exception{
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");
    }

    public String sendEcho(String msg, int port) throws Exception{
        buf = msg.getBytes();
        DatagramPacket packet= new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());
        return received;
    }

    public void close() {
        socket.close();
    }
}

public class Server {

    public static void main(String[] args) throws  Exception {



        EchoClient client=new EchoClient();
        EchoServer server= new EchoServer(4445);
        server.start();

        boolean running=true;

        while(running) {

            Thread.sleep(5000);

        System.out.println(client.sendEcho("hello to Server2", 2222));
        System.out.println(client.sendEcho("hello to Server3", 2223));

        }

    }


}



