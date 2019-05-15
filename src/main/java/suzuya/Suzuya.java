package suzuya;

import suzuya.events.GuildMessage;
import suzuya.events.Ready;

import javax.security.auth.login.LoginException;
import java.lang.InterruptedException;

public class Suzuya {
    public static void main(String[] args) throws LoginException, InterruptedException {
        SuzuyaClient suzuya = new SuzuyaClient();
        suzuya.client.addEventListener(new Ready(suzuya), new GuildMessage(suzuya));
        suzuya.client.awaitReady();
    }
}
