package com.teleport.client;

import javafx.concurrent.Task;
import javafx.util.Pair;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EmptyStackException;
import java.util.List;
import static com.teleport.client.Protocol.*;

public class P2PCommunication extends Thread
{
    private List<String> paths;
    private static final int PORT = 10113;
    private static final int BUF_SIZE = 2048;
    private String fileName;
    private static int BYTES2GB = 1073741824;
    private static int BYTES2MB = 1048576;
    private static int BYTES2KB = 1024;
    private int currentSize;
    private int fileSize;
    private String ip;
    private boolean choose;
    private String idConnection;
    private Transfer transferHandler;
    private float percent;
    private String receiver;
    private String ip_receiver;

    public P2PCommunication(String recv, List<String> path, Transfer transfer, String ip_recv)
    {
        //sender
        paths = path;
        currentSize = 0;
        receiver = recv;
        transferHandler = transfer;
        ip_receiver = ip_recv;
    }

    public P2PCommunication(String recv, String send, boolean chose, Transfer transfer)
    {
        //receiver
        ip = send;
        receiver = recv;
        choose = chose;
        currentSize = 0;
        transferHandler = transfer;
    }

    private static String sizeToString(long size1)
    {
        float size = size1;

        if (size > BYTES2MB)
        {
            if (size > BYTES2GB)
            {
                return String.format("%.1f", size / BYTES2GB) + " GB";
            } else
            {
                return String.format("%.1f", size / BYTES2MB) + " MB";
            }

        }
        return String.format("%.1f", size / BYTES2KB) + " KB";
    }

