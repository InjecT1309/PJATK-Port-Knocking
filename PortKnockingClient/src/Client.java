import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Client extends Thread{
    InetAddress server_address;
    ArrayList<Integer> port_sequence;
    DatagramSocket socket;

    public Client() throws UnknownHostException, SocketException {
        server_address = InetAddress.getLocalHost();
        port_sequence = new ArrayList<>();
        socket = new DatagramSocket();
    }

    public static void main(String args[]) {
        try {
            new Client().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            getIpAddress();
            getPortSequence();
            sendRequestsToPorts(port_sequence);
            int server_port = listenForAnswer();
            talkAboutWheather(server_port);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getIpAddress() throws UnknownHostException {
        System.out.println("Enter server ip address");
        String address = new Scanner(System.in).nextLine();
        server_address = InetAddress.getByName(address);
    }
    private void getPortSequence() {
        File file = new File("../port_sequence.txt");
        String ports[] = new String[0];

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            ports = reader.readLine().split(" ");
        } catch (FileNotFoundException e) {
            System.out.println("No port_sequence.txt file found.\nEnter the ports manually");
            ports = new Scanner(System.in).nextLine().split(" ");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            for(String port : ports) {
                port_sequence.add(Integer.parseInt(port));
            }
            System.out.println(port_sequence);
        }
    }
    private void sendRequestsToPorts(ArrayList<Integer> port_sequence) throws IOException, InterruptedException {
        for(int port : port_sequence) {
            sendRequestToPort(port);
            sleep(50);
        }
    }
    private void sendRequestToPort(int port) throws IOException {
        byte data[] = new byte[0];
        DatagramPacket packet = new DatagramPacket(data, data.length, server_address, port);
        System.out.println("Sending request to port: " + port);
        socket.send(packet);
    }
    private int listenForAnswer() throws IOException {
        byte buffer[] = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        String answer = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Received: " + answer);

        socket.close();

        return Integer.parseInt(answer);
    }
    private void talkAboutWheather(int port) throws IOException {
        Socket socket = new Socket(server_address, port);
        BufferedReader read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter write = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        write.write("Nice whether, isn't it, server?\n");
        write.flush();

        String answer = read.readLine();
        System.out.println("Received: " + answer);
        socket.close();
    }
}
