package com.teleport.client;

import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FriendshipQuerier implements Runnable
{
    private static final int delay = 15000; //in milliseconds

    private Thread t;
    private String name;
    private Client client;
    private List<String> incoming;
    private List<String> outgoing;

    public FriendshipQuerier(String name)
    {
        this.name = name;
        try
        {
            client = new Client();
            incoming = new ArrayList<>();
            outgoing = new ArrayList<>();
        }
        catch (IOException e)
        {
            System.out.println("error in cotr friendship thread");
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                List<String> newIncoming = client.getIncomingFriendRequests();
                List<String> temp = new ArrayList<>(newIncoming);
                newIncoming.removeAll(incoming);
                if (!newIncoming.isEmpty())
                {
                    incoming = temp;
                    System.out.println("New incoming requests: " + newIncoming);
                }
                outgoing = client.getOutgoingFriendRequests();
                Thread.sleep(delay);
            }
            catch (IOException | ParseException | InterruptedException e)
            {
                System.out.println("error in thread friendship");
                System.out.println(e.getMessage());
            }
        }
    }


    public void start()
    {
        if (t == null)
        {
            t = new Thread(this, name);
            t.start();
        }
    }
}
