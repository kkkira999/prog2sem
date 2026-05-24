package server.commands;

import server.CollectionManager;
import common.models.Product;
import common.models.UnitOfMeasure;

public class RemoveAllByUnitOfMeasureCommand implements Command {
    private CollectionManager collectionManager;

    public RemoveAllByUnitOfMeasureCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            System.out.println("Ошибка: укажите единицу измерения");
            System.out.println("Доступные: " + UnitOfMeasure.names());
            return;
        }

        try {
            UnitOfMeasure unit = UnitOfMeasure.valueOf(args[0].toUpperCase());
            System.out.println("Поиск продуктов с единицей измерения: " + unit);
            int count = 0;
            for (Product p : collectionManager.getCollection()) {
                if (p.getUnitOfMeasure() == unit) {
                    count++;
                    System.out.println("Будет удален: " + p);
                }
            }

            if (count == 0) {
                System.out.println("Продукты с единицей измерения " + unit + " не найдены");
                return;
            }

            int removed = collectionManager.removeAllByUnitOfMeasure(unit);
            System.out.println("Удалено элементов: " + removed);

        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: неверная единица измерения");
            System.out.println("Доступные: " + UnitOfMeasure.names());
        }
    }

    @Override
    public String getName() {
        return "remove_all_by_unit_of_measure";
    }

    @Override
    public String getDescription() {
        return "удалить все элементы с указанной единицей измерения";
    }
}