package main;

import managers.CollectionManager;
import managers.CommandManager;
import managers.FileManager;
import utils.ConsoleInputReader;
import models.Product;

import java.util.LinkedList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Ошибка: укажите имя файла!");
            return;
        }

        String fileName = args[0];
        Scanner scanner = new Scanner(System.in);

        FileManager fileManager = new FileManager(fileName);
        CollectionManager collectionManager = new CollectionManager();

        try {
            LinkedList<Product> loadedProducts = fileManager.loadFromFile();
            collectionManager.setCollection(loadedProducts);
        } catch (Exception e) {
            System.err.println("Не удалось загрузить данные: " + e.getMessage());
        }

        ConsoleInputReader consoleReader = new ConsoleInputReader(scanner);
        CommandManager commandManager = new CommandManager(collectionManager, fileManager, consoleReader);

        System.out.println("Программа запущена. Введите 'help' для списка команд.");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                commandManager.executeCommand(input);
            }
        }
    }
}