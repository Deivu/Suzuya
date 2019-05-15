package suzuya.events;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.Presence;
import suzuya.SuzuyaClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Ready extends ListenerAdapter {
    private final SuzuyaClient suzuya;

    public Ready(SuzuyaClient client) {
        suzuya = client;
    }

    private void intervalPlaying() {
        Presence presence = suzuya.client.getPresence();
        presence.setGame(Game.playing(String.format("with %o users", suzuya.client.getUserCache().size())));
    }

    @Override
    public void onReady(ReadyEvent event) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(this::intervalPlaying, 1, 60, TimeUnit.SECONDS);
        for (Guild guild : suzuya.client.getGuilds()) {
            suzuya.settingsHandler.setDefaults(guild.getId());
        }
        System.out.println("Suzuya has logged in");
        suzuya.isClientReady = true;
    }
}
