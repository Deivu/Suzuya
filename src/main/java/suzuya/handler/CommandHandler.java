package suzuya.handler;

import org.reflections.Reflections;
import suzuya.SuzuyaClient;
import suzuya.structures.BaseCommand;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CommandHandler {
    private final ConcurrentHashMap<String, BaseCommand> commands = new ConcurrentHashMap<>();
    private final SuzuyaClient suzuya;

    public CommandHandler(SuzuyaClient suzuya) {
        this.suzuya = suzuya;
        init();
    }

    private void init() {
        Reflections reflections = new Reflections("suzuya.commands");
        Set<Class<? extends BaseCommand>> subTypes = reflections.getSubTypesOf(BaseCommand.class);
        suzuya.SuzuyaLog.info("Trying to load a total of " + subTypes.size() + " commands");
        for (Class<? extends BaseCommand> s : subTypes) {
            try {
                BaseCommand command = s.getConstructor().newInstance();
                commands.putIfAbsent(command.getTitle(), command);
                suzuya.SuzuyaLog.debug("Loaded Command: " + command.getTitle());
            } catch (Exception error) {
                suzuya.errorTrace(error.getStackTrace());
            }
        }
        suzuya.SuzuyaLog.info("Loaded " + commands.size() + " commands");
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

    public int getCommandsSize() { return commands.size(); }
}
