package com.teleport.client;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

public class Sender
{
    private static final String P2P_CONNECT_REQUEST = "301";
    private static final String P2P_ANS_CONNECT_REQUEST = "302";
    private static final String P2P_POSITIVE_ANS = "303";
    private static final String P2P_REFUSE_ANS = "304";
    private static final String P2P_SEND_FILE = "305";
    private static final String P2PTRANSFER_COMPLETE = "306";

    private static final int PORT = 10113;

    public boolean send(Map<String, byte[]> contents, String reciever) throws IOException
    {
        Socket sock = new Socket(reciever, PORT);
        InputStream in = sock.getInputStream();
        OutputStream out = sock.getOutputStream();

        //begin

        //send
        for (Map.Entry<String, byte[]> entry: contents.entrySet())
        {
            byte[] fileName = (entry.getKey() + "|").getBytes();
            byte[] sendData = ArrayUtils.addAll(fileName, entry.getValue());
            out.write(sendData);
        }



        //end

    }
}
