package com.teleport.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.teleport.client.Protocol.*;

public class Reciever
{
    private static final int PORT = 10113;
    private static final int BUF_SIZE = 1024;

    public Map<String, List<byte[]>> recieve() throws IOException
    {
        try (ServerSocket serverSock = new ServerSocket(PORT))
        {
            try(Socket sock = serverSock.accept())
            {
                InputStream in = sock.getInputStream();
                OutputStream out = sock.getOutputStream();
                byte[] buf = new byte[BUF_SIZE];
                String recv, cmp;
                Map<String, List<byte[]>> contents = new HashMap<>();

                in.read(buf);
                if (new String(buf, StandardCharsets.UTF_8).equals(P2P_CONNECT_REQUEST + "||"))
                {
                    out.write((P2P_ANS_CONNECT_REQUEST + "|" + P2P_POSITIVE_ANS + "||").getBytes());
                    out.flush();

                    in.read(buf);
                    recv = new String(buf, StandardCharsets.UTF_8);
                    cmp = P2P_SEND_FILE + "|";
                    if (recv.indexOf(cmp) == 0)
                    {
                        String fileName = recv.substring(recv.indexOf("|") + 1, recv.indexOf("||"));

                    }
                }
                return contents;
            }
        }
    }
}