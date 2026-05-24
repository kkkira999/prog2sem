package server.commands;

import server.CollectionManager;

public class CountGreaterThanManufactureCostCommand implements Command {
    private CollectionManager collectionManager;

    public CountGreaterThanManufactureCostCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            System.out.println("Ошибка: укажите значение");
            return;
        }

        try {
            double cost = Double.parseDouble(args[0]);
            int count = collectionManager.countGreaterThanManufactureCost(cost);
            System.out.println("Количество элементов с manufactureCost > " + cost + ": " + count);
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите число");
        }
    }

    @Override
    public String getName() {
        return "count_greater_than_manufacture_cost";
    }

    @Override
    public String getDescription() {
        return "вывести количество элементов с manufactureCost больше заданного";
    }
}