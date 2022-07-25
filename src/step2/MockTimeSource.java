package step2;

public class MockTimeSource implements TimeSource {
    private ClockObserver observer;

    @Override
    public void setObserver(ClockObserver observer) {
        this.observer =observer;
    }
    void setTime(int hours,int minutes,int seconds){
        observer.update(hours, minutes, seconds);
    }
}
