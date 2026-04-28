package client;

public class ClientApp {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Использование: java client.ClientApp <хост> <порт>");
            System.out.println("Пример: java client.ClientApp localhost 1234");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            UDPClient udpClient = new UDPClient(host, port);
            ClientConsole console = new ClientConsole(udpClient);
            console.start();
            udpClient.close();
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}