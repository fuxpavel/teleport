package com.teleport.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.FloatMap;
import javafx.scene.text.Text;

class ProgressBarSend extends Thread
{
    SendFiles sender;
    ProgressBar pb;
    boolean sendSide;
    Text lblSendFile;
    String receiver;
    int num;

    ProgressBarSend(String recv, SendFiles p, Text lbl, ProgressBar bar, boolean send)
    {
        num =0;
        sender = p;
        pb = bar;
        sendSide = send;
        receiver = recv;
        lblSendFile = lbl;

    }

    @Override
    public void run()
    {
        try
        {
            String a[];
            while (true)
            {
                String message = sender.getMessage();
                a = message.split(" ");
                float p = Float.parseFloat(a[0])/Float.parseFloat(a[1]);
                pb.setProgress(p);
                if(Integer.parseInt(String.format("%.0f", p*100)) > num +1 || num == 99)
                {
                    num++;
                    if(sendSide)
                    {
                        lblSendFile.setText(" send " + sender.GetFileName() + " to " + receiver + " | " + String.format("%.0f", p * 100) + "%");
                    }//System.out.println(String.format("%.0f", p * 100));
                    else
                    {
                        lblSendFile.setText(" receive " + sender.GetFileName() + " from " + receiver + " | " + String.format("%.0f", p * 100) + "%");
                    }
                }
                System.out.println(p);
                if(p==1.0)
                {
                    pb.setStyle("-fx-accent: green;");
                    //lblSendFile.setText("");
                }
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}

