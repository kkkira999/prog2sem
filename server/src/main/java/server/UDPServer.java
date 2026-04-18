package server;

import common.Request;
import common.Response;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.io.*;

public class UDPServer {
    private static final int BUFFER_SIZE = 65507;
    private final DatagramChannel channel;
    private final RequestHandler requestHandler;

    public UDPServer(int port, RequestHandler requestHandler) throws IOException {
        this.requestHandler = requestHandler;
        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(false);
        this.channel.bind(new InetSocketAddress(port));
        System.out.println("Сервер слушает порт: " + port);
    }

    public void start() {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        while (true) {
            try {
                InetSocketAddress clientAddr = (InetSocketAddress) channel.receive(buffer);

                if (clientAddr != null) {
                    buffer.flip();
                    byte[] requestData = new byte[buffer.remaining()];
                    buffer.get(requestData);

                    try (ByteArrayInputStream bais = new ByteArrayInputStream(requestData);
                         ObjectInputStream ois = new ObjectInputStream(bais)) {

                        Request request = (Request) ois.readObject();
                        System.out.println("Получена команда: " + request.getCommandName());

                        Response response = requestHandler.handle(request);

                        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                             ObjectOutputStream oos = new ObjectOutputStream(baos)) {

                            oos.writeObject(response);
                            byte[] responseData = baos.toByteArray();
                            channel.send(ByteBuffer.wrap(responseData), clientAddr);
                        }

                    } catch (ClassNotFoundException e) {
                        System.err.println("Ошибка десериализации: " + e.getMessage());
                        Response errorResponse = new Response(false, "Неизвестный тип запроса");

                        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                            oos.writeObject(errorResponse);
                            byte[] errorData = baos.toByteArray();
                            channel.send(ByteBuffer.wrap(errorData), clientAddr);
                        }
                    }

                    buffer.clear();
                }

                Thread.sleep(100);

            } catch (IOException e) {
                System.err.println("Ошибка ввода-вывода: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Неожиданная ошибка: " + e.getMessage());
            }
        }
    }
}
