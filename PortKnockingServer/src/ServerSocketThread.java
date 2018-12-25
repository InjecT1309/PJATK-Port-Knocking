import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketThread extends Thread{
    Server parent;
    ServerSocket server_socket;

    public ServerSocketThread(Server parent, ServerSocket server_socket) {
        this.parent = parent;
        this.server_socket = server_socket;
    }

    @Override
    public void run() {
        try {
            Socket socket = server_socket.accept();
            BufferedReader read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter write = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            String message = read.readLine();
            System.out.println("Received: " + message);
            write.write("Indeed, client\n");
            write.flush();

            socket.close();
            parent.server_socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
