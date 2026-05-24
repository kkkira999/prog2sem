package server.commands;

import server.CollectionManager;
import server.CommandManager;
import common.models.Coordinates;
import common.models.Organization;
import common.models.OrganizationType;
import common.models.Product;

public class RemoveGreaterCommand implements Command {
    private CommandManager commandManager;
    private CollectionManager collectionManager;

    public RemoveGreaterCommand(CommandManager commandManager, CollectionManager collectionManager) {
        this.commandManager = commandManager;
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String[] args) {
        System.out.println("Создание эталонного элемента для сравнения");
        System.out.println("(Будут удалены все элементы с ценой больше, чем у этого)");

        try {
            System.out.println("Введите название:");
            String name = commandManager.readLine();

            System.out.println("Введите x:");
            int x = Integer.parseInt(commandManager.readLine());

            System.out.println("Введите y:");
            Long y = Long.parseLong(commandManager.readLine());
            Coordinates coordinates = new Coordinates(x, y);

            System.out.println("Введите цену:");
            Float price = Float.parseFloat(commandManager.readLine());

            System.out.println("Введите название организации:");
            String orgName = commandManager.readLine();

            System.out.println("Введите тип организации:");
            String orgTypeStr = commandManager.readLine();
            OrganizationType orgType = OrganizationType.valueOf(orgTypeStr.toUpperCase());

            Organization organization = new Organization(orgName, null, orgType);
            Product referenceProduct = new Product(
                    name, coordinates, price, null, 0, null, organization
            );
            int beforeSize = collectionManager.getSize();
            collectionManager.removeGreater(referenceProduct);

            int afterSize = collectionManager.getSize();
            int removedCount = beforeSize - afterSize;

            System.out.println("Удалено элементов: " + removedCount);

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "удаление из коллекции всех элементов с ценой больше заданной";
    }

    @Override
    public String getName() {
        return "remove_greater";
    }
}
