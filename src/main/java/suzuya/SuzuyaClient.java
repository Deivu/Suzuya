package suzuya;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import suzuya.handler.CommandHandler;
import suzuya.handler.SettingsHandler;
import suzuya.handler.TagsHandler;
import suzuya.player.SuzuyaPlayer;
import suzuya.structures.Page;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ConcurrentHashMap;

public class SuzuyaClient {
    private final Config config = new Config();

    public final JDA client = new JDABuilder(config.getToken()).build();
    public final AudioPlayerManager PlayerManager = new DefaultAudioPlayerManager();
    public final ConcurrentHashMap<String, SuzuyaPlayer> players = new ConcurrentHashMap<String, SuzuyaPlayer>();

    public final CommandHandler commandHandler = new CommandHandler();
    public final SettingsHandler settingsHandler = new SettingsHandler(config);
    public final TagsHandler tagsHandler = new TagsHandler(config);

    public final Color defaultEmbedColor = new Color(62, 180, 137);

    public final OperatingSystemMXBean system = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    public final Runtime runtime = Runtime.getRuntime();

    public Boolean isClientReady = false;

    SuzuyaClient() throws LoginException {
        System.out.println("Working Directory is in:" + config.getDir());
        settingsHandler.initDb("Suzuya.db");
        tagsHandler.initDb("SuzuyaTags.db");
        System.out.println("Database initialized without errors");
        setPlayerSettings();
    }

    private void setPlayerSettings() {
        AudioConfiguration audioConfig = PlayerManager.getConfiguration();
        audioConfig.setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);
        audioConfig.setOpusEncodingQuality(10);
        System.out.println("AudioPlayer settings are now set.");
        YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager();
        youtube.setPlaylistPageCount(500);
        PlayerManager.registerSourceManager(youtube);
        PlayerManager.registerSourceManager(new BandcampAudioSourceManager());
        PlayerManager.registerSourceManager(new SoundCloudAudioSourceManager());
        PlayerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        PlayerManager.registerSourceManager(new VimeoAudioSourceManager());
        PlayerManager.registerSourceManager(new BeamAudioSourceManager());
        PlayerManager.registerSourceManager(new HttpAudioSourceManager());
        PlayerManager.registerSourceManager(new LocalAudioSourceManager());
        System.out.println("Registered the AudioPlayer sources managers");
    }

    public Page paginate(Integer length, Integer page, Integer max) {
        if (page == null) page = 1;
        int limit = length / max + ((length % max == 0) ? 0 : 1);
        int selected = page < 1 ? 1 : page > limit ? limit : page;
        int start = (selected - 1) * max;
        return new Page(selected, limit, start, length > max ? start + max : length);
    }
}
