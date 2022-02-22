package org.lauchproject;
import java.util.concurrent.TimeUnit;

public class StopWatchTimer {
    private long begin, end, time;
    public static final long SECONDS=1000;
    public static final long MINUTES=60000;
    public static final long MILLISECONDS=1;

    /** Constructors **/
    public StopWatchTimer(int time){
        this.time = time*1000;
    }
    public StopWatchTimer(){}

    //Setter
    public void setTime(int time){
        this.time = time*1000;
    }

    public void start(){
        begin = System.currentTimeMillis();
    }

    public boolean stop(){
        end = System.currentTimeMillis();
        time = time - (end-begin);
        if (time < 0)
            return false;
        return true; // return true if there is still some time left
    }

    public int getTime(long measureUnit) {
        return (int) (time/measureUnit);
    }

    public static void main(String[] arg) {
        StopWatchTimer ch = new StopWatchTimer(3);

        ch.start();
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!ch.stop())
            System.out.println("tempo finito");
        else
            System.out.println(ch.getTime(SECONDS));
    }
}
