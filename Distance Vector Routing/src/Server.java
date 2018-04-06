import java.net.*;

import java.io.*;

public class Server {

    public static void main(String args[]) throws IOException {

        ServerSocket s = new ServerSocket(1234);

        Socket s1=s.accept();


        DataOutputStream os;
        DataInputStream is;

        is= new DataInputStream(s1.getInputStream());
        os = new DataOutputStream(s1.getOutputStream());

        os.writeBytes("Hello from Server\n");

        String line=is.readLine();
        System.out.println(line);


        os.close();
        is.close();



        s1.close();


    }


}