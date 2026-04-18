package server.commands;

import server.CommandManager;
import server.CollectionManager;
import common.models.*;
import common.utils.FileInputReader;

public class AddCommand implements Command {
    private CommandManager commandManager;
    private CollectionManager collectionManager;

    public AddCommand(CommandManager commandManager, CollectionManager collectionManager) {
        this.commandManager = commandManager;
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String[] args) {
        System.out.println("Добавление нового продукта");
        boolean isScriptMode = commandManager.getCurrentReader() instanceof FileInputReader;

        if (isScriptMode) {
            System.out.println(" (чтение данных из файла)");
        }

        try {
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
                    if (isScriptMode) {
                        throw new RuntimeException("Ошибка в скрипте: " + e.getMessage());
                    }
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
                    if (isScriptMode) {
                        throw new RuntimeException("Ошибка в скрипте: x должен быть числом");
                    }
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
                    if (isScriptMode) {
                        throw new RuntimeException("Ошибка в скрипте: y должен быть числом");
                    }
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
                    if (isScriptMode) {
                        throw new RuntimeException("Ошибка в скрипте: цена должна быть числом");
                    }
                    System.out.println("Ошибка: введите число. Попробуйте снова.");
                } catch (IllegalArgumentException e) {
                    if (isScriptMode) {
                        throw new RuntimeException("Ошибка в скрипте: " + e.getMessage());
                    }
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
                    if (isScriptMode) {
                        throw new RuntimeException("Ошибка в скрипте: " + e.getMessage());
                    }
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
                    if (isScriptMode) {
                        throw new RuntimeException("Ошибка в скрипте: manufactureCost должна быть числом");
                    }
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
                    if (isScriptMode) {
                        throw new RuntimeException("Ошибка в скрипте: неверная единица измерения");
                    }
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
                    if (isScriptMode) {
                        throw new RuntimeException("Ошибка в скрипте: " + e.getMessage());
                    }
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
                    if (isScriptMode) {
                        throw new RuntimeException("Ошибка в скрипте: неверный тип организации");
                    }
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

            collectionManager.add(product);
            System.out.println("Продукт создан и добавлен в коллекцию с ID: " + product.getId());

        } catch (Exception e) {
            System.out.println("Ошибка при добавлении продукта: " + e.getMessage());
            if (isScriptMode) {
                System.out.println("Выполнение скрипта будет прервано.");
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "добавление нового элемента";
    }
}