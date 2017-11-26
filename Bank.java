package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Bank {

    private static int numberOfCustomers; //Also relates to the number of threads
    static int numberOfResources; //Number of resource types
    private static int[] available;    //The available amount of each resource
    static int[][] max;    //The maximum demand of each customer
    private static int[][] allocation; //Total Number of Resources being allocated to each thread
    static int Completed = 0;
    private static final Object lock = new Lock();

    /**
     * Function used to initialize the Bank with the specific number of Resources and number of Customers
     * @param R Number representing the different types of resources
     * @param C Number of customers in the Bank
     */
    static void Initialize(int R, int C)
    {
        numberOfResources = R;
        numberOfCustomers = C;
        max = RandomGenerator.RandomMaximumArray(numberOfCustomers,numberOfResources);
        available = RandomGenerator.RandomAvailableVector(numberOfResources, numberOfCustomers);
        allocation = new int[numberOfCustomers][numberOfResources];

        System.out.println("Initial Available Array: ");
        Main.PrintVector(available);
    }


    /**
     * Function Used to request resources. This function is safely used by synchronizing with the various synchronize, lock, and notify functions.
     * @param customerNumber The number that referneces the thread/Customer
     * @param request The resources that are requested
     * @throws InterruptedException Handles the wait and notify interruptions.
     *
     */
    static void requestResources(int customerNumber, int[] request) throws InterruptedException {
        boolean safe = false;
        synchronized (lock)
        {
            System.out.println("Customer " + customerNumber + " Making Request for: ");
            Main.PrintVector(request);
            while (!safe)

            {
                //Step 1: Request <= Need
                for (int i = 0; i < Bank.numberOfResources; i++) {
                    if (request[i] > (Bank.max[customerNumber][i] - Bank.allocation[customerNumber][i])) {
                        return;
                    }
                }
                safe = true;
                //Step 2: Request <= Available
                for (int i = 0; i < Bank.numberOfResources; i++) {
                    if (request[i] > Bank.available[i]) {
                        try {
                            synchronized (lock) {
                                safe = false;
                                System.out.println("Customer" + customerNumber + " must wait until the resources are available");
                                lock.wait(); //Wait until the resources are available
                            }

                        } catch (InterruptedException ignored) {
                        }
                    }
                }

                //Step 3: Pretend it happens and then check if it is safe to do;
                if (Bank.isSafe_AfterThisRequest(request, customerNumber)) //Request is safe
                {
                    safe = true;
                } else //Request must wait
                {
                    try {
                        synchronized (lock) {
                            System.out.println("Customer" + customerNumber + " must wait until the system would be safe");
                            lock.wait(); //Wait until the resources are available
                        }
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            synchronized (lock) {
                //Request was safe, now can change the banks arrays
                Bank.ProcessRequest(request, customerNumber);
                System.out.println("Resource Sent to customer: " + customerNumber);
                System.out.println("New Available Array: ");
                Main.PrintVector(available);

                lock.notify(); //Wait until the resources are available
            }
        }


    }

    /**
     * Function that uses the safety algorithm to determine whether the following request would work.
     * @param request The request in question of whether putting the system into a unsafe state.
     * @param num Customer number
     * @return Used to determine whether function works or not
     */
    private static boolean isSafe_AfterThisRequest(int[] request, int num)
    {

        int index;
        boolean flag;
        int [] need_i;
        List<Integer> seq = new ArrayList<Integer>();
        //Step 1: Initialize Work and Finish
        int[] Work = Subtract_Arrays(available,request);
        int[][] Temp_Allocation;
        Temp_Allocation = new int[allocation.length][];
        for(int i = 0; i < allocation.length; i++)
        {
            int[] aMatrix = allocation[i];
            int   aLength = aMatrix.length;
            Temp_Allocation[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, Temp_Allocation[i], 0, aLength);
        }
        for(int k = 0; k < allocation[num].length; k++)
        {
            Temp_Allocation[num][k] = Temp_Allocation[num][k] + request[k];
        }
        boolean[] Finish = new boolean[numberOfCustomers];
        for(int i = 0; i < numberOfCustomers; i++)
        {
            Finish[i] = false;
        }

        //Step 2: Find an index i such that Finish[i] = false and Need <= Work
        index = 0;
        while(true) {
            need_i = GetRow(Calculate_NeedArray(max,Temp_Allocation),index);
            flag = (!Finish[index]) && (Array1_LTEQ_Array2(need_i, Work));
            while ((!flag && (index < Finish.length - 1))) //There is a process that isn't finished and we haven't made it to the end of the array
            {
                index++;
                need_i = GetRow(Calculate_NeedArray(max,Temp_Allocation),index);
                flag = (!Finish[index]) && (Array1_LTEQ_Array2(need_i, Work));
            }
            if (!flag && (index == Finish.length - 1)) //Got to the end without finding a index STEP 4
            {
                for (int j = 0; j < numberOfCustomers; j++) {
                    if (!Finish[j]) {
                        return false; //There exists a process that can't be finished
                    }
                }
                System.out.println("Safe Customer Sequence: ");
                System.out.print("[ ");
                for(int a = 0; a < seq.size(); a++)
                {
                    System.out.print(seq.get(a) + " ");
                }
                System.out.print("]\n");
                return true; //All Process are finished
            }
            //Else a process is found
            Work = Add_Arrays(Work, GetRow(allocation, index));
            Finish[index] = true;
            seq.add(index);
            //Reset index
            index = 0;
        }

    }

    /**
     * Simply just adds the requested resources to allocation and subtracts from available
     * @param request The requested resources
     * @param num The customer number
     */
    private static void ProcessRequest(int[] request, int num)
    {
        available = Subtract_Arrays(available,request);
        System.out.println("Allocation Matrix After Request from Customer " + num + ": ");
        for(int k = 0; k < allocation[num].length; k++)
        {
            allocation[num][k] = allocation[num][k] + request[k];
        }

        for(int j = 0; j < numberOfCustomers; j++)
        {
            Main.PrintVector(allocation[j]);
        }
    }

    /**
     * Releases the resources from the customer
     * @param customerNumber the number of the customer thread
     */
    static  void releaseResources(int customerNumber) {
        synchronized (lock)
        {
            int[] release = allocation[customerNumber];
            System.out.println("Customer " + customerNumber + " is releasing its resources of");
            System.out.println(Arrays.toString(release));

            for (int i = 0; i < release.length; i++)
            {
                available[i] += release[i];
                max[customerNumber][i] -= release[i];
                allocation[customerNumber][i] = 0;

            }
            System.out.println("New available array:");
            System.out.println(Arrays.toString(available));
            System.out.println("New Allocation Matrix after release from Customer " + customerNumber + ": ");
            for(int j = 0; j < numberOfCustomers; j++)
            {
                System.out.println(Arrays.toString((allocation[j])));
            }

            lock.notify();
        }



    }

    /**
     * Determines if array1 is less than or equal to array2
     * @param array1 An array
     * @param array2 An array
     * @return returns true if array1 <= array2
     */
    private static boolean Array1_LTEQ_Array2(int[] array1, int[] array2) {
        if (array1 != null && array2 != null){
            if (array1.length != array2.length)
                return false;
            else
                for (int i = 0; i < array2.length; i++) {
                    if (array1[i] > array2[i]) {
                        return false;
                    }
                }
        }else{
            return false;
        }
        return true;
    }

    /**
     * Gets the row at value "row" of the Matrix
     * @param Matrix The matrix to retrieve from
     * @param row The row in matrix to retrieve from
     * @return the row of Matrix in question
     */
    private static int[] GetRow(int[][] Matrix, int row)
    {
        int[] result = new int[Matrix[row].length];
        for(int i = 0; i < Matrix[row].length; i++)
        {
                result[i] = Matrix[row][i];
        }
        return result;
    }

    /**
     * Subtracts two vectors
     * @param array1 a vector
     * @param array2 a vector
     * @return array1 - array2
     */
    private static int[] Subtract_Arrays(int[] array1, int[] array2)
    {
        int[] result = new int[array1.length];
        for(int i = 0; i < array1.length; i++)
        {
            result[i] = array1[i] - array2[i];
        }
        return result;
    }

    /**
     * Adding two vectors
     * @param array1 a vector
     * @param array2 a vector
     * @return array1 - array2
     */
    private static int[] Add_Arrays(int[] array1, int[] array2)
    {
        int[] result = new int[array1.length];
        for(int i = 0; i < array1.length; i++)
        {
            result[i] = array1[i] - array2[i];
        }
        return result;
    }

    /**
     * Calculates the need vector from the max and allocation matrix
     * @param m max matrix
     * @param a allocation matrix
     * @return the need vector max - allocation
     */
    private static int[][] Calculate_NeedArray(int[][] m, int[][] a)
    {
        int[][] need = new  int[m.length][m[0].length];
        for(int i = 0; i < need.length; i++)
        {
            for(int j = 0; j < need[0].length; j++)
            {
                need[i][j] = m[i][j] - a[i][j];
            }
        }
        return need;
    }
}

