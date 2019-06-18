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
    
    @Override
    public void onReady(ReadyEvent event) {
        for (Guild guild : suzuya.client.getGuildCache()) {
            suzuya.settingsHandler.setDefaults(guild.getId());
        }
        suzuya.SuzuyaLog.info(
                String.format("%s is now logged in to Discord", suzuya.client.getSelfUser().getName())
        );
        long channels = suzuya.client.getPrivateChannelCache().size() + suzuya.client.getVoiceChannelCache().size() + suzuya.client.getTextChannelCache().size();
        suzuya.SuzuyaLog.info(
                String.format("Currently serving %s Guild(s), %s Channel(s) and %s User(s)",
                        suzuya.client.getGuildCache().size(),
                        channels,
                        suzuya.client.getUserCache().size()
                )
        );
        suzuya.isClientReady = true;
    }
}
