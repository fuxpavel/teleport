package com.teleport.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Reciever
{
    private static final int PORT = 10113;

    public List<byte[]> send(String reciever) throws IOException
    {
        List<byte[]> contents = new ArrayList<>();
        Socket sock = new Socket(reciever, PORT);
        InputStream inStream = sock.getInputStream();

    }
}
