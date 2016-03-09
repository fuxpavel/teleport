package com.teleport.client;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import static com.teleport.client.Protocol.*;

public class Sender
{
    private static final int BUF_SIZE = 1024;

    private static final int PORT = 10113;

    public boolean send(Map<String, byte[]> contents, String reciever) throws IOException
    {
        Socket sock = new Socket(reciever, PORT);
        InputStream in = sock.getInputStream();
        OutputStream out = sock.getOutputStream();
        byte[] buf = new byte[BUF_SIZE];
        int len;

        //begin
        out.write(P2P_CONNECT_REQUEST.getBytes());
        in.read(buf);
        if (!buf.toString().equals(P2P_ANS_CONNECT_REQUEST + "|" + P2P_POSITIVE_ANS + "||"))
        {
            return false;
        }
        //send
        for (Map.Entry<String, byte[]> entry: contents.entrySet())
        {
            byte[] fileName = (entry.getKey() + "|").getBytes("UTF-8");
            byte[] sendData = ArrayUtils.addAll(fileName, entry.getValue());
            out.write(sendData);
        }



        //end



        return true;
    }
}
