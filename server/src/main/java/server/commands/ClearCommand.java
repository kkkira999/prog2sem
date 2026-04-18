package server.commands;

import server.CollectionManager;

public class ClearCommand implements Command {
    private CollectionManager collectionManager;

    public ClearCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String[] args) {
        collectionManager.clear();
        System.out.println("Все продукты были удалены из коллекции");
    }

    @Override
    public String getDescription() {
        return "очистка коллекции";
    }

    @Override
    public String getName() {
        return "clear";
    }
}
