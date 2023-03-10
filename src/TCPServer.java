import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {
    private final int port;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public TCPServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {
            Socket socket = serverSocket.accept();
            executorService.execute(new ClientHandler(socket));
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                Llista llista = (Llista) in.readObject();
                List<Integer> numberList = llista.getNumberList();

                // Elimina elementos duplicados de la lista
                Set<Integer> uniqueNumbers = new HashSet<>(numberList);

                // Ordena la lista
                List<Integer> sortedNumbers = uniqueNumbers.stream().sorted().toList();

                Llista result = new Llista(llista.getNom(), sortedNumbers);

                out.writeObject(result);
                out.flush();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        TCPServer server = new TCPServer(1234);
        server.start();
    }
}