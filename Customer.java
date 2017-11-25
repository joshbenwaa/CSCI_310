package com.company;

import java.util.Random;

public class Customer extends Thread {
    private Thread t;
    private int CustomerNum;
    private boolean Finished;
    Bank  TheBank;

    Customer(int CustomerN)
    {
        CustomerNum = CustomerN;
        //TheBank = TheB;
        Finished = false;
    }
    public void start()
    {
        if(t == null)
        {
            t = new Thread (this, "Customer" + CustomerNum);
            t.start();
        }
    }

    public void run()
    {
        int Sleep = (int) (Math.random() * 5); //On a scale of 1 to 5 second
        int NumOfRequests = (int) (Math.random() * 3); //0,1,2 or 3 requests
        try {
            Thread.sleep(Sleep * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < NumOfRequests; i++)
        {
            try {
                requestResources(CustomerNum,RandomGenerator.RandomRequestVector(Bank.max,CustomerNum, Bank.numberOfResources));
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
        Finished = true;
    }

    private boolean requestResources(int customerNumber, int[] request) throws InterruptedException {
        boolean safe = false;
        while(!safe)
        {
            //Step 1: Request <= Need
            for(int i = 0; i < Bank.numberOfResources; i++)
            {
                if(request[i] > (Bank.max[customerNumber][i] - Bank.allocation[customerNumber][i]))
                {
                    return false;
                }
            }
            //Step 2: Request <= Available
            for(int i = 0; i < Bank.numberOfResources; i++)
            {
                if(request[i] > Bank.available[i])
                {
                    t.wait(); //Wait until the resources are available
                }
            }
            //Step 3: Pretend it happens and then check if it is safe to do;
            if(Bank.isSafe_AfterThisRequest(request,customerNumber)) //Request is safe
            {
                safe = true;
            }
            else //Request must wait
                t.wait();
        }
        //Request was safe, now can change the banks arrays
        Bank.ProcessRequest(request, customerNumber);
        notifyAll();
        return true;

    }

}
