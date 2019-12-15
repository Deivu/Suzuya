package suzuya.events;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import suzuya.client.SuzuyaClient;

import java.util.concurrent.TimeUnit;

public class Ready extends ListenerAdapter {
    private final SuzuyaClient suzuya;
    private final String[] status = {
            "Oh! If it isn't the Admiral! 'Sup!",
            "Admiraaall, I'm seriously bored",
            "Just leave it to Suzuya~!",
            "Something's...really slimy~!",
            "I told you not to touch Suzuya's deck knee socks like that! Geez"
    };
    private int counter = 0;

    public Ready(SuzuyaClient suzuya) {
        this.suzuya = suzuya;
    }
    
    @Override
    public void onReady(ReadyEvent event) {
        for (Guild guild : suzuya.client.getGuildCache()) {
            suzuya.settingsHandler.setDefaults(guild.getId());
        }
        suzuya.SuzuyaLog.info(String.format("%s is now logged in to Discord", suzuya.client.getSelfUser().getName()));
        suzuya.SuzuyaLog.info(
                String.format("Currently serving %s Guild(s), %s Channel(s) and %s User(s)",
                        suzuya.client.getGuildCache().size(),
                        suzuya.client.getPrivateChannelCache().size() + suzuya.client.getVoiceChannelCache().size() + suzuya.client.getTextChannelCache().size(),
                        suzuya.client.getUserCache().size()
                )
        );
        suzuya.scheduler.scheduleAtFixedRate(() -> suzuya.client.getPresence().setActivity(Activity.playing(getStatus())), 0, 120, TimeUnit.SECONDS);
        suzuya.isClientReady = true;
    }

    private String getStatus() {
        if (counter > status.length - 1) counter = 0;
        String current = status[counter];
        counter++;
        return current;
    }
}
