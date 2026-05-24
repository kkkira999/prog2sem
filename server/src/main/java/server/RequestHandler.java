package server;

import common.Request;
import common.Response;
import common.models.Product;
import common.models.UnitOfMeasure;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class RequestHandler {
    private final CollectionManager collectionManager;
    private final FileManager fileManager;

    public RequestHandler(CollectionManager collectionManager, FileManager fileManager) {
        this.collectionManager = collectionManager;
        this.fileManager = fileManager;
    }

    public Response handle(Request request) {
        String command = request.getCommandName();
        Object[] args = request.getArgs();

        try {
            switch (command) {
                case "help":
                    return handleHelp();
                case "info":
                    return handleInfo();
                case "show":
                    return handleShow();
                case "add":
                    return handleAdd((Product) args[0]);
                case "add_batch":
                    return handleAddBatch((List<Product>) args[0]);
                case "update":
                    return handleUpdate((Integer) args[0], (Product) args[1]);
                case "remove_by_id":
                    return handleRemoveById((Integer) args[0]);
                case "clear":
                    return handleClear();
                case "insert_at":
                    return handleInsertAt((Integer) args[0], (Product) args[1]);
                case "remove_greater":
                    return handleRemoveGreater((Product) args[0]);
                case "remove_lower":
                    return handleRemoveLower((Product) args[0]);
                case "remove_all_by_unit_of_measure":
                    return handleRemoveAllByUnitOfMeasure((UnitOfMeasure) args[0]);
                case "count_greater_than_manufacture_cost":
                    return handleCountGreaterThanManufactureCost((Double) args[0]);
                case "print_ascending":
                    return handlePrintAscending();
                case "server_save":
                    return handleServerSave();
                case "server_info":
                    return handleServerInfo();
                default:
                    return new Response(false, "Неизвестная команда: " + command);
            }
        } catch (Exception e) {
            return new Response(false, "Ошибка выполнения команды: " + e.getMessage());
        }
    }

    private Response handleHelp() {
        String help = "=== Доступные команды ===\n" +
                "help - вывести справку\n" +
                "info - информация о коллекции\n" +
                "show - вывести все элементы\n" +
                "add {element} - добавить элемент\n" +
                "update id {element} - обновить элемент\n" +
                "remove_by_id id - удалить по id\n" +
                "clear - очистить коллекцию\n" +
                "insert_at index {element} - вставить на позицию\n" +
                "remove_greater {element} - удалить большие\n" +
                "remove_lower {element} - удалить меньшие\n" +
                "remove_all_by_unit_of_measure unit - удалить по единице\n" +
                "count_greater_than_manufacture_cost cost - подсчет\n" +
                "print_ascending - вывести по возрастанию\n" +
                "exit - завершить клиент\n";
        return new Response(true, help);
    }

    private Response handleInfo() {
        String info = "=== Информация о коллекции ===\n" +
                "Тип коллекции: LinkedList<Product>\n" +
                "Количество элементов: " + collectionManager.getSize() + "\n" +
                "Дата инициализации: " + collectionManager.getInitializationDate();
        return new Response(true, info);
    }

    private Response handleShow() {
        if (collectionManager.getSize() == 0) {
            return new Response(true, "Коллекция пуста");
        }
        return new Response(true, "Содержимое коллекции:", collectionManager.showCollection());
    }

    private Response handleAdd(Product product) {
        collectionManager.add(product);
        return new Response(true, "Продукт добавлен. ID: " + product.getId());
    }

    private Response handleAddBatch(List<Product> products) {
        for (Product product : products) {
            collectionManager.add(product);
        }
        return new Response(true, "Продуктов добавлено батчем: " + products.size());
    }

    private Response handleUpdate(Integer id, Product newProduct) {
        if (collectionManager.update(id, newProduct)) {
            return new Response(true, "Продукт с ID " + id + " обновлен");
        }
        return new Response(false, "Продукт с ID " + id + " не найден");
    }

    private Response handleRemoveById(Integer id) {
        if (collectionManager.removeById(id)) {
            return new Response(true, "Продукт с ID " + id + " удален");
        }
        return new Response(false, "Продукт с ID " + id + " не найден");
    }

    private Response handleClear() {
        collectionManager.clear();
        return new Response(true, "Коллекция очищена");
    }

    private Response handleInsertAt(Integer index, Product product) {
        if (collectionManager.insertAt(index, product)) {
            return new Response(true, "Продукт добавлен на позицию " + index + " с ID: " + product.getId());
        }
        return new Response(false, "Недопустимый индекс: " + index);
    }

    private Response handleRemoveGreater(Product reference) {
        int beforeSize = collectionManager.getSize();
        collectionManager.removeGreater(reference);
        int removed = beforeSize - collectionManager.getSize();
        return new Response(true, "Удалено элементов с большей ценой: " + removed);
    }

    private Response handleRemoveLower(Product reference) {
        int beforeSize = collectionManager.getSize();
        collectionManager.removeLower(reference);
        int removed = beforeSize - collectionManager.getSize();
        return new Response(true, "Удалено элементов с меньшей ценой: " + removed);
    }

    private Response handleRemoveAllByUnitOfMeasure(UnitOfMeasure unit) {
        int removed = collectionManager.removeAllByUnitOfMeasure(unit);
        return new Response(true, "Удалено элементов с единицей измерения " + unit + ": " + removed);
    }

    private Response handleCountGreaterThanManufactureCost(Double cost) {
        int count = collectionManager.countGreaterThanManufactureCost(cost);
        return new Response(true, "Количество элементов с manufactureCost > " + cost + ": " + count);
    }

    private Response handlePrintAscending() {
        LinkedList<Product> sorted = new LinkedList<>(collectionManager.getCollection());
        sorted.sort(Product::compareTo);
        if (sorted.isEmpty()) {
            return new Response(true, "Коллекция пуста");
        }
        StringBuilder sb = new StringBuilder();
        for (Product p : sorted) {
            sb.append(p.toString()).append("\n");
        }
        return new Response(true, "Элементы в порядке возрастания:", sb.toString());
    }

    private Response handleServerSave() {
        try {
            fileManager.saveToFile(collectionManager.getCollection());
            return new Response(true, "Сервер: коллекция сохранена в файл");
        } catch (IOException e) {
            return new Response(false, "Ошибка сохранения: " + e.getMessage());
        }
    }

    private Response handleServerInfo() {
        String info = "=== Серверная информация ===\n" +
                "Файл: data.csv\n" +
                "Размер коллекции: " + collectionManager.getSize() + "\n" +
                "Дата запуска: " + collectionManager.getInitializationDate();
        return new Response(true, info);
    }
}
