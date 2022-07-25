package step1;

import junit.framework.TestCase;

public class ClockDriverTest extends TestCase {
    public ClockDriverTest(String name){
        super(name);
    }
    public void testTimeChange(){
        MockTimeSource source=new MockTimeSource();
        MockTimeSink sink= new MockTimeSink();
        ClockDriver driver = new ClockDriver(source,sink);
        source.setTime(2,3,4);
        assertEquals(2,sink.getHours());
        assertEquals(3,sink.getMinutes());
        assertEquals(4,sink.getSeconds());
        source.setTime(7,8,9);
        assertEquals(7,sink.getHours());
        assertEquals(8,sink.getMinutes());
        assertEquals(9,sink.getSeconds());
    }
}
