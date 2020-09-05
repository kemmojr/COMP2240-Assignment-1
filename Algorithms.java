import java.util.ArrayList;
import java.util.Scanner;

public class Algorithms {

    int numOfProcesses;
    ArrayList<SchedulerProcess> processes;
    public Algorithms(Scanner inputReader){
        //Go through the input file and convert all the process data into process classes
        String current = inputReader.next();
        numOfProcesses = 0;
        while (!current.equalsIgnoreCase("EOF")){
            if (current.equalsIgnoreCase("ID:")){
                SchedulerProcess p = new SchedulerProcess();
                current = inputReader.next();
                current = inputReader.next();
                p.setArrive(inputReader.nextInt());
                numOfProcesses++;
                System.out.println("ID found");
            }
            current = inputReader.next();
            System.out.println(current);
        }
    }
}
