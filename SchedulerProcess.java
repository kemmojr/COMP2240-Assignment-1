import java.util.*;

public class SchedulerProcess{

    private String ID = null;
    private int arrive;
    private int execSize;
    private int initialExecSize;
    private int priority;
    private int startTime;
    private int turnAroundTime;
    private int waitingTime;
    private Boolean isHPC;

    public SchedulerProcess(){//default null constructor
        ID = null;
        arrive = -1;
        execSize = -1;
        initialExecSize =-1;
        priority = -1;
        startTime = -1;
        turnAroundTime = -1;
        waitingTime = -1;
        isHPC = null;
    }

    public SchedulerProcess(SchedulerProcess s){//copy constructor
        this.ID = s.ID;
        this.arrive = s.arrive;
        this.execSize = s.execSize;
        this.startTime = s.startTime;
        this.initialExecSize = s.initialExecSize;
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

    public void setInitialExecSize(int e){
        initialExecSize = e;
    }

    public void setPriority(int p) {
        priority = p;
    }

    public void setTurnAroundTime(int t) {
        turnAroundTime = t;
    }

    public void setWaitingTime(int w) {
        if (waitingTime==-1){
            waitingTime = w;
        } else {
            waitingTime += w;
        }

    }

    public void setHPC() {
        isHPC = priority < 3;
    }

    public void setStartTime(int s) {
        startTime = s;
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

    public int getInitialExecSize() {
        return initialExecSize;
    }

    public int getStartTime() {
        return startTime;
    }

    public boolean isIDBefore(SchedulerProcess p){
        String[] p1S = this.ID.split(""), p2S = p.ID.split("");
        int p1 = Integer.parseInt(p1S[1]);
        int p2 = Integer.parseInt(p2S[1]);
        if (p1<p2)
            return true;
        return false;
    }

    public int comparePriority(SchedulerProcess p2){
        SchedulerProcess p1 = this;
        if (p1.getPriority()>p2.getPriority()){
            return 1;
        } else if (p1.getPriority()==p2.getPriority()){
            return 0;
        }
        return -1;
    }

    public boolean isHPC() {
        return priority<3;
    }
}
