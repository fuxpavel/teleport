package com.teleport.client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.teleport.client.Protocol.*;

public class Sender
{
    private static final int PORT = 10113;
    private static final int BUF_SIZE = 1024;

    private static String size(long size1)
    {
        float size = size1;

        if (size > 1048576)
        {
            if (size > 1073741824)
            {
                return String.format("%.1f", size / 1073741824) + " GB";
            }
            else
            {
                return String.format("%.1f", size / 1048576) + " MB";
            }

        }
        return String.format("%.1f", size / 1024) + " KB";
    }

    public boolean send(List<String> paths) throws IOException
    {
        boolean first = true;
        for (String path : paths)
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
                    if (first)
                    {
                        out.write((P2P_CONNECT_REQUEST + "-" + P2P_AMOUT_OF_FILES + "-" + paths.size() + "--")
                                          .getBytes("UTF-8"));
                        out.flush();
                        first = false;
                    }

                    int count;
                    String filename = path.substring(path.lastIndexOf("\\") + 1);
                    out.write((P2P_SEND_FILE + "-" + filename + "-" + size(myFile.length()) + "--").getBytes("UTF-8"));
                    out.flush();
                    in.read(buf);

                    if (new String(buf, StandardCharsets.UTF_8).substring(0, 9).equals(P2P_ANS_CONNECT_REQUEST + "-" +
                                                                                       P2P_POSITIVE_ANS + "--"))
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
        }
        return true;
    }
}
