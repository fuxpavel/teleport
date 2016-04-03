package com.teleport.client;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.FloatMap;

class ProgressBarSend extends Thread
{
    SendFiles sender;
    ProgressBar pb;
    String[] a;
    ProgressBarSend(SendFiles p, ProgressBar bar)
    {
        sender = p;
        pb = bar;
    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                String message = sender.getMessage();
                a = message.split(" ");
                changeBar();
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    @FXML
    public void changeBar()
    {
        pb.setProgress(Float.parseFloat(a[0])/Float.parseFloat(a[1]));
        if((Float.parseFloat(a[0])/Float.parseFloat(a[1]))==1.0)
        {
            pb.setStyle("-fx-accent: green;");
        }
    }
}

