package bankeralgorithm;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class BankerAlgorithm {

    int process;
    int No_resources;
    int[] Available;
    int[][] Maximum;
    int[][] Allocate;
    int[][] Need;
    boolean safeState = false;
    int[] Sim_Available;
    int[] Temp;
    boolean[] Finish;

    public static int RandInt(int min, int max) {
        Random rand = new Random();
        int RandNum = rand.nextInt((max - min) + 1) + min;
        return RandNum;
    }

    public int[] getAvailable() {
        return Available;
    }

    public void SetAvailable(int[] av) {
        for (int i = 0; i < No_resources; i++) {
            Available[i] = av[i];
        }
    }

    public int[] getAllocate(int index) {
        int[] all = new int[No_resources];
        for (int i = 0; i < No_resources; i++) {
            all[i] = Allocate[index][i];
        }
        return all;
    }

    public void SetAllocate(int index) {
        for (int i = 0; i < No_resources; i++) {
            Allocate[index][i] = 0;
        }

    }
    
    public void setAllocate (int index , int[]all)
    {
       for (int i = 0; i < No_resources; i++) {
            Allocate[index][i] = all[i];
        }
    }

    public int[] getNeed(int num) {
        int[] need = new int[No_resources];
        for (int i = 0; i < No_resources; i++) {
            need[i] = Need[num][i];
        }
        return need;
    }

    public BankerAlgorithm(int[] rs, int proc) {

        process = proc;
        No_resources = rs.length;
        Available = new int[No_resources];
        System.arraycopy(rs, 0, Available, 0, No_resources);
        Maximum = new int[process][No_resources];
        Allocate = new int[process][No_resources];
        Need = new int[process][No_resources];
        Sim_Available = new int[No_resources];
        Temp = new int[No_resources];
        Finish = new boolean[process];
        for (int i = 0; i < process; i++) {
            Finish[i] = false;
        }
    }

    public void CreateProcess(int NoProcess, int[] Max, int[] Allocation) {
        Maximum[NoProcess] = new int[No_resources];
        Allocate[NoProcess] = new int[No_resources];
        Need[NoProcess] = new int[No_resources];

        //Filling the arrays 
        System.arraycopy(Max, 0, Maximum[NoProcess], 0, Max.length);
        System.arraycopy(Allocation, 0, Allocate[NoProcess], 0, Max.length);
        for (int i = 0; i < No_resources; i++) {
            Need[NoProcess][i] = Maximum[NoProcess][i] - Allocate[NoProcess][i];
        }
    }

    public boolean SimulationCheck(int[][] Allocation, int[] Avail, int[][] Max, int[][] Need) {

        boolean State = false;

         System.out.print("Available = [ ");
        for (int i = 0; i < No_resources - 1; i++) {
            System.out.print(Avail[i] + " ");
        }
        System.out.println(Avail[No_resources - 1] + " ]");
         
        System.arraycopy(Avail, 0, Sim_Available, 0, No_resources);

        for (int i = 0; i < process; i++) {

            if (Finish[i] == false) {

                safeState = false;
                int NeedCount = 0;

                for (int j = 0; j < No_resources; j++) {
                    if (Need[i][j] <= Sim_Available[j]) {
                        NeedCount++;
                    }
                }
                // if need less than available
                if (NeedCount == No_resources) {
                    for (int j = 0; j < No_resources; j++) {
                        Sim_Available[j] = Sim_Available[j] + Allocation[i][j];
                    }
                    Finish[i] = true;
                }
            }

            if (i == 0) {
                System.arraycopy(Sim_Available, 0, Temp, 0, No_resources);
            }

            if (i == process - 1) {
                int counter = 0;
                for (int j = 0; j < No_resources; j++) {
                    if (Temp[j] == Sim_Available[j]) {
                        counter++;
                    }
                }

                if (counter != No_resources) {
                    i = -1;
                } else {
                    int FinishCount = 0;
                    for (int j = 0; j < process; j++) {
                        if (Finish[j] == true) {
                            FinishCount++;
                        }
                    }

                    if (FinishCount == process) {
                        safeState = true;
                    }
                }
            }
        }
        if (safeState) {
            System.out.println("Safe state, There is no deadlock");
            State = true;
        } else {
            System.out.println("Not safe state, There is deadlock");
        }
        System.out.println("");

        return State;
    }

    public boolean CheckReq(int ProcessNo, int[] request) {

        int[][] AllocateReq = new int[process][No_resources];
        int[][] NeedReq = new int[process][No_resources];
        int[] AvailableReq = new int[No_resources];
        //for function simulation check
        int[][] SimAllocation = new int[process][No_resources];
        int[][] SimMax = new int[process][No_resources];
        int[][] SimNeed = new int[process][No_resources];
        boolean check = false;
        boolean bool = false;
        int Needcounter = 0;
        int Availcounter = 0;

        //check if request less than need
        for (int i = 0; i < No_resources; i++) {
            if (request[i] <= Need[ProcessNo][i]) {
                Needcounter++;
            }
        }
        //check if request less than available
        for (int j = 0; j < No_resources; j++) {
            if (request[j] <= Available[j]) {
                Availcounter++;
            }
        }
        
        // if request greater than the available
        if (Availcounter < No_resources) {
            System.out.println("Request denied");
            System.out.println(" ");
            return bool;
        }

        if (Needcounter == No_resources && Availcounter == No_resources) {

            //Copying the real arrays to request arrays
            System.arraycopy(Available, 0, AvailableReq, 0, No_resources);

            for (int k = 0; k < Allocate.length; k++) {
                System.arraycopy(Allocate[k], 0, AllocateReq[k], 0, Allocate[k].length);
            }

            for (int k = 0; k < Need.length; k++) {
                System.arraycopy(Need[k], 0, NeedReq[k], 0, Need[k].length);
            }

            for (int i = 0; i < No_resources; i++) {

                AllocateReq[ProcessNo][i] = AllocateReq[ProcessNo][i] + request[i];
                NeedReq[ProcessNo][i] = NeedReq[ProcessNo][i] - request[i];
                AvailableReq[i] = AvailableReq[i] - request[i];
            }

            for (int k = 0; k < Maximum.length; k++) {
                System.arraycopy(Maximum[k], 0, SimMax[k], 0, Maximum[k].length);
            }
            for (int j = 0; j < Allocate.length; j++) {
                System.arraycopy(Allocate[j], 0, SimAllocation[j], 0, Allocate[j].length);
            }
            for (int j = 0; j < Need.length; j++) {
                System.arraycopy(Need[j], 0, SimNeed[j], 0, Need[j].length);
            }

            for (int j = 0; j < No_resources; j++) {

                //Updating the 2D arrays for the simulation check
                SimAllocation[ProcessNo][j] = AllocateReq[ProcessNo][j];
                SimNeed[ProcessNo][j] = NeedReq[ProcessNo][j];
                SimMax[ProcessNo][j] = Maximum[ProcessNo][j];
            }

            boolean SafeCheck = true;
            SafeCheck = SimulationCheck(SimAllocation, AvailableReq, SimMax, SimNeed);

            //Changing the real values if the request is accepted
            if (SafeCheck) {

                for (int i = 0; i < Available.length; i++) {
                    Available[i] = Available[i] - request[i];
                }
                for (int j = 0; j < SimAllocation.length; j++) {
                    System.arraycopy(SimAllocation[j], 0, Allocate[j], 0, SimAllocation[j].length);
                }
                for (int j = 0; j < SimNeed.length; j++) {
                    System.arraycopy(SimNeed[j], 0, Need[j], 0, SimNeed[j].length);
                }
                for (int j = 0; j < SimMax.length; j++) {
                    System.arraycopy(SimMax[j], 0, Maximum[j], 0, SimMax[j].length);
                }

            }
            // Not a safe state
            else {
                System.out.println("Request Denied");
                System.out.println(" ");
            }
        } else {
            System.out.println("Request greater than need, Request denied");
            System.out.println(" ");
            check = false;
        }
        return check;
    }

    public static void main(String[] args) {
        int No_Process;
        int Resources;
        //Counter for finished processes
        int cont = 0 ;
        Random rand = new Random();
        //boolean for release
        boolean rel;
        Scanner sc = new Scanner(System.in);
        
        System.out.println("Please Enter Number Of Processes Needed: ");
        No_Process = sc.nextInt();
        System.out.println("Please Enter Number Of Resources Needed: ");
        Resources = sc.nextInt();

        int[] No_instances = new int[Resources];
        int[] available = new int[Resources];
        int[] allocation = new int[Resources];
        int[] relAvailable = new int[Resources];
        int[] Finish_process = new int[No_Process] ;
        
        System.out.println("Enter No. of instance: ");
        for (int i = 0; i < Resources; i++) {
            No_instances[i] = sc.nextInt();
        }
        System.out.println(" ");
        System.arraycopy(No_instances, 0, available, 0, No_instances.length);

        int[][] Max = new int[No_Process][Resources];
        int[][] allocate = new int[No_Process][Resources];
        
        //Need it when need of processes = 0
        int av[] = new int[Resources];
        int all[] = new int[Resources];
        
        //generate maximum randomly
        for (int i = 0; i < No_Process; i++) {
            for (int j = 0; j < Resources; j++) {
                Max[i][j] = RandInt(0, available[j]);
            }
        }

        //printing the maximum 
        System.out.print("Maximum =  ");
        for (int i = 0; i < No_Process; i++) {
            System.out.print("[ ");
            for (int j = 0; j < Resources - 1; j++) {
                System.out.print(Max[i][j] + " ");
            }
            System.out.print(Max[i][Resources - 1] + " ]");
        }
        System.out.println(" ");
        
        //Filling the allocation array with zeros
        for (int i = 0; i < No_Process; i++) {
            for (int j = 0; j < Resources; j++) {
                allocate[i][j] = 0;
            }
        }

        System.out.print("Allocation = ");
        for (int i = 0; i < No_Process; i++) {
            System.out.print("[ ");
            for (int j = 0; j < Resources - 1; j++) {
                System.out.print(allocate[i][j] + " ");
            }
            System.out.print(allocate[i][Resources - 1] + " ]");
        }
        System.out.println(" ");
        System.out.println(" ");

        //creating object from class bankerAlgorithm
        BankerAlgorithm bank = new BankerAlgorithm(available, No_Process);
        for (int i = 0; i < No_Process; i++) {
            bank.CreateProcess(i, Max[i], allocate[i]);
        }

        boolean check = false;
        boolean[] finish = new boolean[No_Process];
        Arrays.fill(finish, Boolean.FALSE);

        while (!check) {
            //for request
            int ProcessNum = RandInt(0, No_Process - 1);
            //for release
            int processNumber = RandInt(0 , No_Process - 1);
            rel = rand.nextBoolean();
            
            if (!finish[ProcessNum]) {

                int need[] = bank.getNeed(ProcessNum);
                int request[] = new int[Resources];
                int release[] = new int [Resources];
                
                //Generating a request
                for (int i = 0; i < Resources; i++) {
                    request[i] = RandInt(0, need[i]+1);
                }
                System.out.print("The request of proceess[" + ProcessNum + "]" + "=" + "[ ");
                for (int i = 0; i < request.length; i++) {
                    System.out.print(request[i] + " ");
                }
                System.out.println(" ] ");
                
                
                check = bank.CheckReq(ProcessNum, request);
                allocation = bank.getAllocate(processNumber);
                
                if (rel) {     
                    //generating release randomly
                    for (int i = 0; i < Resources; i++) {
                        release[i] = RandInt(0, allocation[i]);
                    }
                    int CounteR = 0;
                    //if release = 0
                    for (int i = 0; i < Resources; i++) {
                        if (release[i] == 0) {
                            CounteR++;
                        }
                    }
                    if (CounteR != Resources) {
                        relAvailable = bank.getAvailable();

                        System.out.print("The release of proceess[" + processNumber + "]" + "=" + "[ ");
                        for (int i = 0; i < release.length; i++) {
                            System.out.print(release[i] + " ");
                        }
                        System.out.println(" ] ");

                        for (int i = 0; i < Resources; i++) {
                            allocation[i] = allocation[i] - release[i];
                            relAvailable[i] = relAvailable[i] + release[i];
                        }
                        // setting the real allocate and the real available
                        bank.setAllocate(processNumber, allocation);
                        bank.SetAvailable(relAvailable);

                        System.out.print("Available after Release = [");
                        for (int i = 0; i < Resources; i++) {
                            System.out.print(relAvailable[i] + " ");
                        }
                        System.out.println(" ]");
                        System.out.println(" ");
                    }
                }
                
                int need2[] = bank.getNeed(ProcessNum);
                int Counter = 0;
                for (int i = 0; i < Resources; i++) {
                    Counter = Counter + need2[i];
                }
                
                if (Counter == 0) {
                    finish[ProcessNum] = true; 
                    //array saves the process that has finished
                    Finish_process[cont]=ProcessNum;
                    cont++;
                    
                    all = bank.getAllocate(ProcessNum);
                    av = bank.getAvailable();
                    // returning the allocation of process to avialable
                    for (int i = 0; i < Resources; i++) {
                        av[i] = av[i] + all[i];
                    }
                    bank.SetAvailable(av);
                    // setting allocation of process to zero
                    bank.SetAllocate(ProcessNum);
                }
                int count = 0;
                for (int i = 0; i < finish.length; i++) {
                    if (finish[i] == true) {
                        count++;
                    }
                }
                // if all process has finished (need of each process = 0)
                if (count == No_Process) {
                    check = true;
                }
            }
        }
        System.out.println("System breaks");
        System.out.print("Processes:{");
        for(int i = 0 ; i< No_Process-1 ; i++)
        {
          System.out.print("p"+Finish_process[i]+", ");
        }
        System.out.println("p"+Finish_process[No_Process-1]+"}");
    }

}
