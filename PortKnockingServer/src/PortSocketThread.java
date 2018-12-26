import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;

public class PortSocketThread extends Thread {
    Server parent;
    DatagramSocket socket;

    public PortSocketThread(Server parent, int port) throws IOException {
        this.parent = parent;
        socket = new DatagramSocket(port);
    }

    @Override
    public void run() {
        while(true) {
            byte packet_content[] = new byte[0];
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

    private void addPortToClient(DatagramPacket packet) throws IOException {
        InetSocketAddress packet_address = new InetSocketAddress(packet.getAddress(), packet.getPort());

        synchronized(parent.clients_to_ports) {
            if(!parent.clients_to_ports.containsKey(packet_address)) {
                parent.clients_to_ports.put(packet_address, new ArrayList<>());
            }
            parent.clients_to_ports.get(packet_address).add(socket.getLocalPort());

            System.out.println("Added " + socket.getLocalPort() + " to " + packet.getAddress());
            if(parent.clients_to_ports.get(packet_address).equals(parent.port_sequence)) {
                int server_port = parent.openConnectionForClient(packet_address.getAddress());
                sendOpenPort(packet_address, Integer.toString(server_port));
            }
        }
    }

    private void sendOpenPort(InetSocketAddress address, String message) {
        System.out.println("Sending: " + message);
        byte packet_content[] = message.getBytes();
        DatagramPacket packet = new DatagramPacket(packet_content, packet_content.length, address);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
