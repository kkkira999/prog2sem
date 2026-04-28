package server.commands;

import java.util.Map;

public class HelpCommand implements Command {
    private Map<String, Command> commands;

    public HelpCommand(Map<String, Command> commands) {
        this.commands = commands;
    }

    @Override
    public void execute(String[] args) {
        System.out.println("\nДоступные команды");

        for (Command cmd : commands.values()) {
            System.out.println(cmd.getName() + " - " + cmd.getDescription());
        }
    }

    @Override
    public String getDescription() {
        return "вывод справки по доступным командам";
    }

    @Override
    public String getName() {
        return "help";
    }
}
