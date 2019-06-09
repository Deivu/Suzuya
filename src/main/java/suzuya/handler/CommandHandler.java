package suzuya.handler;

import org.reflections.Reflections;
import suzuya.structures.BaseCommand;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CommandHandler {
    private final ConcurrentHashMap<String, BaseCommand> commands;

    public CommandHandler() {
        this.commands = new ConcurrentHashMap<>();
        loadCommands();
    }

    private void loadCommands() {
        Reflections reflections = new Reflections("suzuya.commands");
        Set<Class<? extends BaseCommand>> subTypes = reflections.getSubTypesOf(BaseCommand.class);
        System.out.println("Trying to load a total of " + subTypes.size() + " commands");
        for (Class<? extends BaseCommand> s : subTypes) {
            try {
                BaseCommand command = s.getConstructor().newInstance();
                commands.putIfAbsent(command.getTitle(), command);
                System.out.println("Loaded Command: " + command.getTitle());
            } catch (Exception error) {
                error.printStackTrace();
            }
        }
        System.out.println("Loaded " + commands.size() + " commands");
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
