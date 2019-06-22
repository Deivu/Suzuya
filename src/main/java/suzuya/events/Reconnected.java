package suzuya.events;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Reconnected extends ListenerAdapter {

    @Override
    public void onReconnect(ReconnectedEvent event) {
        event.getJDA().getPresence().setGame(Game.playing("with Admiral ❤"));
    }
}
