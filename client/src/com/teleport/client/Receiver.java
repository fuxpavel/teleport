package com.teleport.client;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static com.teleport.client.Protocol.*;

public class Receiver
{
    private static final int PORT = 10113;
    private static final int BUF_SIZE = 1024;
    private Socket sock;
    private InputStream in;
    private OutputStream out;
    private String fileName;
    private String size;

    public boolean receive(String ip, boolean chose) throws IOException
    {
        int amout_of_files = 1;
        String[] input;
        byte[] buf;
        boolean first = true;
        for (int i = 0; i < amout_of_files; i++)
        {
            sock = new Socket(ip, PORT);
            in = sock.getInputStream();
            out = sock.getOutputStream();
            buf = new byte[BUF_SIZE];
            if (first)
            {
                first = false;
                in.read(buf);
                input = new String(buf, StandardCharsets.UTF_8).split("-");
                if (input[0].equals(P2P_CONNECT_REQUEST) && input[1].equals(P2P_AMOUT_OF_FILES))
                {
                    amout_of_files = Integer.parseInt(input[2]);
                }
            }
            in.read(buf);
            input = new String(buf, StandardCharsets.UTF_8).split("-");
            if (input[0].equals(P2P_SEND_FILE))
            {
                fileName = input[1];
                size = input[2];
                System.out.println("want send you a file " + size + " " + fileName + " do you want to get it? (y/n)");
                long startTime = System.currentTimeMillis();
                if (chose)
                {
                    ReceiveFile();
                    long endTime = System.currentTimeMillis();
                    System.out.println(endTime - startTime);//convert from millisec to min
                }
                else
                {
                    DenialFile();
                }
            }
        }
        return true;
    }

    public void ReceiveFile() throws IOException
    {
        byte[] buf = new byte[BUF_SIZE];
        int len;
        out.write((P2P_ANS_CONNECT_REQUEST + "-" + P2P_POSITIVE_ANS + "--").getBytes());
        out.flush();
        FileOutputStream fos = new FileOutputStream(fileName);
        while ((len = in.read(buf)) > 0)
        {
            fos.write(buf, 0, len);
        }
        fos.close();
        sock.close();
    }

    public void DenialFile() throws IOException
    {
        out.write((P2P_ANS_CONNECT_REQUEST + "-" + P2P_REFUSE_ANS + "--").getBytes());
        out.flush();
        sock.close();
    }
}