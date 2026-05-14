import java.util.ArrayList;
import java.util.List;

public class DigitalClock extends Clock {

    @Override
    public void tick() {
        second++;
        if (second >= 60) {
            second = 0;
            minute++;
        }
        if (minute >= 60) {
            minute = 0;
            hour++;
        }
        if (hour >= 24) {
            hour = 0;
        }
    }

    public DigitalClock(int hour, int minute, int second) {
        super(hour, minute, second);
    }

    @Override
    public String toString() {
        return "DigitalClock{" +
                "second=" + second +
                ", minute=" + minute +
                ", hour=" + hour +
                '}';
    }
}
