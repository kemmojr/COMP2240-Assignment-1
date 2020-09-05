import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

public class Algorithms {

    int numOfProcesses;
    ArrayList<SchedulerProcess> processes;
    public Algorithms(Scanner inputReader){
        //Go through the input file and convert all the process data into process classes
        String current = "";
        numOfProcesses = 0;
        processes = new ArrayList<>();
        while (!current.equalsIgnoreCase("EOF")){
            current = inputReader.next();
            if (current.equalsIgnoreCase("ID:")){
                SchedulerProcess p = new SchedulerProcess();
                p.setID(inputReader.next());
                while (!current.equalsIgnoreCase("END")){
                    if (current.equalsIgnoreCase("Arrive:")){
                        p.setArrive(inputReader.nextInt());
                    } else if (current.equalsIgnoreCase("ExecSize:")){
                        p.setExecSize(inputReader.nextInt());
                    } else if (current.equalsIgnoreCase("Priority:")){
                        p.setPriority(inputReader.nextInt());
                    }
                    current = inputReader.next();
                }
                numOfProcesses++;
                processes.add(p);
            }
        }
    }

    public void FCFS(){
        //Execute the first come first served algorithm
        ArrayList<SchedulerProcess> temp = processes;
        Queue<SchedulerProcess> readyQueue = new PriorityQueue<>();
        SchedulerProcess processing;//Current item that is being processed
        int numToRemove =0;
        int time = 0;
        while (temp.size()>0){
            for (int i = 0; i < temp.size(); i++) {//Iterate through all the elements of temp and and add all of the elements that have a start time that matches current time to the ready queue
                if (temp.get(i).getArrive()==time){
                    readyQueue.add(temp.get(i));
                    numToRemove++;
                }
            }
            for (int i = 0; i < numToRemove; i++) {
                temp.remove(i);
            }
            numToRemove = 0;
            processing = readyQueue.poll();

        }

    }
}
