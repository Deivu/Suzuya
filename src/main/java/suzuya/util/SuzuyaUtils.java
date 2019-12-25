package suzuya.util;

import suzuya.client.SuzuyaClient;
import suzuya.structures.Page;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SuzuyaUtils {
    private final SuzuyaClient suzuya;

    public SuzuyaUtils(SuzuyaClient suzuya) {
        this.suzuya = suzuya;
    }

    public Page paginate(Integer length, Integer page, Integer max) {
        if (page == null) page = 1;
        int limit = length / max + ((length % max == 0) ? 0 : 1);
        int selected = page < 1 ? 1 : page > limit ? limit : page;
        int start = (selected - 1) * max;
        return new Page(selected, limit, start, length > max ? start + max : length);
    }

    public void errorTrace(String title, StackTraceElement[] traces) {
        List<String> trace = Arrays.stream(traces)
                .map(val -> val.toString() + "\n")
                .collect(Collectors.toList());
        trace.add(0, title + "\n");
        suzuya.SuzuyaLog.error(trace.toString());
    }

    public void errorTrace(String message) {
        if (message == null) return;
        suzuya.SuzuyaLog.error(message);
    }
}
