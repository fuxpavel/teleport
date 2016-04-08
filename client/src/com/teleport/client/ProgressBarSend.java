package com.teleport.client;

import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

class ProgressBarSend extends Thread
{
    P2PCommunication sender;
    ProgressBar progressBar;
    boolean sendSide;
    Text lblSendFile;
    String receiver;
    int count;

    ProgressBarSend(String recv, P2PCommunication p, Text lbl, ProgressBar bar, boolean send)
    {
        count =0;
        sender = p;
        progressBar = bar;
        sendSide = send;
        receiver = recv;
        lblSendFile = lbl;
    }

    @Override
    public void run()
    {
        try
        {
            String info[];
            while (true)
            {
                String message = sender.getMessage();
                info = message.split(" ");
                float percent = Float.parseFloat(info[0]) / Float.parseFloat(info[1]);
                progressBar.setProgress(percent);
                if (Integer.parseInt(String.format("%.0f", percent * 100)) > count + 1 || count == 99)
                {
                    count++;
                    if (sendSide)
                    {
                        lblSendFile.setText(" send " + sender.GetFileName() + " to " + receiver + " | " + String.format("%.0f", percent * 100) + "%");
                    }
                    else
                    {
                        lblSendFile.setText(" receive " + sender.GetFileName() + " from " + receiver + " | " + String.format("%.0f", percent * 100) + "%");
                    }
                }
                if (percent == 1.0)
                {
                    progressBar.setStyle("-fx-accent: green;");
                }
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}