    private static int sizeToInt(String size1)
    {
        String[] unit = size1.split(" ");
        if (unit[1].equals("KB"))
        {
            return Math.round(Float.parseFloat(unit[0])) * BYTES2KB;
        } else if (unit[1].equals("MB"))
        {
            return Math.round(Float.parseFloat(unit[0])) * BYTES2MB;
        } else
        {
            return Math.round(Float.parseFloat(unit[0])) * BYTES2GB;
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

    public Task createWorker()
    {
        return new Task()
        {
            @Override
            protected Object call() throws Exception
            {
                if (ip == null)
                {
                    //sender
                    String path = "";
                    boolean first = true;
                    for (String path1 : paths)
                    {
                        path = path1;
                    }
                    updateMessage("zipping...");
                    String compress = Compress.Compression(path);
                    updateMessage("ready");
                    File myFile = new File(compress);
                    fileName = compress.substring(compress.lastIndexOf("\\") + 1);
                    fileSize = (int) myFile.length();
                    HttpResponse response = transferHandler.beginTransfer(receiver, fileName, sizeToString(myFile.length()));
                    String body = EntityUtils.toString(response.getEntity());
                    JSONObject json = (JSONObject) (new JSONParser().parse(body));
                    if (json.get("status").equals("success"))
                    {
                        idConnection = json.get("id").toString();
                        try (ServerSocket serverSock = new ServerSocket(PORT))
                        {
                            serverSock.setSoTimeout(new Authorization().getTimeout()*60000);
                            try (Socket sock = serverSock.accept())
                            {
                                String ip = sock.getRemoteSocketAddress().toString().split(":")[0].replace("/", "");
                                if (ip.equals(ip_receiver))
                                {
                                    BufferedInputStream in1 = new BufferedInputStream(new FileInputStream(myFile));
                                    InputStream in = sock.getInputStream();
                                    OutputStream out = sock.getOutputStream();
                                    byte[] buf = new byte[BUF_SIZE];
                                    if (first)
                                    {
                                        out.write((P2P_CONNECT_REQUEST + ":" + P2P_AMOUT_OF_FILES + ":" + paths.size() + ":" + idConnection.toString() + "::").getBytes("UTF-8"));
                                        out.flush();
                                        first = false;
                                    }
                                    int count;
                                    out.write((P2P_SEND_FILE + ":" + fileName + ":" + sizeToString(myFile.length()) + "::").getBytes("UTF-8"));
                                    out.flush();
                                    in.read(buf);

                                    if (new String(buf, StandardCharsets.UTF_8).substring(0, 9).equals(P2P_ANS_CONNECT_REQUEST + ":" + P2P_POSITIVE_ANS + "::"))
                                    {
                                        while ((count = in1.read(buf)) > 0)
                                        {
                                            out.write(buf, 0, count);
                                            currentSize = currentSize + count;
                                            updateProgress(GetCurrentSize(), GetFileSize());
                                            percent = (float) GetCurrentSize() / (float) GetFileSize();
                                            updateMessage(" send " + GetFileName() + " to " + receiver + " | " + String.format("%.0f", percent * 100) + "%");
                                            out.flush();
                                        }
                                        sock.close();
                                    }
                                    else
                                    {
                                        sock.close();
                                    }
                                    in.close();
                                    out.close();
                                    /*
                                    if(new Authorization().getZip())
                                    {
                                        //do execption
                                        System.out.println(compress);
                                        Files.delete(Paths.get(compress));
                                    }
                                    */
                                }
                            }
                            catch (SocketTimeoutException e)
                            {
                                //// FIXME: 24/04/2016   add to inbox. recvier not respones
                                response = transferHandler.endTransfer(idConnection);
                                updateMessage(e.getMessage());
                                updateProgress(0,1);
                            }
                        }
                        catch (IOException e)
                        {
                            response = transferHandler.endTransfer(idConnection);
                            updateMessage(e.getMessage());
                            updateProgress(0,1);
                            e.printStackTrace();
                            throw e;
                        }
                    }
                }
                else
                {
                    //receiver
                    currentSize = 0;
                    int amout_of_files = 1;
                    String[] input;
                    byte[] buf;
                    int len;
                    boolean first = true;
                    //for (int i = 0; i < amout_of_files; i++)
                    //{
                    try (Socket sock = new Socket(ip, PORT))
                    {
                        InputStream in = sock.getInputStream();
                        OutputStream out = sock.getOutputStream();
                        buf = new byte[BUF_SIZE];
                        if (first)
                        {
                            first = false;
                            in.read(buf);
                            input = new String(buf, StandardCharsets.UTF_8).split(":");
                            if (input[0].equals(P2P_CONNECT_REQUEST) && input[1].equals(P2P_AMOUT_OF_FILES))
                            {
                                amout_of_files = Integer.parseInt(input[2]);
                                idConnection = input[3];
                            }
                        }
                        in.read(buf);
                        input = new String(buf, StandardCharsets.UTF_8).split(":");
                        if (input[0].equals(P2P_SEND_FILE))
                        {
                            fileName = input[1];
                            fileSize = sizeToInt(input[2]);
                            if (choose)
                            {
                                Authorization authorizationHandler = new Authorization();
                                buf = new byte[BUF_SIZE];
                                out.write((P2P_ANS_CONNECT_REQUEST + ":" + P2P_POSITIVE_ANS + "::").getBytes());
                                out.flush();
                                String location = authorizationHandler.getPath() + "\\" + fileName;
                                FileOutputStream fos = new FileOutputStream(location);
                                while ((len = in.read(buf)) > 0)
                                {
                                    currentSize = currentSize + len;
                                    fos.write(buf, 0, len);
                                    updateProgress(GetCurrentSize(), GetFileSize());
                                    percent = (float) GetCurrentSize() / (float) GetFileSize();
                                    updateMessage(" receive " + GetFileName() + " from " + receiver + " | " + String.format("%.0f", percent * 100) + "%");
                                }
                                currentSize = fileSize;
                                updateProgress(GetCurrentSize(), GetFileSize());
                                percent = (float) GetCurrentSize() / (float) GetFileSize();
                                updateMessage(" receive " + GetFileName() + " from " + receiver + " | " + String.format("%.0f", percent * 100) + "%");
                                fos.close();
                                sock.close();
                                if(authorizationHandler.getOpen())
                                {
                                    Runtime.getRuntime().exec("explorer.exe /select," + location);
                                }
                            }
                            else
                            {
                                out.write((P2P_ANS_CONNECT_REQUEST + ":" + P2P_REFUSE_ANS + "::").getBytes());
                                out.flush();
                                sock.close();
                            }
                            HttpResponse response = transferHandler.endTransfer(idConnection);
                            String body = EntityUtils.toString(response.getEntity());
                            JSONObject json = (JSONObject) (new JSONParser().parse(body));
                        }
                    }
                    catch (IOException | ParseException e)
                    {
                        HttpResponse response = transferHandler.endTransfer(idConnection);
                        updateMessage(e.getMessage());
                        updateProgress(0,1);
                        e.printStackTrace();
                        throw e;
                    }
                }
                return true;
            }
        };
    }
}
