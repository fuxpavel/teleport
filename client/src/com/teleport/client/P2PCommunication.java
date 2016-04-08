package com.teleport.client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Vector;

import static com.teleport.client.Protocol.*;
import static com.teleport.client.Protocol.P2P_POSITIVE_ANS;

public class P2PCommunication extends Thread
{
    private List<String> paths;
    private static final int PORT = 10113;
    private static final int BUF_SIZE = 1024;
    static final int MAXQUEUE = 5;
    private String fileName;
    private static int BYTES2GB = 1073741824;
    private static int BYTES2MB = 1048576;
    private static int BYTES2KB = 1024;
    private Vector messages = new Vector();
    private int currentSize;
    private int fileSize;
    private String ip;
    private boolean recv;

    public P2PCommunication(List<String> path)
    {
        paths = path;
        currentSize = 0;
    }
    public P2PCommunication(String send, boolean chose)
    {
        ip = send;
        recv = chose;
        currentSize = 0;
    }
    private static String sizeToString(long size1)
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
    private static int sizeToInt(String size1)
    {
        String[] unit = size1.split(" ");
        if(unit[1].equals("KB"))
        {
            return Math.round(Float.parseFloat(unit[0]))*BYTES2KB;
        }
        else if (unit[1].equals("MB"))
        {
            return Math.round(Float.parseFloat(unit[0]))*BYTES2MB;
        }
        else
        {
            return Math.round(Float.parseFloat(unit[0]))*BYTES2GB;
        }
    }
    public int GetFileSize()
    {
        return fileSize;
    }

    public int GetCurrentSize()
    {
        return currentSize;
    }

    public String GetFileName()
    {
        return fileName;
    }

    @Override
    public void run()
    {
        if(ip == null)
        {
            Sender();
        }
        else
        {
            Receiver();
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
    }
    public void Sender()
    {
        try
        {
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
                            out.write((P2P_CONNECT_REQUEST + "-" + P2P_AMOUT_OF_FILES + "-" + paths.size() + "--").getBytes("UTF-8"));
                            out.flush();
                            first = false;
                        }

                        int count;
                        fileName = compress.substring(compress.lastIndexOf("\\") + 1);
                        fileSize = (int)myFile.length();
                        out.write((P2P_SEND_FILE + "-" + fileName + "-" + sizeToString(myFile.length()) + "--").getBytes("UTF-8"));
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
    public void Receiver()
    {
        currentSize = 0;
        int amout_of_files = 1;
        String[] input;
        byte[] buf;
        int len;
        boolean first = true;
        for (int i = 0; i < amout_of_files; i++)
        {
            try (Socket sock = new Socket(ip, PORT))
            {
                InputStream in = sock.getInputStream();
                OutputStream out = sock.getOutputStream();
                buf = new byte[BUF_SIZE];
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
                    fileName = input[1];
                    fileSize = sizeToInt(input[2]);
                    System.out.println("want send you a file " + fileSize + " " + fileName + " do you want to get it? (y/n)");
                    long startTime = System.currentTimeMillis();
                    if (recv)
                    {
                        buf = new byte[BUF_SIZE];
                        out.write((P2P_ANS_CONNECT_REQUEST + "-" + P2P_POSITIVE_ANS + "--").getBytes());
                        out.flush();
                        FileOutputStream fos = new FileOutputStream(fileName);
                        while ((len = in.read(buf)) > 0)
                        {
                            currentSize = currentSize + len;
                            fos.write(buf, 0, len);
                            putMessage(GetCurrentSize() + " " + GetFileSize());
                        }
                        currentSize = fileSize;
                        putMessage(GetCurrentSize() + " " + GetFileSize());
                        fos.close();
                        sock.close();
                        long endTime = System.currentTimeMillis();
                        System.out.println(endTime - startTime);//convert from millisec to min
                    }
                    else
                    {
                        out.write((P2P_ANS_CONNECT_REQUEST + "-" + P2P_REFUSE_ANS + "--").getBytes());
                        out.flush();
                        sock.close();
                    }
                }
            }
            catch (IOException | InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
    public synchronized String getMessage() throws InterruptedException
    {
        notify();
        while (messages.size() == 0)
        {
            wait();
        }
        String message = (String) messages.firstElement();
        messages.removeElement(message);
        return message;
    }
}
