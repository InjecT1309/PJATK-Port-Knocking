import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class SocketThread extends Thread {
    DatagramSocket socket;
    HashMap<InetAddress, ArrayList<Integer>> clients_to_ports;

    public SocketThread(int port, HashMap<InetAddress, ArrayList<Integer>> clients_to_ports) throws IOException {
        this.clients_to_ports = clients_to_ports;
        socket = new DatagramSocket(port);
    }

    @Override
    public void run() {
        while(true) {
            byte packet_content[] = new byte[1024];
            DatagramPacket packet = new DatagramPacket(packet_content, packet_content.length);

            try {
                System.out.println("Listening on port: " + socket.getLocalPort());
                socket.receive(packet);

                addPortToClient(packet);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addPortToClient(DatagramPacket packet) {
        synchronized(clients_to_ports) {
            if(!clients_to_ports.containsKey(packet.getAddress())) {
                clients_to_ports.put(packet.getAddress(), new ArrayList<>());
            }
            clients_to_ports.get(packet.getAddress()).add(socket.getLocalPort());

            System.out.println("Added " + socket.getLocalPort() + " to " + packet.getAddress());
        }
    }
}
