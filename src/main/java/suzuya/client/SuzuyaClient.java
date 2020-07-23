package suzuya.client;

import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sun.management.OperatingSystemMXBean;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import suzuya.util.SuzuyaConfig;
import suzuya.Sortie;
import suzuya.handler.CommandHandler;
import suzuya.handler.SettingsHandler;
import suzuya.handler.TagsHandler;
import suzuya.player.SuzuyaPlayer;
import suzuya.structures.CaptchaExecutor;
import suzuya.util.SuzuyaUtils;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SuzuyaClient {
    public final Logger SuzuyaLog = LoggerFactory.getLogger(Sortie.class);

    public final SuzuyaConfig suzuyaConfig = new SuzuyaConfig(this);
    public final SuzuyaUtils util = new SuzuyaUtils(this);

    public final AudioPlayerManager PlayerManager = new DefaultAudioPlayerManager();
    public final ConcurrentHashMap<String, SuzuyaPlayer> players = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, CaptchaExecutor> captcha = new ConcurrentHashMap<>();

    public final Color defaultEmbedColor = new Color(62, 180, 137);

    public final OperatingSystemMXBean system = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    public final RuntimeMXBean runtime_mx = ManagementFactory.getRuntimeMXBean();
    public final Runtime runtime = Runtime.getRuntime();

    public final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    public final ExecutorService executors = Executors.newCachedThreadPool();

    public final JDA client;
    public final CommandHandler commandHandler;
    public final SettingsHandler settingsHandler;
    public final TagsHandler tagsHandler;

    public Boolean isClientReady = false;

    public SuzuyaClient() throws LoginException {
        commandHandler = new CommandHandler(this)
                .init();
        settingsHandler = new SettingsHandler(this)
                .init();
        tagsHandler = new TagsHandler(this)
                .init();
        PlayerManager
                .setFrameBufferDuration(1000);
        PlayerManager
                .getConfiguration()
                .setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);
        PlayerManager
                .getConfiguration()
                .setOpusEncodingQuality(10);
        PlayerManager.registerSourceManager(new YoutubeAudioSourceManager(true));
        PlayerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        PlayerManager.registerSourceManager(new BandcampAudioSourceManager());
        PlayerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        PlayerManager.registerSourceManager(new VimeoAudioSourceManager());
        PlayerManager.registerSourceManager(new BeamAudioSourceManager());
        PlayerManager.registerSourceManager(new HttpAudioSourceManager());

        client = JDABuilder
                .createDefault(suzuyaConfig.getToken())
                .setMemberCachePolicy(
                        MemberCachePolicy.any(
                                MemberCachePolicy.ONLINE,
                                MemberCachePolicy.OWNER,
                                MemberCachePolicy.VOICE
                        )
                )
                .disableIntents(
                        GatewayIntent.DIRECT_MESSAGE_TYPING,
                        GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_MESSAGE_TYPING
                )
                .disableCache(CacheFlag.ACTIVITY)
                .setChunkingFilter(ChunkingFilter.NONE)
                .setAudioSendFactory(new NativeAudioSendFactory())
                .build();
    }
}
