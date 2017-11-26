package com.company;

public class Main {

    public static void main(String[] args) {
        int R = Integer.parseInt(args[0]); //Number of Resources
        int C = Integer.parseInt(args[1]); //Number of Customers
//        int R = 4; //Number of Resources
//        int C = 5; //Number of Customers
        Bank.Initialize(R,C);

        Customer CustomerArray[] = new Customer[C];
        //Initialize
        for(int i = 0; i < C; i++)
        {
            CustomerArray[i] = new Customer(i);
        }
        for(int i = 0; i < C; i++)
        {
            CustomerArray[i].start();
        }
        while(Bank.Completed < C)
        {
            Thread.yield();
        }
    }

    /**
     * Prints a vector out to the console
     * @param v the vector to print
     */
    static void PrintVector(int[] v)
    {
        StringBuilder sb = new StringBuilder();
        sb.append('['); sb.append(' ');
        for (int aV : v) {
            sb.append(aV);
            sb.append(' ');
        }
        sb.append("]");
        System.out.println(sb);
    }
}
