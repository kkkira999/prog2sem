package server.commands;

import server.CollectionManager;
import server.FileManager;

import java.io.IOException;

public class SaveCommand implements Command {
    private CollectionManager collectionManager;
    private FileManager fileManager;

    public SaveCommand(CollectionManager collectionManager, FileManager fileManager) {
        this.collectionManager = collectionManager;
        this.fileManager = fileManager;
    }

    @Override
    public void execute(String [] args) {
        try {
            fileManager.saveToFile(collectionManager.getCollection());
            System.out.println("Коллекция сохранена в файл");
        }
        catch (IOException e) {
            System.out.println("Ошибка при сохранении: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "сохранение коллекции в файл";
    }

    @Override
    public String getName() {
        return "save";
    }
}
