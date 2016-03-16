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


    public boolean receive(String ip) throws IOException
    {
        int amout_of_files = 1;
        String[] input;
        boolean first = true;
        for (int i = 0; i < amout_of_files; i++)
        {
            try (Socket sock = new Socket(ip, PORT))
            {
                InputStream in = sock.getInputStream();
                OutputStream out = sock.getOutputStream();
                byte[] buf = new byte[BUF_SIZE];
                int len;
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
                    String filename = input[1];
                    String size = input[2];
                    System.out
                            .println("want send you a file " + size + " " + filename + " do you want to get it? (y/n)");
                    Scanner scanner = new Scanner(System.in);
                    if (scanner.next().equals("y"))
                    {
                        out.write((P2P_ANS_CONNECT_REQUEST + "-" + P2P_POSITIVE_ANS + "--").getBytes());
                        out.flush();

                        FileOutputStream fos = new FileOutputStream("D:\\Users\\user-pc\\Desktop\\" + filename);
                        while ((len = in.read(buf)) > 0)
                        {
                            fos.write(buf);
                        }
                        fos.close();
                        sock.close();
                    }
                    else
                    {
                        out.write((P2P_ANS_CONNECT_REQUEST + "-" + P2P_REFUSE_ANS + "--").getBytes());
                        out.flush();
                        sock.close();
                    }
                }
            }
        }
        return true;
    }
}