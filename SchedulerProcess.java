import java.util.Scanner;

public class SchedulerProcess {

    String ID = null;
    int arrive;
    int execSize;
    int priority;

    public SchedulerProcess(){
        ID = null;
        arrive = -1;
        execSize = -1;
        priority = -1;
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

    public boolean isBefore(SchedulerProcess p){
        String[] p1S = this.ID.split(""), p2S = p.ID.split("");
        int p1 = Integer.parseInt(p1S[1]);
        int p2 = Integer.parseInt(p2S[1]);
        if (p1<p2)
            return true;
        return false;
    }
}
