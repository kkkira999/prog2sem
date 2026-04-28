package server.commands;

import server.CommandManager;
import common.utils.InputReader;
import common.utils.FileInputReader;
import common.utils.ConsoleInputReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ExecuteScriptCommand implements Command {
    private CommandManager commandManager;

    public ExecuteScriptCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            System.out.println("Ошибка: укажите имя файла со скриптом");
            return;
        }

        String fileName = args[0];
        System.out.println("Выполнение скрипта из файла: " + fileName);

        Scanner fileScanner = null;
        InputReader oldReader = null;

        try {
            File scriptFile = new File(fileName);
            fileScanner = new Scanner(scriptFile);
            oldReader = commandManager.getCurrentReader();
            FileInputReader fileReader = new FileInputReader(fileScanner);
            commandManager.setInputReader(fileReader);

            int lineNumber = 0;
            int successfulCommands = 0;
            boolean hasError = false;

            while (fileScanner.hasNextLine() && !hasError) {
                lineNumber++;
                String commandLine = fileScanner.nextLine().trim();

                if (commandLine.isEmpty()) {
                    continue;
                }

                System.out.println("> " + commandLine);

                try {
                    commandManager.executeCommand(commandLine);
                    successfulCommands++;
                } catch (Exception e) {
                    System.out.println("Ошибка при выполнении команды '" + commandLine + "': " + e.getMessage());
                    System.out.println("Выполнение скрипта прервано на строке " + lineNumber);
                    hasError = true;
                }
            }

            if (hasError) {
                System.out.println("Скрипт выполнен с ошибками. Успешно выполнено команд: " + successfulCommands);
            } else {
                System.out.println("Скрипт выполнен успешно. Всего команд: " + lineNumber);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Ошибка: файл не найден - " + fileName);
        } finally {
            if (fileScanner != null) {
                fileScanner.close();
            }
            if (oldReader != null) {
                commandManager.setInputReader(oldReader);
            } else {
                commandManager.setInputReader(new ConsoleInputReader(new Scanner(System.in)));
            }
        }
    }

    @Override
    public String getName() {
        return "execute_script";
    }

    @Override
    public String getDescription() {
        return "считать и исполнить скрипт из файла";
    }
}