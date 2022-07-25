package step1;

public class MockTimeSource implements TimeSource{
    private ClockDriver driver;

    @Override
    public void setDriver(ClockDriver driver) {
        this.driver=driver;
    }
    void setTime(int hours,int minutes,int seconds){
        driver.update(hours, minutes, seconds);
    }
}
