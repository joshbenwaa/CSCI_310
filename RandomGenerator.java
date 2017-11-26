package com.company;

public final class RandomGenerator{

    /**
     * Returns a random request vector
     * @param Max the max demand matrix
     * @param CustomerNum customer number
     * @param NumberOfResources number resources
     * @return the request vector
     */
    static int[] RandomRequestVector(int[][] Max, int CustomerNum, int NumberOfResources)
    {
        int[] result = new int[NumberOfResources];
        for(int i = 0; i < NumberOfResources; i++)
        {
            result[i] = randomWithRange(0,Max[CustomerNum][i]);
        }
        return result;
    }

    /**
     * Returns a random max array
     * @param NumberOfCustomers number of customers (rows)
     * @param NumberOfResources number of resources (columns)
     * @return random max array
     */
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

    /**
     * Returns a random available
     * @param NumberOfResources number of resource types
     * @param NumberOfCustomers number of customers
     * @return random available vector
     */
    static int[] RandomAvailableVector(int NumberOfResources, int NumberOfCustomers)
    {
        int[] result = new int[NumberOfResources];
        int[] Total = new int[NumberOfResources];
        int slightlyAboveTotal;
        for(int k = 0; k < NumberOfResources; k++)
        {
            for(int j = 0; j < NumberOfCustomers; j++)
            {
                Total[k] += Bank.max[j][k];
            }
        }

        for(int i = 0; i < NumberOfResources; i++)
        {
            slightlyAboveTotal = randomWithRange(Total[i],Total[i] + 3);
            result[i] = randomWithRange(Total[i],slightlyAboveTotal); //Randomly just slightly more than the total demand
        }
        return result;
    }

    /**
     * Returns a random number based on a range
     * @param min min of the range
     * @param max max of the range
     * @return returns a random number between min and max
     */
    private static int randomWithRange(int min, int max)
    {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }
}
