import java.util.Comparator;

public class SchedulerProcessComparator implements Comparator<SchedulerProcess> {
    @Override
    public int compare(SchedulerProcess x, SchedulerProcess y) {

        if (x.getPriority() < y.getPriority()) {
            return -1;
        }
        if (x.getPriority() > y.getPriority()) {
            return 1;
        }
        return 0;
    }
}
