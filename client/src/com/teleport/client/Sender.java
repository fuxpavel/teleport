package com.teleport.client;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.teleport.client.Protocol.*;

public class Sender
{
    private static final int PORT = 10113;
    private static final int BUF_SIZE = 1024;

    public boolean send(Map<String, byte[]> contents, String reciever) throws IOException
    {
        try (Socket sock = new Socket(reciever, PORT))
        {
            InputStream in = sock.getInputStream();
            OutputStream out = sock.getOutputStream();
            byte[] buf = new byte[BUF_SIZE];
            int len;

            //begin
            out.write((P2P_CONNECT_REQUEST + "||").getBytes("UTF-8"));
            out.flush();
            in.read(buf);

            if (buf.toString().equals(P2P_ANS_CONNECT_REQUEST + "|" + P2P_POSITIVE_ANS + "||"))
            {
                //send
                for (Map.Entry<String, byte[]> entry : contents.entrySet())
                {
                    out.write((P2P_SEND_FILE + "|" + entry.getKey() + "||").getBytes(StandardCharsets.UTF_8));
                    out.flush();
                    in.read(buf);
                    if (new String(buf, StandardCharsets.UTF_8)
                            .equals(P2P_ANS_SEND_FILE + "|" + P2P_POSITIVE_ANS + "||"))
                    {
                        out.write(ArrayUtils.addAll(ArrayUtils.addAll((P2P_SEND_CONTENT + "|")
                                                                              .getBytes(StandardCharsets.UTF_8),
                                                                      entry.getValue()),
                                                    "||".getBytes(StandardCharsets.UTF_8)));
                    }
                }

                //end
                out.write((P2P_TRANSFER_COMPLETE + "||").getBytes(StandardCharsets.UTF_8));
            }
        }
        return true;
    }
}
