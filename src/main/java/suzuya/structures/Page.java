package suzuya.structures;

public class Page {
    public final Integer current;
    public final Integer max;
    public final Integer start;
    public final Integer end;

    public Page(Integer current, Integer max, Integer start, Integer end) {
        this.current = current;
        this.max = max;
        this.start = start;
        this.end = end;
    }
}
