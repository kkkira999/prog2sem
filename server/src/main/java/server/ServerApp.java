package server;

import java.io.IOException;

public class ServerApp {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Использование: java server.ServerApp <port>");
            System.out.println("Пример: java server.ServerApp 1234");
            return;
        }

        try {
            int port = Integer.parseInt(args[0]);

            FileManager fileManager = new FileManager("data.csv");
            CollectionManager collectionManager = new CollectionManager();

            try {
                collectionManager.setCollection(fileManager.loadFromFile());
                System.out.println("Загружено " + collectionManager.getSize() + " элементов");
            } catch (IOException e) {
                System.out.println("Не удалось загрузить файл: " + e.getMessage());
            }

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    fileManager.saveToFile(collectionManager.getCollection());
                    System.out.println("Коллекция сохранена при завершении сервера");
                } catch (IOException e) {
                    System.err.println("Ошибка сохранения: " + e.getMessage());
                }
            }));

            RequestHandler handler = new RequestHandler(collectionManager, fileManager);
            UDPServer server = new UDPServer(port, handler);
            server.start();

        } catch (NumberFormatException e) {
            System.out.println("Ошибка: порт должен быть числом");
        } catch (IOException e) {
            System.out.println("Ошибка сервера: " + e.getMessage());
        }
    }
}