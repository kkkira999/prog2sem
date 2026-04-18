package client;

import common.Request;
import common.Response;
import common.models.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Set;

public class ClientConsole {
    /*
    65535 - 20 (заголовок IPv4) - 8 (заголовок UDP) = 65507 байт - максимальная нагрузка UDP.
    Следовательно, Request("add_batch", list) не должен превышать 65507 байт.
    При тестах получило так, что при 280 элементах выходит примерно 65122 байт, а при 285 - 66267 байт, то есть
    выходят за предел.
    Поэтому, безопасное значение 280 элементов.
     */
    private static final int SCRIPT_ADD_BATCH_SIZE = 280;
    private final Scanner scanner;
    private final UDPClient udpClient;
    private final Set<String> activeScripts = new HashSet<>();

    public ClientConsole(UDPClient udpClient) {
        this.scanner = new Scanner(System.in);
        this.udpClient = udpClient;
    }

    public void start() {
        System.out.println("Клиент запущен. Введите 'help' для списка команд.");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.equals("exit")) {
                System.out.println("Завершение работы клиента");
                break;
            }

            if (input.isEmpty()) {
                continue;
            }

            try {
                processCommand(input);
            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void processCommand(String input) throws Exception {
        String[] parts = input.split(" ");
        String command = parts[0];

        switch (command) {
            case "help":
            case "info":
            case "show":
            case "clear":
            case "save":
            case "print_ascending":
                sendSimpleCommand(command);
                break;

            case "remove_by_id":
                if (parts.length < 2) {
                    System.out.println("Ошибка: укажите ID");
                    return;
                }
                try {
                    int id = Integer.parseInt(parts[1]);
                    Request req = new Request("remove_by_id", id);
                    sendAndPrint(req);
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: ID должен быть числом");
                }
                break;

            case "update":
                if (parts.length < 2) {
                    System.out.println("Ошибка: укажите ID");
                    return;
                }
                try {
                    int id = Integer.parseInt(parts[1]);
                    System.out.println("Введите данные для обновления:");
                    Product product = readProduct();
                    Request req = new Request("update", id, product);
                    sendAndPrint(req);
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: ID должен быть числом");
                }
                break;

            case "add":
                System.out.println("Добавление нового продукта:");
                Product newProduct = readProduct();
                Request addReq = new Request("add", newProduct);
                sendAndPrint(addReq);
                break;

            case "insert_at":
                if (parts.length < 2) {
                    System.out.println("Ошибка: укажите индекс");
                    return;
                }
                try {
                    int index = Integer.parseInt(parts[1]);
                    System.out.println("Вставка на позицию " + index);
                    Product product = readProduct();
                    Request req = new Request("insert_at", index, product);
                    sendAndPrint(req);
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: индекс должен быть числом");
                }
                break;

            case "remove_greater":
                System.out.println("Удаление элементов с ценой больше заданного:");
                System.out.print("Введите цену для сравнения: ");
                try {
                    float price = Float.parseFloat(scanner.nextLine().trim());
                    Product tempProduct = new Product(
                            "temp", new Coordinates(0, 0L), price, null, 0, null,
                            new Organization("temp", null, OrganizationType.COMMERCIAL)
                    );
                    Request req = new Request("remove_greater", tempProduct);
                    sendAndPrint(req);
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: цена должна быть числом");
                }
                break;

            case "remove_lower":
                System.out.println("Удаление элементов с ценой меньше заданного:");
                Product lowerProduct = readProduct();
                Request lowerReq = new Request("remove_lower", lowerProduct);
                sendAndPrint(lowerReq);
                break;

            case "remove_all_by_unit_of_measure":
                if (parts.length < 2) {
                    System.out.println("Ошибка: укажите единицу измерения");
                    System.out.println("Доступные: " + UnitOfMeasure.names());
                    return;
                }
                try {
                    UnitOfMeasure unit = UnitOfMeasure.valueOf(parts[1].toUpperCase());
                    Request req = new Request("remove_all_by_unit_of_measure", unit);
                    sendAndPrint(req);
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка: неверная единица измерения");
                    System.out.println("Доступные: " + UnitOfMeasure.names());
                }
                break;

            case "count_greater_than_manufacture_cost":
                if (parts.length < 2) {
                    System.out.println("Ошибка: укажите значение");
                    return;
                }
                try {
                    double cost = Double.parseDouble(parts[1]);
                    Request req = new Request("count_greater_than_manufacture_cost", cost);
                    sendAndPrint(req);
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: введите число");
                }
                break;

            case "execute_script":
                if (parts.length < 2) {
                    System.out.println("Ошибка: укажите имя файла");
                    return;
                }
                executeScript(parts[1]);
                break;

            default:
                System.out.println("Неизвестная команда. Введите 'help'");
        }
    }

    private void sendSimpleCommand(String command) throws Exception {
        Request req = new Request(command);
        sendAndPrint(req);
    }

    private void sendAndPrint(Request req) throws Exception {
        Response resp = udpClient.sendAndReceive(req);
        if (resp.isSuccess()) {
            if (resp.getMessage() != null && !resp.getMessage().isBlank()) {
                System.out.println(resp.getMessage());
            }
            if (resp.getData() != null) {
                System.out.println(resp.getData());
            }
        } else {
            System.out.println("Ошибка: " + resp.getMessage());
        }
    }

    private void executeScript(String fileName) {
        File scriptFile = new File(fileName);
        String scriptPath = scriptFile.getAbsolutePath();

        if (!activeScripts.add(scriptPath)) {
            System.out.println("Ошибка: обнаружена рекурсия execute_script для файла " + scriptPath);
            return;
        }

        int executedCommands = 0;
        List<Product> addBatch = new ArrayList<>();

        try (Scanner fileScanner = new Scanner(scriptFile)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();

                if (line.isEmpty()) {
                    continue;
                }

                System.out.println("> " + line);
                executedCommands += executeScriptCommand(line, fileScanner, addBatch);
            }

            flushAddBatch(addBatch);
            System.out.println("Скрипт выполнен. Команд: " + executedCommands);
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка: файл не найден - " + fileName);
        } catch (Exception e) {
            System.out.println("Ошибка выполнения скрипта: " + e.getMessage());
        } finally {
            activeScripts.remove(scriptPath);
        }
    }

