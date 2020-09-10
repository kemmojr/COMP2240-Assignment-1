import java.util.*;

public class Algorithms {

    private int numOfProcesses, dispatchTime;
    private ArrayList<SchedulerProcess> processes, FCFSProcessed, SPNProcessed, PPProcessed, PRRProcessed;
    private ArrayList<SchedulerProcess> sortedReadyQueue;

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

    private void updateReadyQueue(ArrayList<SchedulerProcess> temp, Queue<SchedulerProcess> readyQueue,int time){
        int numToRemove = 0;
        for (int i = 0; i < temp.size(); i++) {//Iterate through all the elements of temp and and add all of the elements that have a start time that matches current time to the ready queue
            if (temp.get(i).getArrive()<=time){
                readyQueue.add(temp.get(i));
                numToRemove++;
            }
        }
        for (int i = numToRemove-1; i > -1; i--) {//Remove all of the SchedulerProcesses from the temp list that were added to the readyQueue
            temp.remove(i);
        }
    }

    private void updateReadyQueueSorted(ArrayList<SchedulerProcess> temp, ArrayList<SchedulerProcess> readyQueue,int time){
        ArrayList<SchedulerProcess> copy = new ArrayList<>();
        SchedulerProcess current, adding = null;
        int currentIndex = 0;
        for (int i = 0; i < temp.size(); i++) {//Iterate through all the elements of temp and and add all of the elements that have a start time that matches current time to the ready queue
            if (temp.get(i).getArrive()<=time){
                readyQueue.add(temp.get(i));
            }
        }
        for (int i = 0; i < readyQueue.size(); i++) {
            adding = readyQueue.get(i);
            if (copy.size()==0){
                copy.add(adding);
                continue;
            }
            for (int j = 0; j < copy.size(); j++) {
                if (j==copy.size()-1 && copy.size()==1){//if there is only one element in copy
                    if (copy.get(j).comparePriority(adding)<0){//if the element is less than adding then append adding
                        copy.add(adding);
                    } else {//otherwise add adding to the start of the ArrayList
                        copy.add(j,adding);
                    }
                    break;
                } else if (copy.get(j).comparePriority(adding)==0){//If both elements match add adding at the current index
                    copy.add(j,adding);
                    break;
                } else if (copy.get(j).comparePriority(adding)>0){//if adding is less than current element then add before current element
                    copy.add(j,adding);
                    break;
                } else if (copy.get(j).comparePriority(adding)<0 && j ==copy.size()-1){//Otherwise if adding is greater than current element and we have reached the end of the ArrayList append adding
                    copy.add(adding);
                    break;
                }
            }
        }
        sortedReadyQueue = copy;
    }

    public int getHighestPriority(ArrayList<SchedulerProcess> sorted){
        return sorted.get(0).getPriority();
    }

    public void FCFS(){//Execute the first come first served algorithm
        ArrayList<SchedulerProcess> temp = new ArrayList<>(processes);
        Queue<SchedulerProcess> readyQueue = new LinkedList<>();
        SchedulerProcess processing = null;//Current item that is being processed
        int numToRemove =0, processingTimeRemaining =0, time = 0;
        boolean allItemsExecuted = false;
        while (!allItemsExecuted){
            updateReadyQueue(temp,readyQueue,time);
            if (processingTimeRemaining==0){
                if (processing!=null){
                    processing.setTurnAroundTime(-(processing.getArrive()-time));
                    FCFSProcessed.add(processing);
                }
                if (readyQueue.size()>0){//Add the turnaroundTime to the processing SchedulerProcess
                    time += dispatchTime;
                    processing = new SchedulerProcess(readyQueue.poll());
                    processing.setWaitingTime(-(processing.getArrive()-time));
                    processingTimeRemaining = processing.getExecSize();
                } else if (temp.isEmpty()){
                    allItemsExecuted = true;
                } else {
                    processing = null;
                }

            }

            time++;
            processingTimeRemaining--;


        }

    }

