package com.teleport.client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Vector;

import static com.teleport.client.Protocol.*;
import static com.teleport.client.Protocol.P2P_POSITIVE_ANS;

public class SendFiles extends Thread
{
    private List<String> paths;
    private static final int PORT = 10113;
    private static final int BUF_SIZE = 1024;
    static final int MAXQUEUE = 5;
    private static int BYTES2GB = 1073741824;
    private static int BYTES2MB = 1048576;
    private static int BYTES2KB = 1024;
    private Vector messages = new Vector();
    private int currentSize;
    private int fileSize;

    public SendFiles(List<String> path)
    {
        paths = path;
    }
    private static String size(long size1)
    {
        float size = size1;

        if (size > BYTES2MB)
        {
            if (size > BYTES2GB)
            {
                return String.format("%.1f", size / BYTES2GB) + " GB";
            }
            else
            {
                return String.format("%.1f", size / BYTES2MB) + " MB";
            }

        }
        return String.format("%.1f", size / BYTES2KB) + " KB";
    }

    public int GetFileSize()
    {
        return fileSize;
    }

    public int GetCurrentSize()
    {
        return currentSize;
    }
    @Override
    public void run()
    {
        try
        {
            currentSize = 0;
            boolean first = true;
            for (String path : paths)
            {
                try (ServerSocket serverSock = new ServerSocket(PORT))
                {
                    try (Socket sock = serverSock.accept())
                    {
                        String compress = Compress.Compression(path);
                        File myFile = new File(compress);
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
                        String filename = compress.substring(compress.lastIndexOf("\\") + 1);
                        fileSize = (int)myFile.length();
                        out.write((P2P_SEND_FILE + "-" + filename + "-" + size(myFile.length()) + "--").getBytes("UTF-8"));
                        out.flush();
                        in.read(buf);

                        if (new String(buf, StandardCharsets.UTF_8).substring(0, 9).equals(P2P_ANS_CONNECT_REQUEST + "-" +
                                P2P_POSITIVE_ANS + "--"))
                        {
                            while ((count = in1.read(buf)) > 0)
                            {
                                out.write(buf, 0, count);
                                currentSize = currentSize + count;
                                putMessage(GetCurrentSize()+" "+GetFileSize());
                                out.flush();
                            }
                            sock.close();
                        }
                        else
                        {
                            sock.close();
                        }

                        in.close();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
    public synchronized void putMessage(String msg) throws InterruptedException
    {
        while (messages.size() == MAXQUEUE)
        {
            wait();
        }
        messages.addElement(msg);
        notify();
        //Later, when the necessary event happens, the thread that is running it calls notify() from a block synchronized on the same object.
    }

    // Called by Consumer
    public synchronized String getMessage() throws InterruptedException
    {
        notify();
        while (messages.size() == 0)
        {
            wait();//By executing wait() from a synchronized block, a thread gives up its hold on the lock and goes to sleep.
        }
        String message = (String) messages.firstElement();
        messages.removeElement(message);
        return message;
    }
}
