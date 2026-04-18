package server.commands;

import server.CollectionManager;
import common.models.Product;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class PrintAscendingCommand implements Command {
    private CollectionManager collectionManager;

    public PrintAscendingCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String[] args) {
        LinkedList<Product> collection = collectionManager.getCollection();

        if (collection.isEmpty()) {
            System.out.println("Коллекция пуста");
            return;
        }

        LinkedList<Product> sortedList = new LinkedList<>(collection);

        Collections.sort(sortedList);

        System.out.println("Элементы коллекции в порядке возрастания (по цене)");
        for (Product p : sortedList) {
            System.out.println(p);
        }
    }

    @Override
    public String getDescription() {
        return "вывод элементов коллекции в порядке возрастания";
    }

    @Override
    public String getName() {
        return "print_ascending";
    }
}
