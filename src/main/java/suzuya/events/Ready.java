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

    public Ready(SuzuyaClient suzuya) {
        this.suzuya = suzuya;
    }

    private void intervalPlaying() {
        Presence presence = suzuya.client.getPresence();
        presence.setGame(Game.playing(String.format("with %s users", suzuya.client.getUserCache().size())));
    }

    @Override
    public void onReady(ReadyEvent event) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(this::intervalPlaying, 1, 60, TimeUnit.SECONDS);
        for (Guild guild : suzuya.client.getGuilds()) {
            suzuya.settingsHandler.setDefaults(guild.getId());
        }
        System.out.println(
                String.format("Shipgirl %s is now logged in to Discord", suzuya.client.getSelfUser().getName())
        );
        long channels = suzuya.client.getPrivateChannelCache().size() + suzuya.client.getVoiceChannelCache().size() + suzuya.client.getTextChannelCache().size();
        System.out.println(
                String.format("Currently serving %s Guild(s), %s Channel(s) and %s User(s)",
                        suzuya.client.getGuildCache().size(),
                        channels,
                        suzuya.client.getUserCache().size()
                )
        );
        suzuya.isClientReady = true;
    }
}
