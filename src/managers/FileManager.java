package managers;

import models.Product;
import models.Coordinates;
import models.Organization;
import models.OrganizationType;
import models.UnitOfMeasure;

import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;

public class FileManager {
    private String fileName;

    public FileManager(String fileName) {
        this.fileName = fileName;
    }

    public void saveToFile(LinkedList<Product> products) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(fileName);
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"))) {

            writer.println("id,name,coord_x,coord_y,creationDate,price,partNumber," +
                    "manufactureCost,unitOfMeasure,org_id,org_name,org_fullName,org_type");

            for (Product p : products) {
                writer.println(formatProductToCSV(p));
            }

            System.out.println("Данные сохранены в файл: " + fileName);
        }
    }

    public LinkedList<Product> loadFromFile() throws IOException {
        LinkedList<Product> products = new LinkedList<>();
        File file = new File(fileName);

        if (!file.exists()) {
            System.out.println("Файл не найден. Будет создана пустая коллекция.");
            return products;
        }

        if (file.length() == 0) {
            System.out.println("Файл пустой. Будет создана пустая коллекция.");
            return products;
        }

        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            int lineNumber = 1;
            int loadedCount = 0;

            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine().trim();

                if (line.isEmpty()) {
                    continue;
                }

                try {
                    Product product = parseProductFromCSV(line);
                    if (product != null) {
                        products.add(product);
                        loadedCount++;
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка в строке " + lineNumber + ": " + e.getMessage());
                }
            }

            System.out.println("Загружено продуктов: " + loadedCount);

        } catch (SecurityException e) {
            System.err.println("Ошибка: нет прав доступа к файлу " + fileName);
            throw e;
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            throw e;
        }

        return products;
    }

    private String formatProductToCSV(Product p) {
        return String.join(",",
                p.getId().toString(),
                escapeCSV(p.getName()),
                String.valueOf(p.getCoordinates().getX()),
                String.valueOf(p.getCoordinates().getY()),
                p.getCreationDate().toString(),
                p.getPrice().toString(),
                p.getPartNumber() != null ? escapeCSV(p.getPartNumber()) : "",
                String.valueOf(p.getManufactureCost()),
                p.getUnitOfMeasure() != null ? p.getUnitOfMeasure().name() : "",
                p.getManufacturer().getId().toString(),
                escapeCSV(p.getManufacturer().getName()),
                p.getManufacturer().getFullName() != null ? escapeCSV(p.getManufacturer().getFullName()) : "",
                p.getManufacturer().getType().name()
        );
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private Product parseProductFromCSV(String line) {
        String[] parts = parseCSVLine(line);
        if (parts.length < 13) {
            throw new IllegalArgumentException("Недостаточно полей в строке. Ожидалось 13, получено " + parts.length);
        }

        try {
            Integer id = Integer.parseInt(parts[0].trim());
            String name = unescapeCSV(parts[1].trim());
            int x = Integer.parseInt(parts[2].trim());
            Long y = Long.parseLong(parts[3].trim());
            Coordinates coordinates = new Coordinates(x, y);
            java.time.ZonedDateTime creationDate = java.time.ZonedDateTime.parse(parts[4].trim());
            Float price = Float.parseFloat(parts[5].trim());
            String partNumber = parts[6].trim();
            if (partNumber.isEmpty()) {
                partNumber = null;
            } else {
                partNumber = unescapeCSV(partNumber);
            }
            double manufactureCost = Double.parseDouble(parts[7].trim());
            UnitOfMeasure unitOfMeasure = null;
            if (!parts[8].trim().isEmpty()) {
                unitOfMeasure = UnitOfMeasure.valueOf(parts[8].trim());
            }
            Long orgId = Long.parseLong(parts[9].trim());
            String orgName = unescapeCSV(parts[10].trim());
            String orgFullName = parts[11].trim();
            if (orgFullName.isEmpty()) {
                orgFullName = null;
            } else {
                orgFullName = unescapeCSV(orgFullName);
            }
            OrganizationType orgType = OrganizationType.valueOf(parts[12].trim());
            Organization organization = new Organization(orgId, orgName, orgFullName, orgType);

            return new Product(
                    id, name, coordinates, creationDate, price,
                    partNumber, manufactureCost, unitOfMeasure, organization
            );

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ошибка формата числа: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ошибка в данных: " + e.getMessage());
        }
    }

    private String[] parseCSVLine(String line) {
        return line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
    }

    private String unescapeCSV(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
            value = value.replace("\"\"", "\"");
        }
        return value;
    }
}