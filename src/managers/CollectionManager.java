package managers;

import models.Product;
import models.UnitOfMeasure;

import java.time.LocalDateTime;
import java.util.LinkedList;

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

        StringBuilder result = new StringBuilder();
        for (Product c : collection) {
            result.append(c.toString()).append("\n");
        }
        return result.toString();
    }

    public void add(Product product) {
        collection.add(product);
        System.out.println("Продукт добавлен в коллекцию. ID: " + product.getId());
    }

    public void clear() {
        collection.clear();
        System.out.println("Коллекция очищена");
    }

    public boolean removeById(Integer id) {
        for (Product p : collection) {
            if (p.getId().equals(id)) {
                collection.remove(p);
                return true;
            }
        }
        return false;
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

        LinkedList<Product> toRemove = new LinkedList<>();

        for (Product p : collection) {
            int comparisonResult = p.compareTo(referenceProduct);
            if (comparisonResult > 0) {
                toRemove.add(p);
            }
        }

        for (Product p : toRemove) {
            collection.remove(p);
        }

        int afterSize = collection.size();
        int removedCount = beforeSize - afterSize;

        System.out.println("Удалено элементов с большей ценой: " + removedCount);
    }

    public void removeLower(Product referenceProduct) {
        int beforeSize = collection.size();

        LinkedList<Product> toRemove = new LinkedList<>();

        for (Product p : collection) {
            int comparisonResult = p.compareTo(referenceProduct);
            if (comparisonResult < 0) {
                toRemove.add(p);
            }
        }

        for (Product p : toRemove) {
            collection.remove(p);
        }

        int afterSize = collection.size();
        int removedCount = beforeSize - afterSize;

        System.out.println("Удалено элементов с меньшей ценой: " + removedCount);
    }

    public int removeAllByUnitOfMeasure(UnitOfMeasure unit) {
        int beforeSize = collection.size();

        LinkedList<Product> toRemove = new LinkedList<>();

        for (Product p : collection) {
            if (p.getUnitOfMeasure() == unit) {
                toRemove.add(p);
            }
        }

        for (Product p : toRemove) {
            collection.remove(p);
        }

        int removedCount = beforeSize - collection.size();
        return removedCount;
    }

    public int countGreaterThanManufactureCost(double cost) {
        int count = 0;

        for (Product p : collection) {
            if (p.getManufactureCost() > cost) {
                count++;
            }
        }

        return count;
    }

    public boolean insertAt(int index, Product product) {
        if (index < 0 || index > collection.size()) {
            return false;
        }

        collection.add(index, product);
        return true;
    }
}