import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketThread extends Thread {
    Server parent;
    ServerSocket server_socket;
    InetAddress client_allowed;

    public ServerSocketThread(Server parent, int server_port, InetAddress client_allowed) throws IOException {
        this.parent = parent;
        this.client_allowed = client_allowed;
        server_socket = new ServerSocket(server_port);
    }

    @Override
    public void run() {
        try {
            Socket socket = server_socket.accept();
            InetAddress client_address = socket.getInetAddress();
            if(!client_address.equals(client_allowed)) {
                System.out.println("I am waiting for  " + client_allowed + "\nNot " + client_address);
                return;
            }
            BufferedReader read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter write = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            String message = read.readLine();
            System.out.println("Received: " + message);
            System.out.println("Sending: " + "Indeed, client");
            write.println("Indeed, client");
            write.flush();

            socket.close();
            synchronized (parent.server_ports_taken) {
                parent.server_ports_taken.removeIf(e -> e == server_socket.getLocalPort());
            }
            server_socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
