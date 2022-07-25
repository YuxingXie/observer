package step1;

public class MockTimeSink implements TimeSink{
    private int hours;
    private int minutes;
    private int seconds;

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    @Override
    public void setTime(int hours, int minutes, int seconds) {
        this.hours=hours;
        this.minutes=minutes;
        this.seconds=seconds;
    }
}
