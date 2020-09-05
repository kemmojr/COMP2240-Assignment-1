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
}
