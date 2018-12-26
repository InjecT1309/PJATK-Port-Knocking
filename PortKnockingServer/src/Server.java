import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Server {
    Random rand;
    ArrayList<Integer> port_sequence;
    HashMap<InetSocketAddress, ArrayList<Integer>> clients_to_ports;
    ArrayList<Integer> server_ports_taken;

    public Server() {
        rand = new Random();
        port_sequence = new ArrayList<>();
        clients_to_ports = new HashMap<>();
        server_ports_taken = new ArrayList<>();

        int port_sequence_length = 10 + (rand.nextInt(91)); // from 10 to 100

        for(int i = 0; i < port_sequence_length; i++) {
            int port_number = 1025 + (rand.nextInt(1000)); //from 1025 to 2024
            if(port_sequence.contains(port_number))
                i--;
            else
                port_sequence.add(port_number);
        }

        System.out.println("Server running" + "\nThe key is:");
        System.out.println(port_sequence.toString().replaceAll("[\\[\\],]", ""));

        exportToTxt(port_sequence);

        for(int port_number : port_sequence) {
            try {
                new PortSocketThread(this, port_number).start();
            } catch (IOException e) {
                System.out.println("Error when creating socket " + port_number);
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public int openConnectionForClient(InetAddress client_ip) throws IOException {
        int port;
        do {
            port = 2025 + (rand.nextInt(1000));
        } while(server_ports_taken.contains(port));

        System.out.println("Starting a tcp connection on port: " + port);
        new ServerSocketThread(this, port, client_ip).start();
        server_ports_taken.add(port);

        return port;
    }

    public static void main(String args[]) {
        new Server();
    }

    private void exportToTxt(ArrayList<Integer> port_sequence) {
        File file_txt = new File("../port_sequence.txt");
        try {
            PrintWriter write = new PrintWriter(file_txt);
            write.println(port_sequence.toString().replaceAll("[\\[\\],]", ""));
            write.flush();
            System.out.println("Created a file storing port sequence in:\n" + file_txt.getAbsoluteFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(file_txt.getAbsoluteFile());
    }
}
