package suzuya.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import java.nio.ByteBuffer;

class PlayerSendHandler implements AudioSendHandler {
    private final AudioPlayer player;
    private AudioFrame lastFrame;

    public PlayerSendHandler(AudioPlayer player) {
        this.player = player;
    }

    @Override
    public boolean canProvide() {
        if (lastFrame == null) lastFrame = player.provide();
        return lastFrame != null;
    }

    @Override
    public ByteBuffer provide20MsAudio() {
        if (lastFrame == null) lastFrame = player.provide();
        ByteBuffer data = ByteBuffer.wrap(lastFrame.getData());
        lastFrame = null;
        return data;
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