    private int executeScriptCommand(String input, Scanner fileScanner, List<Product> addBatch) throws Exception {
        String[] parts = input.trim().split("\\s+");
        String command = parts[0];

        switch (command) {
            case "help":
            case "info":
            case "show":
            case "clear":
            case "save":
            case "print_ascending":
                flushAddBatch(addBatch);
                sendSimpleCommand(command);
                return 1;

            case "remove_by_id":
                flushAddBatch(addBatch);
                requireArgs(parts, 2, "Ошибка в скрипте: укажите ID");
                sendAndPrint(new Request("remove_by_id", Integer.parseInt(parts[1])));
                return 1;

            case "count_greater_than_manufacture_cost":
                flushAddBatch(addBatch);
                requireArgs(parts, 2, "Ошибка в скрипте: укажите значение");
                sendAndPrint(new Request("count_greater_than_manufacture_cost", Double.parseDouble(parts[1])));
                return 1;

            case "remove_all_by_unit_of_measure":
                flushAddBatch(addBatch);
                requireArgs(parts, 2, "Ошибка в скрипте: укажите единицу измерения");
                sendAndPrint(new Request(
                        "remove_all_by_unit_of_measure",
                        UnitOfMeasure.valueOf(parts[1].toUpperCase())
                ));
                return 1;

            case "add":
                addBatch.add(readProduct(fileScanner, true));
                if (addBatch.size() >= SCRIPT_ADD_BATCH_SIZE) {
                    flushAddBatch(addBatch);
                }
                return 1;

            case "insert_at":
                flushAddBatch(addBatch);
                requireArgs(parts, 2, "Ошибка в скрипте: укажите индекс");
                sendAndPrint(new Request("insert_at", Integer.parseInt(parts[1]), readProduct(fileScanner, true)));
                return 1;

            case "update":
                flushAddBatch(addBatch);
                requireArgs(parts, 2, "Ошибка в скрипте: укажите ID");
                sendAndPrint(new Request("update", Integer.parseInt(parts[1]), readProduct(fileScanner, true)));
                return 1;

            case "remove_greater":
            case "remove_lower":
                flushAddBatch(addBatch);
                sendAndPrint(new Request(command, readProduct(fileScanner, true)));
                return 1;

            case "execute_script":
                flushAddBatch(addBatch);
                requireArgs(parts, 2, "Ошибка в скрипте: укажите имя файла");
                executeScript(parts[1]);
                return 1;

            default:
                throw new IllegalArgumentException("Неизвестная команда в скрипте: " + command);
        }
    }

