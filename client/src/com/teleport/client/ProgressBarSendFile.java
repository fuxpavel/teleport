package com.teleport.client;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProgressBarSendFile implements Runnable
{
    private static final int delay = 15000; //in milliseconds

    private Thread t;
    private String name;
    private Sender sender;
    private List<String> path;

    public ProgressBarSendFile(List<String> paths)
    {
        this.name = name;
        sender = new Sender();
        path = paths;
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {
                sender.send(path);
            } catch (IOException e)
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