package models;

import java.time.ZonedDateTime;

public class Product implements Comparable<Product> {
    private Integer id;
    private String name;
    private Coordinates coordinates;
    private java.time.ZonedDateTime creationDate;
    private Float price;
    private String partNumber;
    private double manufactureCost;
    private UnitOfMeasure unitOfMeasure;
    private Organization manufacturer;

    private static Integer nextId = 0;
    public Product(String name, Coordinates coordinates,
                   Float price, String partNumber, double manufactureCost,
                   UnitOfMeasure unitOfMeasure, Organization manufacturer) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name не может быть null или пустым");
        }

        if (coordinates == null) {
            throw new IllegalArgumentException("coordinates не может быть null");
        }

        if (price == null || price <= 0) {
            throw new IllegalArgumentException("price должен быть > 0 и не может быть null");
        }

        if (partNumber != null && partNumber.length() < 28) {
            throw new IllegalArgumentException("partNumber должен быть >= 28 символов");
        }

        if (manufacturer == null) {
            throw new IllegalArgumentException("manufacturer не может быть null");
        }

        this.id = generateId();
        this.name = name;
        this.coordinates = coordinates;
        this.price = price;
        this.manufactureCost = manufactureCost;
        this.unitOfMeasure = unitOfMeasure;
        this.manufacturer = manufacturer;
        this.creationDate = java.time.ZonedDateTime.now();
    }
    public Product(Integer id, String name, Coordinates coordinates,
                   java.time.ZonedDateTime creationDate, Float price, String partNumber,
                   double manufactureCost, UnitOfMeasure unitOfMeasure, Organization manufacturer) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id должен быть > 0 и не может быть null");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name не может быть null или пустым");
        }

        if (coordinates == null) {
            throw new IllegalArgumentException("coordinates не может быть null");
        }

        if (price == null || price <= 0) {
            throw new IllegalArgumentException("price должен быть > 0 и не может быть null");
        }

        if (partNumber != null && partNumber.length() < 28) {
            throw new IllegalArgumentException("partNumber должен быть >= 28 символов");
        }

        if (manufacturer == null) {
            throw new IllegalArgumentException("manufacturer не может быть null");
        }

        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.price = price;
        this.manufactureCost = manufactureCost;
        this.unitOfMeasure = unitOfMeasure;
        this.manufacturer = manufacturer;
        this.partNumber = partNumber;

        if (id >= nextId) {
            nextId = id + 1;
        }
    }
    private Integer generateId() {
        return ++nextId;
    }
    @Override
    public int compareTo(Product p) {
        return this.price.compareTo(p.price);
    }
    @Override
    public String toString() {
        return "ID: " + id + ", Название: " + name + ", Цена: " + price;
    }
    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public Float getPrice() {
        return price;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public double getManufactureCost() {
        return manufactureCost;
    }

    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public Organization getManufacturer() {
        return manufacturer;
    }

    public void setId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID должен быть > 0 и не null");
        }
        this.id = id;
    }
}