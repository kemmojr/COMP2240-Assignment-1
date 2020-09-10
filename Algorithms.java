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

    private void updateReadyQueue(ArrayList<SchedulerProcess> temp, ArrayList<SchedulerProcess> readyQueue,int time){
        int numToRemove = 0;
        for (SchedulerProcess schedulerProcess : temp) {//Iterate through all the elements of temp and and add all of the elements that have a start time that matches current time to the ready queue
            if (schedulerProcess.getArrive() <= time) {
                readyQueue.add(schedulerProcess);
                numToRemove++;
            }
        }
        for (int i = numToRemove-1; i > -1; i--) //Remove all of the SchedulerProcesses from the temp list that were added to the readyQueue
            temp.remove(i);
    }

    public boolean insertSorted(int j, ArrayList<SchedulerProcess> list, SchedulerProcess inserting){
        if (j==list.size()-1 && list.size()==1){//if there is only one element in copy
            if (list.get(j).comparePriority(inserting)<0){//if the element is less than inserting then append inserting
                list.add(inserting);
            } else {//otherwise add inserting to the start of the ArrayList
                list.add(j,inserting);
            }
            return true;
        } else if (list.get(j).comparePriority(inserting)==0){//If both elements match add inserting at the current index
            list.add(j,inserting);
            return true;
        } else if (list.get(j).comparePriority(inserting)>0){//if inserting is less than current element then add before current element
            list.add(j,inserting);
            return true;
        } else if (list.get(j).comparePriority(inserting)<0 && j ==list.size()-1){//Otherwise if inserting is greater than current element and we have reached the end of the ArrayList append inserting
            list.add(inserting);
            return true;
        }
        return false;
    }

    private void updateReadyQueueSorted(ArrayList<SchedulerProcess> temp, ArrayList<SchedulerProcess> readyQueue,int time){
        ArrayList<SchedulerProcess> copy = new ArrayList<>();
        SchedulerProcess current, adding = null;
        int numToRemove = 0;
        updateReadyQueue(temp,readyQueue,time);

        for (int i = 0; i < readyQueue.size(); i++) {
            adding = readyQueue.get(i);
            if (copy.size()==0){
                copy.add(adding);
                continue;
            } else if (copy.size()==3){
                int k = 0;
            }
            int copySize = copy.size();
            for (int j = 0; j < copySize; j++) {
                if (insertSorted(j,copy,adding))
                    j = copySize;
            }
        }
        sortedReadyQueue = copy;
    }

    public void addProcessBackPP(SchedulerProcess processing){
        int queueSize = sortedReadyQueue.size();
        for (int j = 0; j < queueSize; j++) {
            insertSorted(j, sortedReadyQueue,processing);
        }
    }

    public int getHighestPriority(ArrayList<SchedulerProcess> sorted){
        return sorted.get(0).getPriority();
    }

    public void FCFS(){//Execute the first come first served algorithm
        ArrayList<SchedulerProcess> temp = new ArrayList<>(processes);
        ArrayList<SchedulerProcess> readyQueue = new ArrayList<>();
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
                    processing = new SchedulerProcess(readyQueue.get(0));
                    readyQueue.remove(0);
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
        ArrayList<SchedulerProcess> readyQueue = new ArrayList<>(), tempQueue;
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
                    tempQueue = new ArrayList<>(readyQueue);
                    for (int i = 0; i < readyQueue.size(); i++) {//loops through the readyQueue to find the shortest process
                        possibleNextProcess = tempQueue.get(0);
                        tempQueue.remove(0);
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
        int processingTimeRemaining = 0, time = 0, processRuntime = -1, highestPriority = 6, readyQueueSize;
        boolean allItemsExecuted = false;

        while (!allItemsExecuted) {
            if (temp.size()>0)
                updateReadyQueueSorted(temp, readyQueue, time);
            if (sortedReadyQueue.size()>0)
                highestPriority = getHighestPriority(sortedReadyQueue);

            if (processing!=null && highestPriority<processing.getPriority()){//If the highest priority in the readyQueue is higher than processing then swap processing
                if (processingTimeRemaining!=0){
                    processing.setExecSize(processing.getExecSize()-(-(processing.getArrive() - time)));
                    //Add the process back to the readyQueue in the sorted position
                    addProcessBackPP(processing);
                    processing = sortedReadyQueue.get(0);
                    sortedReadyQueue.remove(processing);
                    processing = new SchedulerProcess(processing);
                    if (processing.getWaitingTime() <= 0) {
                        processing.setWaitingTime(-(processing.getArrive() - time));
                    }
                    processingTimeRemaining = processing.getExecSize();
                }


            }


            if (processingTimeRemaining == 0) {
                if (processing != null) {
                    processing.setTurnAroundTime(-(processing.getArrive() - time));
                    PPProcessed.add(processing);
                }
                if (sortedReadyQueue.size() > 0) {
                    time += dispatchTime;
                    processing = sortedReadyQueue.get(0);
                    sortedReadyQueue.remove(processing);
                    processing = new SchedulerProcess(processing);
                    if (processing.getWaitingTime() <= 0) {
                        processing.setWaitingTime(-(processing.getArrive() - time));
                    }
                    processingTimeRemaining = processing.getExecSize();
                } else if (temp.isEmpty()) {
                    allItemsExecuted = true;
                } else {
                    processing = null;
                }
            }
            time++;
            if (processing != null) {
                processingTimeRemaining--;
            }


        }
    }



    public void getOutput(){
        String algName = "";
        ArrayList<SchedulerProcess> output = null;
        int processedSize = 0, counter = 0;;
        for (int i = 0; i < 4; i++) {
            switch (i){
                case 0: algName = "FCFS";
                output = FCFSProcessed;
                processedSize = output.size();
                counter = 0;
                break;
                case 1: algName = "SPN";
                    output = SPNProcessed;
                    processedSize = output.size();
                    counter = 0;
                    break;
                case 2: algName = "PP";
                    output = PPProcessed;
                    processedSize = output.size();
                    counter = 0;
                    break;
                case 3: algName = "PRR";
                    output = PRRProcessed;
                    processedSize = output.size();
                    counter = 0;
                    break;
            }
            System.out.println("\n"+algName);
            for (int j = 0; j < processedSize; j++) {
                System.out.println("T" + output.get(j).getWaitingTime() + ": " + output.get(j).getID() + "(" + output.get(j).getPriority() + ")");
            }
            System.out.println("Process\tTurnaround Time\tWaiting Time");

            while (counter<processes.size()){

                for (int j = 0; j < processedSize; j++) {
                    if (processes.get(counter).getID().equalsIgnoreCase(output.get(j).getID())){
                        System.out.println(output.get(j).getID() + "\t\t" + output.get(j).getTurnAroundTime() + "\t\t\t\t" + output.get(j).getWaitingTime());
                    }
                }
                counter++;
            }

        }
    }
}


