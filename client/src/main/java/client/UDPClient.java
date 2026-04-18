package client;

import common.Request;
import common.Response;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.io.*;

public class UDPClient {
    private static final int BUFFER_SIZE = 65507;
    private final DatagramChannel channel;
    private final InetSocketAddress serverAddress;
    private final int timeout = 3000;
    private final int maxAttempts = 3;

    public UDPClient(String host, int port) throws IOException {
        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(false);
        this.serverAddress = new InetSocketAddress(host, port);
    }

    public Response sendAndReceive(Request request) throws IOException, InterruptedException {
        int attempt = 0;
        Exception lastException = null;

        while (attempt < maxAttempts) {
            attempt++;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(request);
                byte[] data = baos.toByteArray();

                channel.send(ByteBuffer.wrap(data), serverAddress);

                ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                long startTime = System.currentTimeMillis();

                while (System.currentTimeMillis() - startTime < timeout) {
                    InetSocketAddress from = (InetSocketAddress) channel.receive(buffer);
                    if (from != null) {
                        buffer.flip();
                        byte[] responseData = new byte[buffer.remaining()];
                        buffer.get(responseData);

                        ByteArrayInputStream bais = new ByteArrayInputStream(responseData);
                        ObjectInputStream ois = new ObjectInputStream(bais);

                        try {
                            return (Response) ois.readObject();
                        } catch (ClassNotFoundException e) {
                            throw new IOException("Ошибка десериализации: " + e.getMessage());
                        } catch (IOException e) {
                            throw new IOException("Ошибка чтения ответа сервера", e);
                        }
                    }
                    Thread.sleep(100);
                }
                throw new IOException("Таймаут ответа от сервера (попытка " + attempt + ")");

            } catch (IOException e) {
                lastException = e;
                String details = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
                System.out.println("Попытка " + attempt + " не удалась: " + details);
                if (attempt >= maxAttempts) {
                    throw new IOException("Сервер недоступен после " + maxAttempts + " попыток: " + details, e);
                }
                Thread.sleep(1000 * attempt);
            }
        }
        throw new IOException("Сервер недоступен");
    }

    public void close() throws IOException {
        channel.close();
    }
}
