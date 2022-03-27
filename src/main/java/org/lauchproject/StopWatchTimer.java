package org.lauchproject;


public class StopWatchTimer {
    String player;
    private long begin, end, time;
    public static final long SECONDS=1000;
    public static final long MINUTES=60000;
    public static final long MILLISECONDS=1;

    /** Constructors **/
    public StopWatchTimer(int time, String player){
        this.time = time* 1000L;
        this.player = player;
    }

    public String getPlayer() {
        return player;
    }

    public void start(){
        begin = System.currentTimeMillis();
    }

    public boolean stop(){
        end = System.currentTimeMillis();
        time = time - (end-begin);
        return time >= 0;// return true if there is still some time left
    }

    public int getTime(long measureUnit) {
        return (int) (time/measureUnit);
    }

}
