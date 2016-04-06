package com.teleport.client;

import com.sun.org.apache.xerces.internal.util.TeeXMLDocumentFilterImpl;
import javafx.scene.text.Text;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TransferQuerier implements Runnable
{
    private static final int delay = 15000; //in milliseconds

    private Thread t;
    private String name;
    private Client client;
    private List<String> incoming;
    private List<String> outgoing;

    public TransferQuerier(String name)
    {
        this.name = name;
        try
        {
            client = new Client();
            incoming = client.getIncomingTransfers();
            outgoing = client.getOutgoingTransfers();
        }
        catch (IOException | ParseException e)
        {
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
                List<String> newIncoming = client.getIncomingTransfers();
                List<String> temp = new ArrayList<>(newIncoming);
                newIncoming.removeAll(incoming);
                if (!newIncoming.isEmpty())
                {
                    incoming = temp;
                    System.out.println("New incoming transfers: " + newIncoming);
                }
                outgoing = client.getOutgoingTransfers();
                Thread.sleep(delay);
            }
            catch (IOException | ParseException | InterruptedException e)
            {
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
