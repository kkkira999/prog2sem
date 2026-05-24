package server;

import common.models.Product;
import common.models.UnitOfMeasure;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class CollectionManager {
    private LinkedList<Product> collection;
    private LocalDateTime initializationDate;

    public CollectionManager() {
        this.collection = new LinkedList<>();
        this.initializationDate = LocalDateTime.now();
    }

    public LinkedList<Product> getCollection() {
        return collection;
    }

    public int getSize() {
        return collection.size();
    }

    public LocalDateTime getInitializationDate() {
        return initializationDate;
    }

    public String showCollection() {
        if (collection.isEmpty()) {
            return "Коллекция пуста";
        }
        return collection.stream()
                .map(Product::toString)
                .collect(Collectors.joining("\n"));
    }

    public void add(Product product) {
        collection.add(product);
        System.out.println("Продукт добавлен. ID: " + product.getId());
    }

    public void clear() {
        collection.clear();
        System.out.println("Коллекция очищена");
    }

    public boolean removeById(Integer id) {
        return collection.removeIf(p -> p.getId().equals(id));
    }

    public boolean update(Integer id, Product newProduct) {
        for (int i = 0; i < collection.size(); i++) {
            if (collection.get(i).getId().equals(id)) {
                newProduct.setId(id);
                collection.set(i, newProduct);
                return true;
            }
        }
        return false;
    }

    public void setCollection(LinkedList<Product> newCollection) {
        this.collection = newCollection;
    }

    public void removeGreater(Product referenceProduct) {
        int beforeSize = collection.size();
        collection.removeIf(p -> p.compareTo(referenceProduct) > 0);
        int removed = beforeSize - collection.size();
        System.out.println("Удалено элементов с большей ценой: " + removed);
    }

    public void removeLower(Product referenceProduct) {
        int beforeSize = collection.size();
        collection.removeIf(p -> p.compareTo(referenceProduct) < 0);
        int removed = beforeSize - collection.size();
        System.out.println("Удалено элементов с меньшей ценой: " + removed);
    }

    public int removeAllByUnitOfMeasure(UnitOfMeasure unit) {
        int beforeSize = collection.size();
        collection.removeIf(p -> p.getUnitOfMeasure() == unit);
        return beforeSize - collection.size();
    }

    public int countGreaterThanManufactureCost(double cost) {
        return (int) collection.stream()
                .filter(p -> p.getManufactureCost() > cost)
                .count();
    }

    public boolean insertAt(int index, Product product) {
        if (index < 0 || index > collection.size()) {
            return false;
        }
        collection.add(index, product);
        return true;
    }
}