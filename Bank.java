package com.company;

public final class Bank {

    static int numberOfCustomers;
    static int numberOfResources;
    static int[] available;    //The available amount of each resource
    static int[][] max;    //The maximum demand of each customer
    static int[][] allocation; //Total Number of Resources being allocated to each thread
    static int[][] need; //the remaining needs of each customer
    private final Object lock = new Lock();

    public static void Initialize(int m, int n)
    {
        numberOfResources = m;
        numberOfCustomers = n;
        available = RandomGenerator.RandomAvailableVector((numberOfResources));
        max = RandomGenerator.RandomMaximumArray(numberOfCustomers,numberOfResources);
        allocation = new int[numberOfCustomers][numberOfResources];

        System.out.println("Initial Available Array: ");
        Main.PrintVector(available);
    }

    //Methods
//    public static void addCustomer(int customerNum, int[] maximumDemand)
//    {
//
//    }

    public static boolean isSafe()
    {
        int index;
        boolean flag;
        //Step 1: Initialize Work and Finish
        int[] Work = available;
        boolean[] Finish = new boolean[numberOfCustomers];
        for(int i = 0; i < numberOfCustomers; i++)
        {
            Finish[i] = false;
        }

        //Step 2: Find an index i such that Finish[i] = false and Need <= Work
        index = 0;
        while(true) {
            flag = (!Finish[index]) && (Array1_LTEQ_Array2(Subtract_Arrays(GetRow(max, index), GetRow(allocation, index)), Work));
            while (!flag && (index < Finish.length)) //There is a process that isn't finished and we haven't made it to the end of the array
            {
                index++;
                flag = !Finish[index] && Array1_LTEQ_Array2(Subtract_Arrays(GetRow(max, index), GetRow(allocation, index)), Work);
            }
            if (!flag && (index == Finish.length)) //Got to the end without finding a index STEP 4
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

    public static boolean isSafe_AfterThisRequest(int[] request, int num)
    {

        int index;
        boolean flag;
        //Step 1: Initialize Work and Finish
        int[] Work = Subtract_Arrays(available,request);
        int[][] Temp_Allocation = allocation;
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
            flag = (!Finish[index]) && (Array1_LTEQ_Array2(Subtract_Arrays(GetRow(max, index), GetRow(Temp_Allocation, index)), Work));
            while (!flag && (index < Finish.length)) //There is a process that isn't finished and we haven't made it to the end of the array
            {
                index++;
                flag = !Finish[index] && Array1_LTEQ_Array2(Subtract_Arrays(GetRow(max, index), GetRow(Temp_Allocation, index)), Work);
            }
            if (!flag && (index == Finish.length)) //Got to the end without finding a index STEP 4
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

    public static void ProcessRequest(int[] request, int num)
    {
        available = Subtract_Arrays(available,request);
        for(int k = 0; k < allocation[num].length; k++)
        {
            allocation[num][k] = allocation[num][k] + request[k];
        }
    }

    public static void releaseResources(int customerNumber, int[] release) {

    }

    public static boolean Array1_LTEQ_Array2(int[] array1, int[] array2) {
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

    public static int[] GetRow(int [][] Matrix, int row)
    {
        int[] result = new int[Matrix[row].length];
        for(int i = 0; i < Matrix[row].length; i++)
        {
                result[i] = Matrix[row][i];
        }
        return result;
    }

    public static int[] Subtract_Arrays(int[] array1, int[] array2)
    {
        int[] result = new int[array1.length];
        for(int i = 0; i < array1.length; i++)
        {
            result[i] = array1[i] - array2[i];
        }
        return result;
    }

    public static int[] Add_Arrays(int[] array1, int[] array2)
    {
        int[] result = new int[array1.length];
        for(int i = 0; i < array1.length; i++)
        {
            result[i] = array1[i] - array2[i];
        }
        return result;
    }
}

