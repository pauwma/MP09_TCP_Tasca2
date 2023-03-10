import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TCPClient {
    private final String hostname;
    private final int port;

    public TCPClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void sendAndReceive(Llista llista) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(hostname, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(llista);
            out.flush();

            Llista result = (Llista) in.readObject();
            List<Integer> numberList = result.getNumberList();

            System.out.println("Nombre de la lista: " + result.getNom());
            System.out.println("Lista ordenada y sin repetidos: " + numberList);
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);

        List<Integer> numeros = new ArrayList<>();
        while (true) {
            System.out.print("Ingrese un número (o presione enter para finalizar la lista): ");
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                break;
            }
            try {
                int numero = Integer.parseInt(input);
                numeros.add(numero);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Intente nuevamente.");
            }
        }

        Llista llista = new Llista("Lista", numeros);
        TCPClient client = new TCPClient("localhost", 1234);
        client.sendAndReceive(llista);
    }
}
