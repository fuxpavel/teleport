package com.teleport.client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static com.teleport.client.Protocol.*;

public class Sender
{
    private static final int PORT = 10113;
    private static final int BUF_SIZE = 1024;

    private static String size(long size1)
    {
        float size = size1;
        if (size > 1000000)
        {
            if (size > 1000000000)
            {
                return String.format("%.1f", size / 1000000000) + " GB";
            }
            else
            {
                return String.format("%.1f", size / 1000000) + " MB";
            }

        }
        return String.format("%.1f", size / 1000) + " KB";
    }

    public boolean send(String path) throws IOException
    {
        try (ServerSocket serverSock = new ServerSocket(PORT))
        {
            try (Socket sock = serverSock.accept())
            {
                File myFile = new File(path);
                BufferedInputStream in1 = new BufferedInputStream(new FileInputStream(myFile));
                InputStream in = sock.getInputStream();
                OutputStream out = sock.getOutputStream();
                byte[] buf = new byte[BUF_SIZE];
                int count;
                String filename = path.substring(path.lastIndexOf("\\") + 1);
                out.write(
                        (P2P_CONNECT_REQUEST + "-" + filename + "-" + size(myFile.length()) + "--").getBytes("UTF-8"));
                out.flush();
                in.read(buf);

                if (new String(buf, StandardCharsets.UTF_8).substring(0, 9)
                                                           .equals(P2P_ANS_CONNECT_REQUEST + "-" + P2P_POSITIVE_ANS +
                                                                   "--"))
                {
                    while ((count = in1.read(buf)) > 0)
                    {
                        out.write(buf, 0, count);
                        out.flush();
                    }
                    sock.close();
                }
                else
                {
                    sock.close();
                }
            }
        }
        return true;
    }
}
