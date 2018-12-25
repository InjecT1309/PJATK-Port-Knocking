import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Server {
    Random rand;
    ArrayList<Integer> port_sequence;
    HashMap<InetAddress, ArrayList<Integer>> clients_to_ports;

    public Server() {
        rand = new Random();
        port_sequence = new ArrayList<>();
        clients_to_ports = new HashMap<InetAddress, ArrayList<Integer>>();

        int port_sequence_length = 10 + (rand.nextInt(91)); // from 10 to 100

        for(int i = 0; i < port_sequence_length; i++) {
            int port_number = 1025 + (rand.nextInt(1000)); //from 1025 to 2024
            if(port_sequence.contains(port_number))
                i--;
            else
                port_sequence.add(port_number);
        }

        System.out.println("Server started\nThe key is:");
        System.out.println(port_sequence.toString().replaceAll("[\\[\\],]", ""));

        for(int port_number : port_sequence) {
            try {
                new SocketThread(port_number, clients_to_ports).start();
            } catch (IOException e) {
                System.out.println("Error when creating socket " + port_number);
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public static void main(String args[]) {
        new Server();
    }
}
