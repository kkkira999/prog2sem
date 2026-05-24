package server.commands;

import server.CommandManager;
import server.CollectionManager;
import common.models.*;

public class InsertAtCommand implements Command {
    private CommandManager commandManager;
    private CollectionManager collectionManager;

    public InsertAtCommand(CommandManager commandManager, CollectionManager collectionManager) {
        this.commandManager = commandManager;
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            System.out.println("Ошибка: укажите индекс");
            return;
        }

        try {
            int index = Integer.parseInt(args[0]);
            if (index < 0 || index > collectionManager.getSize()) {
                System.out.println("Ошибка: индекс должен быть от 0 до " + collectionManager.getSize());
                return;
            }

            System.out.println("Добавление нового продукта на позицию " + index);
            String name = "";
            while (true) {
                try {
                    System.out.println("Введите название:");
                    name = commandManager.readLine();
                    if (name == null || name.trim().isEmpty()) {
                        throw new IllegalArgumentException("Название не может быть пустым");
                    }
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
                }
            }
            int x = 0;
            while (true) {
                try {
                    System.out.println("Введите x:");
                    x = Integer.parseInt(commandManager.readLine());
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: введите целое число. Попробуйте снова.");
                }
            }
            Long y = null;
            while (true) {
                try {
                    System.out.println("Введите y:");
                    y = Long.parseLong(commandManager.readLine());
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: введите целое число. Попробуйте снова.");
                }
            }
            Coordinates coordinates = new Coordinates(x, y);
            Float price = null;
            while (true) {
                try {
                    System.out.println("Введите цену:");
                    price = Float.parseFloat(commandManager.readLine());
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
            String partNumber = null;
            while (true) {
                try {
                    System.out.println("Введите partNumber (Enter, если null):");
                    partNumber = commandManager.readLine();
                    if (partNumber.isEmpty()) {
                        partNumber = null;
                        break;
                    }
                    if (partNumber.length() < 28) {
                        throw new IllegalArgumentException("partNumber должен быть >= 28 символов");
                    }
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
                }
            }
            double manufactureCost = 0;
            while (true) {
                try {
                    System.out.println("Введите manufactureCost:");
                    manufactureCost = Double.parseDouble(commandManager.readLine());
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: введите число. Попробуйте снова.");
                }
            }
            UnitOfMeasure unitOfMeasure = null;
            while (true) {
                try {
                    System.out.println("Доступные единицы измерения: " + UnitOfMeasure.names());
                    System.out.println("Введите unitOfMeasure (Enter, если null):");
                    String unitStr = commandManager.readLine();
                    if (unitStr.isEmpty()) {
                        unitOfMeasure = null;
                        break;
                    }
                    unitOfMeasure = UnitOfMeasure.valueOf(unitStr.toUpperCase());
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка: введите одно из: " + UnitOfMeasure.names() + ". Попробуйте снова.");
                }
            }
            String orgName = "";
            while (true) {
                try {
                    System.out.println("Введите название организации:");
                    orgName = commandManager.readLine();
                    if (orgName == null || orgName.trim().isEmpty()) {
                        throw new IllegalArgumentException("Название организации не может быть пустым");
                    }
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
                }
            }
            System.out.println("Введите полное название организации (Enter, если null):");
            String orgFullName = commandManager.readLine();
            if (orgFullName.isEmpty()) {
                orgFullName = null;
            }
            OrganizationType orgType = null;
            while (true) {
                try {
                    System.out.println("Доступные типы организации: " + OrganizationType.names());
                    System.out.println("Введите тип организации:");
                    String orgTypeStr = commandManager.readLine();
                    orgType = OrganizationType.valueOf(orgTypeStr.toUpperCase());
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка: введите одно из: " + OrganizationType.names() + ". Попробуйте снова.");
                }
            }

            Organization organization = new Organization(orgName, orgFullName, orgType);
            Product product = new Product(
                    name,
                    coordinates,
                    price,
                    partNumber,
                    manufactureCost,
                    unitOfMeasure,
                    organization
            );
            if (collectionManager.insertAt(index, product)) {
                System.out.println("Продукт добавлен на позицию " + index + " с ID: " + product.getId());
            } else {
                System.out.println("Ошибка: не удалось вставить элемент");
            }

        } catch (NumberFormatException e) {
            System.out.println("Ошибка: индекс должен быть числом");
        }
    }

    @Override
    public String getName() {
        return "insert_at";
    }

    @Override
    public String getDescription() {
        return "добавить новый элемент на указанную позицию";
    }
}