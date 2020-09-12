import java.util.*;

public class Algorithms {

    private int dispatchTime, quantumTimeHPC, quantumTimeLPC;
    private ArrayList<SchedulerProcess> processes, FCFSProcessed, SPNProcessed, PPProcessed, PPThreads, PRRProcessed, PRRThreads;
    private ArrayList<SchedulerProcess> sortedReadyQueue;

    public Algorithms(Scanner inputReader){
        //Go through the input file and convert all the process data into process classes
        FCFSProcessed = new ArrayList<>();
        SPNProcessed = new ArrayList<>();
        PPProcessed = new ArrayList<>();
        PPThreads = new ArrayList<>();
        PRRProcessed = new ArrayList<>();
        PRRThreads = new ArrayList<>();
        sortedReadyQueue = new ArrayList<>();
        String current = "";
        quantumTimeHPC = 4;//CHANGE THIS VALUE TO ALTER THE QUANTUM TIME OF PRR
        quantumTimeLPC = 2;//CHANGE THIS VALUE TO ALTER THE QUANTUM TIME OF PRR
        processes = new ArrayList<>();
        while (!current.equalsIgnoreCase("EOF")){//Scan the whole file
            current = inputReader.next();
            if (current.equalsIgnoreCase("DISP:")){//get the dispatch time
                current = inputReader.next();
                dispatchTime = Integer.parseInt(current);
            }

            if (current.equalsIgnoreCase("ID:")){//get the ID
                SchedulerProcess p = new SchedulerProcess();
                p.setID(inputReader.next());
                while (!current.equalsIgnoreCase("END")){//continue to read until we reach the end of the process
                    if (current.equalsIgnoreCase("Arrive:")){//set arrive
                        p.setArrive(inputReader.nextInt());
                    } else if (current.equalsIgnoreCase("ExecSize:")){//set execution size
                        int exec = inputReader.nextInt();
                        p.setExecSize(exec);
                        p.setInitialExecSize(exec);
                    } else if (current.equalsIgnoreCase("Priority:")){//set priority
                        p.setPriority(inputReader.nextInt());
                    }
                    current = inputReader.next();
                }
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

    public boolean insertSorted(int j, ArrayList<SchedulerProcess> list, SchedulerProcess inserting){//insert a SchedulerProcess in it's correct position in an ArrayList
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

    private void updateReadyQueueSorted(ArrayList<SchedulerProcess> temp, ArrayList<SchedulerProcess> readyQueue,int time){//update the ready queue with elements that are sorted by priority
        ArrayList<SchedulerProcess> copy = new ArrayList<>();
        SchedulerProcess current, adding = null;
        int numToRemove = 0;
        updateReadyQueue(temp,readyQueue,time);

        for (int i = 0; i < readyQueue.size(); i++) {//iterate through each element of the readyQueue and add it to it's correct position in a sorted ready queue
            adding = readyQueue.get(i);
            adding.setHPC();
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

    public void addProcessBackPP(SchedulerProcess processing){//Adds a process that was executing back into the readyQueue
        int queueSize = sortedReadyQueue.size();
        for (int j = 0; j < queueSize; j++) {
            insertSorted(j, sortedReadyQueue,processing);
        }
    }

    public void addProcessBack(SchedulerProcess s, ArrayList<SchedulerProcess> readyQueue){
        readyQueue.add(s);
    }

    public void FCFS(){//Execute the first come first served algorithm
        ArrayList<SchedulerProcess> temp = new ArrayList<>(processes);//A temporary list of all of the processes
        ArrayList<SchedulerProcess> readyQueue = new ArrayList<>();//A readyQueue as an arrayList (this helps for comparisons with insertionSort)
        SchedulerProcess processing = null;//Current item that is being processed
        int numToRemove =0, processingTimeRemaining =0, time = 0;
        boolean allItemsExecuted = false;
        while (!allItemsExecuted){
            updateReadyQueue(temp,readyQueue,time);//update the readyQueue with all of the processes that start at the current time
            if (processingTimeRemaining==0){//when the last process has reached the end of its runtime
                if (processing!=null){//If the last process is finishing it's processing time i.e. something was just processing and the scheduler isn't starving
                    processing.setTurnAroundTime(-(processing.getArrive()-time));
                    FCFSProcessed.add(processing);//Add finished process to the finished process stats
                }

                if (readyQueue.size()>0){
                    time += dispatchTime;//factor in the time required to run the dispatcher
                    processing = new SchedulerProcess(readyQueue.get(0));//get next item in the queue to begin processing
                    readyQueue.remove(0);
                    processing.setWaitingTime(-(processing.getArrive()-time));//Set the waiting time to be recorded
                    processingTimeRemaining = processing.getExecSize();//set how long the process has remaining

                } else if (temp.isEmpty()){//if there are no more processes then finish
                    allItemsExecuted = true;
                } else {//if starving then set processing to null
                    processing = null;
                }
            }
            time++;
            processingTimeRemaining--;
        }
    }

    public void SPN(){//Execute the shortest process next algorithm
        ArrayList<SchedulerProcess> temp = new ArrayList<>(processes);//A temporary list of all of the processes
        ArrayList<SchedulerProcess> readyQueue = new ArrayList<>(), tempQueue;//A readyQueue as an arrayList (this helps for comparisons with insertionSort)
        SchedulerProcess processing = null, possibleNextProcess, shortestNextProcess = null;//Current item that is being processed = processing
        //temporary process to compare runtime = possibleNextProcess
        //shortest process that has been found in the readyQueue = shortestNextProcess
        int processingTimeRemaining =0, time = 0, processRuntime = -1;
        boolean allItemsExecuted = false;
        while (!allItemsExecuted){
            updateReadyQueue(temp,readyQueue,time);//update the readyQueue with all of the processes that start at the current time
            if (processingTimeRemaining==0){//when the last process has reached the end of its runtime
                if (processing!=null){//If the last process is finishing it's processing time i.e. something was just processing and the scheduler isn't starving
                    processing.setTurnAroundTime(-(processing.getArrive()-time));//turnAroundTime tracking
                    SPNProcessed.add(processing);//Storing the metrics
                    processRuntime = -1;
                }

                if (readyQueue.size()>0){
                    time += dispatchTime;//factor in the time required to run the dispatcher
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
                    processing = shortestNextProcess;//start the process with the shortest runtime
                    readyQueue.remove(processing);
                    processing = new SchedulerProcess(processing);
                    processing.setWaitingTime(-(processing.getArrive()-time));//metric tracking for waiting time
                    processingTimeRemaining = processing.getExecSize();//set how long the process has remaining

                } else if (temp.isEmpty()){//exit if there are no more processes to be run
                    allItemsExecuted = true;
                } else {
                    processing = null;
                }
            }
            time++;
            processingTimeRemaining--;
        }
    }

    public void PP() {//Execute the pre-emptive priority algorithm
        ArrayList<SchedulerProcess> temp = new ArrayList<>(processes);
        ArrayList<SchedulerProcess> readyQueue = new ArrayList<>();
        SchedulerProcess processing = null;//Current item that is being processed = processing
        int processingTimeRemaining = 0, time = 0, highestPriority = 6;//tracking for what the highest priority process is in the queue
        boolean allItemsExecuted = false;

        while (!allItemsExecuted) {
            if (temp.size()>0)
                updateReadyQueueSorted(temp, readyQueue, time);
            if (sortedReadyQueue.size()>0)
                highestPriority = sortedReadyQueue.get(0).getPriority();

            if (processing!=null && highestPriority<processing.getPriority()){//If the highest priority in the readyQueue is higher than processing then swap processing
                if (processingTimeRemaining!=0){
                    processing.setExecSize(processing.getExecSize()-(-(processing.getArrive() - time)));//decrease execution time by the amount executed
                    processing = new SchedulerProcess(processing);
                    PPThreads.add(processing);
                    addProcessBackPP(processing);
                    processing = sortedReadyQueue.get(0);
                    sortedReadyQueue.remove(processing);
                    processing = new SchedulerProcess(processing);
                    processing.setWaitingTime(-(processing.getArrive() - time));//Sets the waiting time. If it has already waited it += wait time
                    processingTimeRemaining = processing.getExecSize();
                }
            }

            if (processingTimeRemaining == 0) {
                if (processing != null) {//If the last process is finishing it's processing time i.e. something was just processing and the scheduler isn't starving
                    processing.setTurnAroundTime(-(processing.getArrive() - time));
                    PPProcessed.add(processing);//metric tracking
                    PPThreads.add(processing);
                }
                if (sortedReadyQueue.size() > 0) {
                    time += dispatchTime;//factor in the time required to run the dispatcher
                    processing = sortedReadyQueue.get(0);//get the next process with the highest priority from the readyQueue
                    sortedReadyQueue.remove(processing);
                    processing = new SchedulerProcess(processing);
                    if (processing.getWaitingTime() <= 0) {
                        processing.setWaitingTime(-(processing.getArrive() - time));
                    }
                    processingTimeRemaining = processing.getExecSize();//set how long this process has to go
                } else if (temp.isEmpty()) {
                    allItemsExecuted = true;
                } else {
                    processing = null;
                }
            }
            time++;
            if (processing != null)
                processingTimeRemaining--;
        }
    }

    public void PRR(){
        ArrayList<SchedulerProcess> temp = new ArrayList<>(processes);
        ArrayList<SchedulerProcess> readyQueue = new ArrayList<>();
        SchedulerProcess processing = null;//Current item that is being processed = processing
        int quantumTimeRemaining = 0, processingTimeRemaining = 0, time = 0;//tracking for what the highest priority process is in the queue
        boolean allItemsExecuted = false;
        while (!allItemsExecuted){
            if (temp.size()>0)
                updateReadyQueue(temp, readyQueue, time);

            if (processingTimeRemaining == 0) {
                if (processing != null) {//If the last process is finishing it's processing time i.e. something was just processing and the scheduler isn't starving
                    processing.setTurnAroundTime(-(processing.getArrive() - time));
                    processing.setWaitingTime(processing.getTurnAroundTime()-processing.getInitialExecSize());
                    PRRProcessed.add(processing);//metric tracking
                    PRRThreads.add(processing);

                }
                if (readyQueue.size() > 0) {
                    time += dispatchTime;//factor in the time required to run the dispatcher
                    processing = readyQueue.get(0);//get the next process with the highest priority from the readyQueue
                    readyQueue.remove(processing);
                    processing = new SchedulerProcess(processing);
                    processingTimeRemaining = processing.getExecSize();//set how long this process has to go
                    if (processing.isHPC()){
                        quantumTimeRemaining = quantumTimeHPC;
                    } else {
                        quantumTimeRemaining = quantumTimeLPC;
                    }
                } else if (temp.isEmpty()) {
                    allItemsExecuted = true;
                } else {
                    processing = null;
                }
            }

            if (quantumTimeRemaining ==0){//Pre-emption with quantum time
                if (readyQueue.size()<0 && processingTimeRemaining >0){
                    //continue running the process without running the dispatcher
                    if (processing.isHPC()){
                        quantumTimeRemaining = quantumTimeHPC;
                    } else {
                        quantumTimeRemaining = quantumTimeLPC;
                    }
                } else if (readyQueue.size()>0){
                    time += dispatchTime;//factor in the time required to run the dispatcher
                    if (processing!=null){
                        processing.setExecSize(processing.getExecSize() + processingTimeRemaining-processing.getExecSize());//decrease execution time by the amount executed
                        PRRThreads.add(processing);
                    }
                    processing = new SchedulerProcess(processing);
                    addProcessBack(processing,readyQueue);
                    processing = readyQueue.get(0);
                    readyQueue.remove(processing);
                    processing = new SchedulerProcess(processing);
                    processingTimeRemaining = processing.getExecSize();
                    if (processing.isHPC()){
                        quantumTimeRemaining = quantumTimeHPC;
                    } else {
                        quantumTimeRemaining = quantumTimeLPC;
                    }
                }


            }
            time++;
            quantumTimeRemaining--;
            if (processing != null)
                processingTimeRemaining--;
        }
    }



    public void getOutput(){//display the output formatted as it was in the output files given
        String algName = "";
        ArrayList<SchedulerProcess> output = null, threadsOut = null;
        int processedSize = 0, counter = 0;;
        for (int i = 0; i < 4; i++) {
            switch (i){
                case 0: algName = "FCFS";
                output = FCFSProcessed;
                threadsOut = FCFSProcessed;
                processedSize = output.size();
                counter = 0;
                break;
                case 1: algName = "SPN";
                    output = SPNProcessed;
                    threadsOut = SPNProcessed;
                    processedSize = output.size();
                    counter = 0;
                    break;
                case 2: algName = "PP";
                    output = PPProcessed;
                    threadsOut = PPThreads;
                    processedSize = output.size();
                    counter = 0;
                    break;
                case 3: algName = "PRR";
                    output = PRRProcessed;
                    threadsOut = PRRThreads;
                    processedSize = output.size();
                    counter = 0;
                    break;
            }
            System.out.println("\n"+algName);
            for (int j = 0; j < threadsOut.size(); j++) {
                System.out.println("T" + threadsOut.get(j).getWaitingTime() + ": " + threadsOut.get(j).getID() + "(" + threadsOut.get(j).getPriority() + ")");
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


