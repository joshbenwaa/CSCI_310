package com.company;

import java.util.Arrays;

public final class Bank {

    private static int numberOfCustomers;
    static int numberOfResources;
    private static int[] available;    //The available amount of each resource
    static int[][] max;    //The maximum demand of each customer
    private static int[][] allocation; //Total Number of Resources being allocated to each thread
    static int Completed = 0;
    private static final Object lock = new Lock();

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

    private static boolean isSafe_AfterThisRequest(int[] request, int num)
    {

        int index;
        boolean flag;
        int [] need_i;
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
                return true; //All Process are finished
            }
            //Else a process is found
            Work = Add_Arrays(Work, GetRow(allocation, index));
            Finish[index] = true;

            //Reset index
            index = 0;
        }

    }

    private static void ProcessRequest(int[] request, int num)
    {
        available = Subtract_Arrays(available,request);
        for(int k = 0; k < allocation[num].length; k++)
        {
            allocation[num][k] = allocation[num][k] + request[k];
        }
    }

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

            lock.notify();
        }



    }

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

    private static int[] GetRow(int[][] Matrix, int row)
    {
        int[] result = new int[Matrix[row].length];
        for(int i = 0; i < Matrix[row].length; i++)
        {
                result[i] = Matrix[row][i];
        }
        return result;
    }

    private static int[] Subtract_Arrays(int[] array1, int[] array2)
    {
        int[] result = new int[array1.length];
        for(int i = 0; i < array1.length; i++)
        {
            result[i] = array1[i] - array2[i];
        }
        return result;
    }

    private static int[] Add_Arrays(int[] array1, int[] array2)
    {
        int[] result = new int[array1.length];
        for(int i = 0; i < array1.length; i++)
        {
            result[i] = array1[i] - array2[i];
        }
        return result;
    }

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

