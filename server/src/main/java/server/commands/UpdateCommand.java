package server.commands;

import server.CommandManager;
import server.CollectionManager;
import common.models.*;

public class UpdateCommand implements Command {
    private CommandManager commandManager;
    private CollectionManager collectionManager;

    public UpdateCommand(CommandManager commandManager, CollectionManager collectionManager) {
        this.commandManager = commandManager;
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
            Product oldProduct = findProductById(id);
            if (oldProduct == null) {
                System.out.println("Продукт с ID " + id + " не найден");
                return;
            }

            System.out.println("Обновление продукта с ID: " + id);
            System.out.println("(Оставьте поле пустым, чтобы оставить старое значение)");
            String name = oldProduct.getName();
            while (true) {
                try {
                    System.out.println("Старое название: " + oldProduct.getName());
                    System.out.print("Введите новое название (Enter чтобы оставить): ");
                    String input = commandManager.readLine();
                    if (input.isEmpty()) {
                        break;
                    }
                    if (input.trim().isEmpty()) {
                        throw new IllegalArgumentException("Название не может быть пустым");
                    }
                    name = input;
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
                }
            }
            Float price = oldProduct.getPrice();
            while (true) {
                try {
                    System.out.println("Старая цена: " + oldProduct.getPrice());
                    System.out.print("Введите новую цену (Enter чтобы оставить): ");
                    String input = commandManager.readLine();
                    if (input.isEmpty()) {
                        break;
                    }
                    price = Float.parseFloat(input);
                    if (price <= 0) {
                        throw new IllegalArgumentException("Цена должна быть > 0");
                    }
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: введите число. Попробуйте снова.");
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
                }
            }
            Coordinates coordinates = oldProduct.getCoordinates();
            while (true) {
                try {
                    System.out.println("Старые координаты: " + oldProduct.getCoordinates());
                    System.out.print("Введите новый x (Enter чтобы оставить): ");
                    String xStr = commandManager.readLine();
                    System.out.print("Введите новый y (Enter чтобы оставить): ");
                    String yStr = commandManager.readLine();

                    if (xStr.isEmpty() && yStr.isEmpty()) {
                        break;
                    }

                    if (!xStr.isEmpty() && !yStr.isEmpty()) {
                        int x = Integer.parseInt(xStr);
                        Long y = Long.parseLong(yStr);
                        coordinates = new Coordinates(x, y);
                        break;
                    } else {
                        throw new IllegalArgumentException("Нужно ввести оба значения или ни одного");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: введите целые числа. Попробуйте снова.");
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
                }
            }
            String partNumber = oldProduct.getPartNumber();
            while (true) {
                try {
                    System.out.println("Старый partNumber: " + oldProduct.getPartNumber());
                    System.out.print("Введите новый partNumber (Enter чтобы оставить): ");
                    String input = commandManager.readLine();
                    if (input.isEmpty()) {
                        break;
                    }
                    if (input.length() < 28) {
                        throw new IllegalArgumentException("partNumber должен быть >= 28 символов");
                    }
                    partNumber = input;
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
                }
            }
            double manufactureCost = oldProduct.getManufactureCost();
            while (true) {
                try {
                    System.out.println("Старая manufactureCost: " + oldProduct.getManufactureCost());
                    System.out.print("Введите новую manufactureCost (Enter чтобы оставить): ");
                    String input = commandManager.readLine();
                    if (input.isEmpty()) {
                        break;
                    }
                    manufactureCost = Double.parseDouble(input);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: введите число. Попробуйте снова.");
                }
            }
            UnitOfMeasure unitOfMeasure = oldProduct.getUnitOfMeasure();
            while (true) {
                try {
                    System.out.println("Старая единица измерения: " + oldProduct.getUnitOfMeasure());
                    System.out.println("Доступные единицы: " + UnitOfMeasure.names());
                    System.out.print("Введите новую единицу измерения (Enter чтобы оставить): ");
                    String input = commandManager.readLine();
                    if (input.isEmpty()) {
                        break;
                    }
                    unitOfMeasure = UnitOfMeasure.valueOf(input.toUpperCase());
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка: введите одно из: " + UnitOfMeasure.names() + ". Попробуйте снова.");
                }
            }
            String orgName = oldProduct.getManufacturer().getName();
            while (true) {
                try {
                    System.out.println("Старое название организации: " + oldProduct.getManufacturer().getName());
                    System.out.print("Введите новое название организации (Enter чтобы оставить): ");
                    String input = commandManager.readLine();
                    if (input.isEmpty()) {
                        break;
                    }
                    if (input.trim().isEmpty()) {
                        throw new IllegalArgumentException("Название не может быть пустым");
                    }
                    orgName = input;
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
                }
            }
            String orgFullName = oldProduct.getManufacturer().getFullName();
            System.out.println("Старое полное название: " + oldProduct.getManufacturer().getFullName());
            System.out.print("Введите новое полное название (Enter чтобы оставить): ");
            String orgFullInput = commandManager.readLine();
            if (!orgFullInput.isEmpty()) {
                orgFullName = orgFullInput;
            }
            OrganizationType orgType = oldProduct.getManufacturer().getType();
            while (true) {
                try {
                    System.out.println("Старый тип организации: " + oldProduct.getManufacturer().getType());
                    System.out.println("Доступные типы: " + OrganizationType.names());
                    System.out.print("Введите новый тип организации (Enter чтобы оставить): ");
                    String input = commandManager.readLine();
                    if (input.isEmpty()) {
                        break;
                    }
                    orgType = OrganizationType.valueOf(input.toUpperCase());
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка: введите одно из: " + OrganizationType.names() + ". Попробуйте снова.");
                }
            }
            Organization organization = new Organization(
                    oldProduct.getManufacturer().getId(),
                    orgName,
                    orgFullName,
                    orgType
            );
            Product updatedProduct = new Product(
                    id,
                    name,
                    coordinates,
                    oldProduct.getCreationDate(),
                    price,
                    partNumber,
                    manufactureCost,
                    unitOfMeasure,
                    organization
            );
            if (collectionManager.update(id, updatedProduct)) {
                System.out.println("Продукт с ID " + id + " обновлен");
            }

        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть числом");
        }
    }

    private Product findProductById(Integer id) {
        for (Product p : collectionManager.getCollection()) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getDescription() {
        return "обновить значение элемента по ID";
    }
}