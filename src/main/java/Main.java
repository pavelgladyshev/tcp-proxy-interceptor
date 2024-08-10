
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class Main {

    public static void main(String[] args) throws IOException {

        if (args.length == 3) {

            ServerSocket ss = new ServerSocket(Integer.parseInt(args[0]));
            SocketAddress outAddr = new InetSocketAddress(args[1], Integer.parseInt(args[2]));
            Socket out = new Socket();

            out.connect(outAddr);
            System.out.println("Connected to TCP target");
            Socket in = ss.accept();
            System.out.println("Accepted incoming TCP connection");

            final DataInputStream in_input = new DataInputStream(in.getInputStream());
            final DataOutputStream in_output = new DataOutputStream(in.getOutputStream());

            final DataInputStream out_input = new DataInputStream(out.getInputStream());
            final DataOutputStream out_output = new DataOutputStream(out.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] packetData = new byte[65536];
                    int len;
                    try {
                        while(true) {

                            // block until new data arrives
                            len = out_input.read(packetData);

                            //print packet
                            System.out.print("<- ");
                            for (int i=0; i<len; i++)
                            {
                                System.out.print((char)(packetData[i]));
                            }
                            System.out.println();

                            //send it on
                            in_output.write(packetData,0,len);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] packetData = new byte[65536];
                    int len;
                    try {
                        while(true) {

                            // block until new data arrives
                            len = in_input.read(packetData);

                            //print packet
                            System.out.print("-> ");
                            for (int i=0; i<len; i++)
                            {
                                System.out.print((char)(packetData[i]));
                            }
                            System.out.println();

                            //send it on
                            out_output.write(packetData,0,len);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            for(;;);

        } else {
            System.out.println("Usage: java -jar interceptor-1.0.jar listening-port target-ip target-port");
            System.out.println("Example: java -jar interceptor-1.0.jar 3333 127.0.0.1 1234");
        }

    }
}
