public abstract class Clock {
    // 1. Pola chronione (dostępne dla klas-dzieci)
    protected int hour;
    protected int minute;
    protected int second;

    public Clock(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    // 2. Metoda abstrakcyjna - nie ma klamerek {}, kończy się średnikiem
    public abstract void tick();

    // 3. Zwykła metoda - każdy zegar ustawia czas tak samo
    public void setTime(int h, int m, int s) {
        this.hour = h;
        this.minute = m;
        this.second = s;
    }

    @Override
    public String toString() {
        return "Clock{" +
                "hour=" + hour +
                ", minute=" + minute +
                ", second=" + second +
                '}';
    }
}