    public void SPN(){//Execute the shortest process next algorithm
        ArrayList<SchedulerProcess> temp = new ArrayList<>(processes);
        Queue<SchedulerProcess> readyQueue = new LinkedList<>(), tempQueue;
        SchedulerProcess processing = null, possibleNextProcess, shortestNextProcess = null;//Current item that is being processed = processing
        //temporary process to compare runtime = possibleNextProcess
        //shortest process that has been found in the readyQueue = shortestNextProcess
        int processingTimeRemaining =0, time = 0, processRuntime = -1;
        boolean allItemsExecuted = false;
        while (!allItemsExecuted){
            updateReadyQueue(temp,readyQueue,time);
            if (processingTimeRemaining==0){
                if (processing!=null){
                    processing.setTurnAroundTime(-(processing.getArrive()-time));
                    SPNProcessed.add(processing);
                    processRuntime = -1;
                }
                if (readyQueue.size()>0){
                    time += dispatchTime;
                    tempQueue = new LinkedList<>(readyQueue);
                    for (int i = 0; i < readyQueue.size(); i++) {//loops through the readyQueue to find the shortest process
                        possibleNextProcess = tempQueue.poll();
                        if (processRuntime == -1){
                            shortestNextProcess = possibleNextProcess;
                            processRuntime = possibleNextProcess.getExecSize();
                        } else if (possibleNextProcess.getExecSize() < processRuntime){
                            shortestNextProcess = possibleNextProcess;
                            processRuntime = possibleNextProcess.getExecSize();
                        }
                    }
                    processing = shortestNextProcess;
                    readyQueue.remove(processing);
                    processing = new SchedulerProcess(processing);
                    processing.setWaitingTime(-(processing.getArrive()-time));
                    processingTimeRemaining = processing.getExecSize();
                } else if (temp.isEmpty()){
                    allItemsExecuted = true;
                } else {
                    processing = null;
                }

            }

            time++;
            processingTimeRemaining--;


        }
    }

    public void PP() {
        ArrayList<SchedulerProcess> temp = new ArrayList<>(processes);
        sortedReadyQueue = new ArrayList<>();
        ArrayList<SchedulerProcess> readyQueue = new ArrayList<>();
        SchedulerProcess processing = null;//Current item that is being processed = processing
        int processingTimeRemaining = 0, time = 0, processRuntime = -1, highestPriority, readyQueueSize;
        boolean allItemsExecuted = false;

        while (!allItemsExecuted) {
            updateReadyQueueSorted(temp, readyQueue, time);
            highestPriority = getHighestPriority(sortedReadyQueue);
            if (processing!=null && highestPriority<processing.getPriority()){//If the highest priority in the readyQueue is higher than processing then swap processing
                if (processingTimeRemaining!=0){
                    processing.setExecSize(processing.getExecSize()-(-(processing.getArrive() - time)));
                    //Add the process back to the readyQueue in the sorted position
                    processing = sortedReadyQueue.get(0);
                    sortedReadyQueue.remove(processing);
                    processing = new SchedulerProcess(processing);
                    processing.setWaitingTime(-(processing.getArrive() - time));
                    processingTimeRemaining = processing.getExecSize();
                }


            }


            if (processingTimeRemaining == 0) {
                if (processing != null) {
                    processing.setTurnAroundTime(-(processing.getArrive() - time));
                    SPNProcessed.add(processing);
                    processRuntime = -1;
                }
                if (readyQueue.size() > 0) {
                    time += dispatchTime;

                    //processing = shortestNextProcess;
                    processing = sortedReadyQueue.get(0);
                    sortedReadyQueue.remove(processing);
                    processing = new SchedulerProcess(processing);
                    processing.setWaitingTime(-(processing.getArrive() - time));
                    processingTimeRemaining = processing.getExecSize();
                } else if (temp.isEmpty()) {
                    allItemsExecuted = true;
                } else {
                    processing = null;
                }
                time++;
                processingTimeRemaining--;
            }


        }
    }

    public void getOutput(){
        int counter = 0;
        System.out.println("FCFS:");
        for (int i = 0; i < FCFSProcessed.size(); i++) {
            System.out.println("T" + FCFSProcessed.get(i).getWaitingTime() + ": " + FCFSProcessed.get(i).getID() + "(" + FCFSProcessed.get(i).getPriority() + ")");
        }
        System.out.println("Process\tTurnaround Time\tWaiting Time");
        for (int i = 0; i < FCFSProcessed.size(); i++) {
            System.out.println(FCFSProcessed.get(i).getID() + "\t\t" + FCFSProcessed.get(i).getTurnAroundTime() + "\t\t\t" + FCFSProcessed.get(i).getWaitingTime());
        }

        System.out.println("SPN:");
        for (int i = 0; i < SPNProcessed.size(); i++) {
            System.out.println("T" + SPNProcessed.get(i).getWaitingTime() + ": " + SPNProcessed.get(i).getID() + "(" + SPNProcessed.get(i).getPriority() + ")");
        }
        System.out.println("Process\tTurnaround Time\tWaiting Time");

        while (counter<processes.size()){

            for (int i = 0; i < SPNProcessed.size(); i++) {
                if (processes.get(counter).getID().equalsIgnoreCase(SPNProcessed.get(i).getID())){
                    System.out.println(SPNProcessed.get(i).getID() + "\t\t" + SPNProcessed.get(i).getTurnAroundTime() + "\t\t\t" + SPNProcessed.get(i).getWaitingTime());
                }
            }
            counter++;
        }
        //Output the rest of the algorithms metrics
    }
}


