import java.util.*;

public class Algorithms {

    int numOfProcesses;
    ArrayList<SchedulerProcess> processes, FCFSProcessed, SPNProcessed, PPProcessed, PRRProcessed;
    int dispatchTime;

    public Algorithms(Scanner inputReader){
        //Go through the input file and convert all the process data into process classes
        FCFSProcessed = new ArrayList<>();
        SPNProcessed = new ArrayList<>();
        PPProcessed = new ArrayList<>();
        PRRProcessed = new ArrayList<>();
        String current = "";
        numOfProcesses = 0;
        processes = new ArrayList<>();
        while (!current.equalsIgnoreCase("EOF")){
            current = inputReader.next();
            if (current.equalsIgnoreCase("DISP:")){
                current = inputReader.next();
                dispatchTime = Integer.parseInt(current);
            }

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
        Queue<SchedulerProcess> readyQueue = new LinkedList<>();
        SchedulerProcess processing = null;//Current item that is being processed
        int numToRemove =0, processingTimeRemaining =0, time = 0;
        boolean allItemsExecuted = false;
        while (!allItemsExecuted){
            int lastTime = time;
            for (int i = 0; i < temp.size(); i++) {//Iterate through all the elements of temp and and add all of the elements that have a start time that matches current time to the ready queue
                if (temp.get(i).getArrive()<=time){
                    readyQueue.add(temp.get(i));
                    numToRemove++;
                }
            }
            for (int i = numToRemove-1; i > -1; i--) {//Remove all of the SchedulerProcesses from the temp list that were added to the readyQueue
                temp.remove(i);
            }
            numToRemove = 0;
            if (processingTimeRemaining==0){
                if (processing!=null){
                    processing.setTurnAroundTime(-(processing.getArrive()-time));
                    FCFSProcessed.add(processing);
                }
                if (readyQueue.size()>0){//Add the turnaroundTime to the processing SchedulerProcess
                    time += dispatchTime;
                    processing = readyQueue.poll();
                    processing.setWaitingTime(-(processing.getArrive()-time));
                    processingTimeRemaining = processing.getExecSize();
                } else if (temp.isEmpty()){
                    allItemsExecuted = true;
                } else {
                    processing = null;
                }

            }

            time++;
            processingTimeRemaining -= time-lastTime;


        }

    }
}
