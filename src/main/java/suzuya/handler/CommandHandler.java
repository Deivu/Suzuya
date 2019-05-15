package suzuya.handler;

import suzuya.structures.BaseCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.reflections.Reflections;

public class CommandHandler {
    private final HashMap<String, BaseCommand> commands;

    public CommandHandler() {
        commands = new HashMap<>();
        loadCommands();
    }

    private void loadCommands() {
        Reflections reflections = new Reflections("suzuya.commands");
        Set<Class<? extends BaseCommand>> subTypes = reflections.getSubTypesOf(BaseCommand.class);
        System.out.println("Loading Commands");
        for (Class<? extends BaseCommand> s : subTypes) {
            try {
                BaseCommand command = s.getConstructor().newInstance();
                commands.put(command.getTitle(), command);
            } catch (Exception error) {
                error.printStackTrace();
            }
        }
        System.out.println(String.format("Loaded %o command(s) in cache", commands.size()));
    }

    public BaseCommand getCommand(String command) {
        return commands.get(command);
    }

    public ArrayList<String> getCommandsInCategory(String category) {
        ArrayList<String> array = new ArrayList<>();
        for (BaseCommand c : commands.values()) {
            if (c.getCategory().equals(category)) {
                array.add(String.format("`%s`", c.getTitle()));
            }
        }
        return array;
    }
}
