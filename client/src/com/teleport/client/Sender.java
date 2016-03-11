package com.teleport.client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import static com.teleport.client.Protocol.*;
import java.nio.charset.StandardCharsets;


public class Sender
{
    private static final int PORT = 10113;
    private static final int BUF_SIZE = 1024;

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
                String filename = path.substring(path.lastIndexOf("\\")+1);
                long size = myFile.length();
                out.write((P2P_CONNECT_REQUEST + "-" + filename + "-"+size+"--").getBytes("UTF-8"));
                out.flush();
                in.read(buf);

                if (new String(buf, StandardCharsets.UTF_8).substring(0,9).equals(P2P_ANS_CONNECT_REQUEST + "-" + P2P_POSITIVE_ANS + "--"))
                {
                    while ((count = in1.read(buf)) > 0)
                    {
                        out.write(buf, 0, count);
                        out.flush();
                    }
                    sock.close();
                }
            }
        }
        return true;
    }
}
