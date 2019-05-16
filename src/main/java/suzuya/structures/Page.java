package suzuya.structures;

public class Page {
    public final Integer current;
    public final Integer max;
    public final Integer start;
    public final Integer end;

    public Page(Integer _current, Integer _max, Integer _start, Integer _end) {
        current = _current;
        max = _max;
        start = _start;
        end = _end;
    }
}
