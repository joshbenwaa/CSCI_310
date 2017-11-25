package com.company;

public final class RandomGenerator{

    public static int[][] RandomNeedArray(int [][] Max, int NumberOfCustomers, int NumberOfResources)
    {
        int[][] result = new int [NumberOfCustomers][NumberOfResources];
        int[] TotalResources = new int[NumberOfResources];

        for(int i = 0; i < NumberOfCustomers; i++)
        {
            for(int j = 0; j < NumberOfResources; j++)
            {
                randomWithRange(0,Max[i][j]);
            }
        }

        return result;
    }

    static int[] RandomRequestVector(int[][] Max, int CustomerNum, int NumberOfResources)
    {
        int[] result = new int[NumberOfResources];
        for(int i = 0; i < NumberOfResources; i++)
        {
            result[i] = randomWithRange(0,Max[CustomerNum][i]);
        }
        return result;
    }

    static int[][] RandomMaximumArray(int NumberOfCustomers, int NumberOfResources)
    {
        int[][] result = new int[NumberOfCustomers][NumberOfResources];
        for(int i = 0; i < NumberOfCustomers; i++)
        {
            for(int j = 0; j < NumberOfResources; j++)
            {
                result[i][j] = randomWithRange(0,10);
            }
        }
        return result;
    }

    static int[] RandomAvailableVector(int NumberOfResources)
    {
        int[] result = new int[NumberOfResources];
        for(int i = 0; i < NumberOfResources; i++)
        {
            result[i] = randomWithRange(0,10);
        }
        return result;
    }

    private static int randomWithRange(int min, int max)
    {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }
}
