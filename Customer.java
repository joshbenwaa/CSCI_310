package com.company;

public class Customer extends Thread {
    private Thread t;
    private int CustomerNum;

    /**
     * Constructor
     * @param CustomerN customer number
     */
    Customer(int CustomerN)
    {
        CustomerNum = CustomerN;
    }

    /**
     * Function called to start thread
     */
    public void start()
    {
        if(t == null)
        {
            t = new Thread (this, "Customer" + CustomerNum);
            t.start();
        }
    }

    /**
     * The work of the thread
     */
    public void run()
    {
        int Sleep = (int) (Math.random() * 5); //On a scale of 1 to 5 second
        int NumOfRequests = (int) (Math.random() * 3) + 1; //1,2,3 or 4 requests
        try {
            Thread.sleep(Sleep * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < NumOfRequests; i++)
        {
            try {
                Bank.requestResources(CustomerNum,RandomGenerator.RandomRequestVector(Bank.max,CustomerNum, Bank.numberOfResources));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Sleep = (int) (Math.random() * 5); //On a scale of 1 to 5 second
            try {
                Thread.sleep(Sleep * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Bank.releaseResources(CustomerNum);
        Bank.Completed++;
    }
}