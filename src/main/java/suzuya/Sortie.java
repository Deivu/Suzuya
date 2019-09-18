package suzuya;

import suzuya.client.SuzuyaClient;
import suzuya.events.*;

import javax.security.auth.login.LoginException;
import java.lang.InterruptedException;

public class Sortie {
    public static void main(String[] args) throws LoginException, InterruptedException {
        SuzuyaClient suzuya = new SuzuyaClient();
        suzuya.client.addEventListener(
                new Ready(suzuya),
                new GuildMessage(suzuya),
                new GuildVerificationMessage(suzuya),
                new GuildVoiceUpdate(suzuya),
                new GuildMemberJoin(suzuya),
                new GuildMemberUnverifiedLeave(suzuya)
        );
        suzuya.client.awaitReady();
    }
}