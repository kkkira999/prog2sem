package server.commands;

import server.CollectionManager;

public class InfoCommand implements Command {
    private CollectionManager collectionManager;

    public InfoCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String[] args) {
        System.out.println("Информация о коллекции");
        System.out.println("Тип коллекции: LinkedList<Product>");
        System.out.println("Размер коллекции: " + collectionManager.getSize());
        System.out.println("Дата инициализации: " + collectionManager.getInitializationDate());
    }

    @Override
    public String getDescription() {
        return "вывод информации о коллекции";
    }

    @Override
    public String getName() {
        return "info";
    }
}
