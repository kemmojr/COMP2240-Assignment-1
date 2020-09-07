import java.util.Scanner;

public class SchedulerProcess {

    private String ID = null;
    private int arrive;
    private int execSize;
    private int priority;
    private int turnAroundTime;
    private int waitingTime;

    public SchedulerProcess(){//default null constructor
        ID = null;
        arrive = -1;
        execSize = -1;
        priority = -1;
    }

    public SchedulerProcess(SchedulerProcess s){//copy costructor
        this.ID = s.ID;
        this.arrive = s.arrive;
        this.execSize = s.execSize;
        this.priority = s.priority;
    }

    public void setID(String id) {
        ID = id;
    }

    public void setArrive(int a) {
        arrive = a;
    }

    public void setExecSize(int e) {
        execSize = e;
    }

    public void setPriority(int p) {
        priority = p;
    }

    public void setTurnAroundTime(int t) {
        turnAroundTime = t;
    }

    public void setWaitingTime(int w) {
        waitingTime = w;
    }

    public String getID() {
        return ID;
    }

    public int getArrive() {
        return arrive;
    }

    public int getExecSize() {
        return execSize;
    }

    public int getPriority() {
        return priority;
    }

    public int getTurnAroundTime() {
        return turnAroundTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public boolean isBefore(SchedulerProcess p){
        String[] p1S = this.ID.split(""), p2S = p.ID.split("");
        int p1 = Integer.parseInt(p1S[1]);
        int p2 = Integer.parseInt(p2S[1]);
        if (p1<p2)
            return true;
        return false;
    }
}
