package com.company;

import java.io.Console;

public class Main {

    public static void main(String[] args) {
//        int n = Integer.parseInt(args[0]); //Number of Resources
//        int m = Integer.parseInt(args[1]); //Number of Customers
        int n = 4; //Number of Resources
        int m = 5; //Number of Customers
        Bank.Initialize(m,n);

        Customer CustomerArray[] = new Customer[n];
        //Initialize
        for(int i = 0; i < n; i++)
        {
            CustomerArray[i] = new Customer(i);
        }
        for(int i = 0; i < n; i++)
        {
            CustomerArray[i].start();
        }
    }

    public static void PrintVector(int[] v)
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
