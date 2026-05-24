package server.commands;

import server.CollectionManager;

public class RemoveByIdCommand implements Command {
    private CollectionManager collectionManager;

    public RemoveByIdCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            System.out.println("Ошибка: укажите ID");
            return;
        }

        try {
            Integer id = Integer.parseInt(args[0]);

            if (collectionManager.removeById(id)) {
                System.out.println("Продукт с ID " + id + " удален");
            }
            else {
                System.out.println("Продукт с ID " + id + " не найден");
            }
        }
        catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть числом");
        }
    }

    @Override
    public String getDescription() {
        return "удалить элемент по его ID";
    }

    @Override
    public String getName() {
        return "remove_by_id";
    }
}
