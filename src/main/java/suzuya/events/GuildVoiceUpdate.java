package suzuya.events;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import suzuya.SuzuyaClient;
import suzuya.player.SuzuyaPlayer;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class GuildVoiceUpdate extends ListenerAdapter {
    private final BlockingQueue<String> process = new LinkedBlockingQueue<>();
    private final SuzuyaClient suzuya;

    public GuildVoiceUpdate(SuzuyaClient suzuya) {
        this.suzuya = suzuya;
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        Guild guild = event.getGuild();
        if (process.contains(guild.getId())) return;
        if (!suzuya.players.containsKey(guild.getId())) return;
        SuzuyaPlayer player = suzuya.players.get(guild.getId());
        VoiceChannel channel = event.getChannelLeft();
        if (!player.voiceChannel.getId().equals(channel.getId())) return;
        List<Member> members = channel.getMembers()
                .stream()
                .filter(m ->
                    !m.getUser().isBot() && !m.getUser().getId().equals(suzuya.client.getSelfUser().getId())
                )
                .collect(Collectors.toList());
        if (members.size() >= 1) return;
        process.offer(guild.getId());
        Executor exe = CompletableFuture.delayedExecutor(30, TimeUnit.SECONDS);
        exe.execute(() -> {
            try {
                List<Member> _members = channel.getMembers()
                        .stream()
                        .filter(m ->
                                !m.getUser().isBot() && !m.getUser().getId().equals(suzuya.client.getSelfUser().getId())
                        )
                        .collect(Collectors.toList());
                if (_members.size() >= 1) return;
                player.destroy();
            } catch (Exception error) {
                suzuya.errorTrace(error.getMessage(), error.getStackTrace());
            } finally {
                process.remove(guild.getId());
            }
        });
    }
}
