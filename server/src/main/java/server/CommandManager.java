package server;

import server.commands.*;
import common.utils.InputReader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private Map<String, Command> commands;
    private InputReader currentReader;

    public CommandManager(CollectionManager collectionManager, FileManager fileManager, InputReader reader) {
        this.currentReader = reader;
        this.commands = new HashMap<>();
        registerCommands(collectionManager, fileManager);
    }

    private void registerCommands(CollectionManager collectionManager, FileManager fileManager) {
        try {
            String pathToClasses = ".gradle-build/classes/java/main/server/commands";

            File directory = new File(pathToClasses);
            File[] classFiles = directory.listFiles((dir, name) -> name.endsWith(".class"));
            if (classFiles == null) {
                System.err.println("Папка со скомпилированными командами не найдена: " + pathToClasses);
                return;
            }

            for (File classFile : classFiles) {
                String className = classFile.getName().replace(".class", "");
                String fullClassName = "server.commands." + className;

                if (fullClassName.equals("server.commands.Command")) {
                    continue;
                }

                try {
                    Class<?> clazz = Class.forName(fullClassName);

                    if (Command.class.isAssignableFrom(clazz)) {
                        Command command = createCommandInstance(clazz, collectionManager, fileManager);
                        if (command != null) {
                            commands.put(command.getName(), command);
                            System.out.println("Загружена команда: " + command.getName());
                        }
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Не удалось загрузить класс: " + className);
                }
            }

            System.out.println("Всего загружено команд: " + commands.size());

        } catch (Exception e) {
            System.err.println("Ошибка при загрузке команд: " + e.getMessage());
        }
    }

    private Command createCommandInstance(Class<?> clazz, CollectionManager cm, FileManager fm) {
        try {
            try {
                return (Command) clazz.getConstructor(CommandManager.class, CollectionManager.class)
                        .newInstance(this, cm);
            } catch (NoSuchMethodException e1) {
                try {
                    return (Command) clazz.getConstructor(CommandManager.class)
                            .newInstance(this);
                } catch (NoSuchMethodException e2) {
                    try {
                        return (Command) clazz.getConstructor(CollectionManager.class, FileManager.class)
                                .newInstance(cm, fm);
                    } catch (NoSuchMethodException e3) {
                        try {
                    return (Command) clazz.getConstructor(CollectionManager.class)
                            .newInstance(cm);
                        } catch (NoSuchMethodException e4) {
                            try {
                        return (Command) clazz.getConstructor(CommandManager.class, CollectionManager.class, FileManager.class)
                                .newInstance(this, cm, fm);
                            } catch (NoSuchMethodException e5) {
                                try {
                            return (Command) clazz.getConstructor(Map.class)
                                    .newInstance(commands);
                                } catch (NoSuchMethodException e6) {
                                    try {
                                return (Command) clazz.getConstructor().newInstance();
                                    } catch (NoSuchMethodException e7) {
                                System.err.println("Нет подходящего конструктора для: " + clazz.getSimpleName());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при создании команды " + clazz.getSimpleName() + ": " + e.getMessage());
        }
        return null;
    }

    public void executeCommand(String input) {
        String[] parts = input.split(" ");
        String commandName = parts[0];

        Command command = commands.get(commandName);

        if (command != null) {
            String[] args = new String[parts.length - 1];
            System.arraycopy(parts, 1, args, 0, parts.length - 1);
            command.execute(args);
        } else {
            System.out.println("Неизвестная команда. Введите 'help'");
        }
    }

    public void setInputReader(InputReader reader) {
        this.currentReader = reader;
    }

    public InputReader getCurrentReader() {
        return currentReader;
    }

    public String readLine() {
        return currentReader.readLine();
    }

    public int getCommandsCount() {
        return commands.size();
    }

    public String getCommandNames() {
        return String.join(", ", commands.keySet());
    }
}
