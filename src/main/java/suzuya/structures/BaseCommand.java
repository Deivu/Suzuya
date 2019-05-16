package suzuya.structures;

public abstract class BaseCommand {

    public abstract String getTitle();

    public abstract String getUsage();

    public abstract String getDescription();

    public abstract String getCategory();

    public abstract String run(HandlerArgs handler, Settings config, String[] args);
}