    private void flushAddBatch(List<Product> addBatch) throws Exception {
        if (addBatch.isEmpty()) {
            return;
        }

        sendAndPrint(new Request("add_batch", new ArrayList<>(addBatch)));
        addBatch.clear();
    }

    private void requireArgs(String[] parts, int requiredLength, String message) {
        if (parts.length < requiredLength) {
            throw new IllegalArgumentException(message);
        }
    }

    private Product readProduct() {
        return readProduct(scanner, false);
    }

    private Product readProduct(Scanner inputScanner, boolean scriptMode) {
        String name = null;
        while (name == null) {
            try {
                if (!scriptMode) {
                    System.out.print("Введите название: ");
                }
                name = readTrimmedLine(inputScanner, scriptMode, "название");
                if (name.isEmpty()) {
                    if (scriptMode) {
                        throw new IllegalArgumentException("Название не может быть пустым");
                    }
                    System.out.println("Название не может быть пустым. Попробуйте снова.");
                    name = null;
                }
            } catch (Exception e) {
                if (scriptMode) {
                    throw new IllegalArgumentException(e.getMessage());
                }
                System.out.println("Ошибка ввода. Попробуйте снова.");
            }
        }

        int x = 0;
        boolean xValid = false;
        while (!xValid) {
            try {
                if (!scriptMode) {
                    System.out.print("Введите x: ");
                }
                x = Integer.parseInt(readTrimmedLine(inputScanner, scriptMode, "x"));
                xValid = true;
            } catch (NumberFormatException e) {
                if (scriptMode) {
                    throw new IllegalArgumentException("x должен быть целым числом");
                }
                System.out.println("x должен быть целым числом. Попробуйте снова.");
            }
        }

        Long y = null;
        boolean yValid = false;
        while (!yValid) {
            try {
                if (!scriptMode) {
                    System.out.print("Введите y: ");
                }
                y = Long.parseLong(readTrimmedLine(inputScanner, scriptMode, "y"));
                yValid = true;
            } catch (NumberFormatException e) {
                if (scriptMode) {
                    throw new IllegalArgumentException("y должен быть целым числом");
                }
                System.out.println("y должен быть целым числом. Попробуйте снова.");
            }
        }
        Coordinates coordinates = new Coordinates(x, y);

        Float price = null;
        boolean priceValid = false;
        while (!priceValid) {
            try {
                if (!scriptMode) {
                    System.out.print("Введите цену: ");
                }
                price = Float.parseFloat(readTrimmedLine(inputScanner, scriptMode, "цену"));
                if (price <= 0) {
                    if (scriptMode) {
                        throw new IllegalArgumentException("Цена должна быть > 0");
                    }
                    System.out.println("Цена должна быть > 0. Попробуйте снова.");
                } else {
                    priceValid = true;
                }
            } catch (NumberFormatException e) {
                if (scriptMode) {
                    throw new IllegalArgumentException("Цена должна быть числом");
                }
                System.out.println("Цена должна быть числом. Попробуйте снова.");
            }
        }

        if (!scriptMode) {
            System.out.print("Введите partNumber (Enter если null): ");
        }
        String partNumber = readTrimmedLine(inputScanner, scriptMode, "partNumber");
        if (partNumber.isEmpty()) {
            partNumber = null;
        } else if (partNumber.length() < 28) {
            throw new IllegalArgumentException("partNumber должен быть >= 28 символов");
        }

        double manufactureCost = 0;
        boolean costValid = false;
        while (!costValid) {
            try {
                if (!scriptMode) {
                    System.out.print("Введите manufactureCost: ");
                }
                manufactureCost = Double.parseDouble(readTrimmedLine(inputScanner, scriptMode, "manufactureCost"));
                costValid = true;
            } catch (NumberFormatException e) {
                if (scriptMode) {
                    throw new IllegalArgumentException("manufactureCost должно быть числом");
                }
                System.out.println("manufactureCost должно быть числом. Попробуйте снова.");
            }
        }

        if (!scriptMode) {
            System.out.println("Доступные единицы измерения: " + UnitOfMeasure.names());
            System.out.print("Введите unitOfMeasure (Enter если null): ");
        }
        String unitStr = readTrimmedLine(inputScanner, scriptMode, "unitOfMeasure");
        UnitOfMeasure unitOfMeasure = null;
        if (!unitStr.isEmpty()) {
            try {
                unitOfMeasure = UnitOfMeasure.valueOf(unitStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                if (scriptMode) {
                    throw new IllegalArgumentException("Неверная единица измерения");
                }
                System.out.println("Неверная единица измерения. Будет установлено null.");
            }
        }

        String orgName = null;
        while (orgName == null) {
            try {
                if (!scriptMode) {
                    System.out.print("Введите название организации: ");
                }
                orgName = readTrimmedLine(inputScanner, scriptMode, "название организации");
                if (orgName.isEmpty()) {
                    if (scriptMode) {
                        throw new IllegalArgumentException("Название организации не может быть пустым");
                    }
                    System.out.println("Название организации не может быть пустым. Попробуйте снова.");
                    orgName = null;
                }
            } catch (Exception e) {
                if (scriptMode) {
                    throw new IllegalArgumentException(e.getMessage());
                }
                System.out.println("Ошибка ввода. Попробуйте снова.");
            }
        }

        if (!scriptMode) {
            System.out.print("Введите полное название организации (Enter если null): ");
        }
        String orgFullName = readTrimmedLine(inputScanner, scriptMode, "полное название организации");
        if (orgFullName.isEmpty()) {
            orgFullName = null;
        }

        OrganizationType orgType = null;
        while (orgType == null) {
            try {
                if (!scriptMode) {
                    System.out.println("Доступные типы организации: " + OrganizationType.names());
                    System.out.print("Введите тип организации: ");
                }
                String orgTypeStr = readTrimmedLine(inputScanner, scriptMode, "тип организации").toUpperCase();
                orgType = OrganizationType.valueOf(orgTypeStr);
            } catch (IllegalArgumentException e) {
                if (scriptMode) {
                    throw new IllegalArgumentException("Неверный тип организации");
                }
                System.out.println("Неверный тип организации. Доступны: " + OrganizationType.names());
            }
        }

        Organization organization = new Organization(orgName, orgFullName, orgType);

        return new Product(
                name, coordinates, price, partNumber,
                manufactureCost, unitOfMeasure, organization
        );
    }

    private String readTrimmedLine(Scanner inputScanner, boolean scriptMode, String fieldName) {
        if (!inputScanner.hasNextLine()) {
            throw new IllegalArgumentException("Недостаточно данных в скрипте для поля: " + fieldName);
        }
        return inputScanner.nextLine().trim();
    }
}
