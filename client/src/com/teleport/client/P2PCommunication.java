package com.teleport.client;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Optional;

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
    private String idConnection;
    private Transfer transferHandler;
    private String receiver;
    private Task copyWorker;
    private boolean returnVal;
    boolean chose;
    Socket sock;
    ServerSocket serverSock;
    InputStream in;
    OutputStream out;
    int amout_of_files;
    ProgressBar pbBar;
    Text lbl;
    private List<Path> delete;


    public P2PCommunication(String recv, List<String> path, Transfer transfer, String ip_recv)
    {
        //sender
        paths = path;
        currentSize = 0;
        receiver = recv;
        transferHandler = transfer;
        ip = ip_recv;
    }

    public P2PCommunication(String recv, String send, Transfer transfer)
    {
        //receiver
        ip = send;
        receiver = recv;
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
    public Task deleteFiles()
    {
        return new Task()
        {
            @Override
            protected Object call() throws InterruptedException
            {
                for(Path path1 : delete)
                {
                    try
                    {
                        try
                        {
                            Files.delete(path1);
                        }
                        catch (FileNotFoundException e1)
                        {
                            lbl.setText("File not found");
                        }
                        catch (FileSystemException e1)
                        {
                            lbl.setText("Use by other process");
                        }
                    }
                    catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
                return true;
            }
        };
    }
    public Task createWorker()
    {
        return new Task()
        {
            @Override
            protected Object call()
            {
                //sender
                if (paths != null)
                {
                    try
                    {
                        HttpResponse response = transferHandler.beginTransfer(receiver);
                        delete = new ArrayList<>();
                        int count;
                        float percent;
                        BufferedInputStream in1;
                        byte[] buf;
                        try
                        {
                            serverSock = new ServerSocket(PORT);
                            sock = serverSock.accept();
                            String ipRem = sock.getRemoteSocketAddress().toString().split(":")[0].replace("/", "");
                            if (ipRem.equals(ip))
                            {
                                String body = EntityUtils.toString(response.getEntity());
                                JSONObject json = (JSONObject) (new JSONParser().parse(body));
                                if (json.get("status").equals("success"))
                                {
                                    idConnection = json.get("id").toString();
                                    in = sock.getInputStream();
                                    out = sock.getOutputStream();
                                    out.write((P2P_CONNECT_REQUEST + ":" + P2P_AMOUT_OF_FILES + ":" + paths.size() + ":" + idConnection + "::").getBytes("UTF-8"));
                                    out.flush();
                                    int amout = paths.size();
                                    for (String path : paths)
                                    {
                                        currentSize = 0;
                                        fileSize = 0;
                                        updateMessage("zipping...");
                                        String compress = Compress.Compression(path);
                                        updateMessage("ready");
                                        File myFile = new File(compress);
                                        delete.add(myFile.toPath());
                                        fileName = compress.substring(compress.lastIndexOf("\\") + 1);
                                        fileSize = (int) myFile.length();
                                        in1 = new BufferedInputStream(new FileInputStream(myFile));
                                        buf = new byte[BUF_SIZE];
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
                                                updateMessage("send " + GetFileName() + " to " + receiver + " | " + String.format("%.0f", percent * 100) + "%");
                                                out.flush();
                                            }
                                        }
                                        else
                                        {
                                            updateProgress(0, 1);
                                        }
                                        amout--;
                                        sock.close();
                                        in.close();
                                        out.close();
                                        out = null;
                                        myFile.delete();
                                        System.gc();
                                        if (amout > 0)
                                        {
                                            sock = serverSock.accept();
                                            in = sock.getInputStream();
                                            out = sock.getOutputStream();
                                        }
                                    }
                                    updateMessage("Done!");
                                    serverSock.close();
                                }
                            }
                        }
                        catch (IOException e)
                        {
                            updateMessage(e.getMessage());
                            e.printStackTrace();
                        }
                        catch (ParseException e)
                        {
                            updateMessage(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    catch (IOException e)
                    {
                        updateMessage(e.getMessage());
                        e.printStackTrace();
                    }
                }
                else
                {
                    //receiver
                    byte[] buf = new byte[BUF_SIZE];
                    int len;
                    currentSize = 0;
                    try
                    {
                        Authorization authorizationHandler = new Authorization();
                        try
                        {
                            if(chose)
                            {
                                out.write((P2P_ANS_CONNECT_REQUEST + ":" + P2P_POSITIVE_ANS + "::").getBytes());
                                out.flush();
                                String location = authorizationHandler.getPath() + "\\" + fileName;
                                FileOutputStream fos = new FileOutputStream(location);
                                float percent;
                                while ((len = in.read(buf)) > 0)
                                {
                                    currentSize = currentSize + len;
                                    fos.write(buf, 0, len);
                                    updateProgress(GetCurrentSize(), GetFileSize());
                                    percent = (float) GetCurrentSize() / (float) GetFileSize();
                                    updateMessage("receive " + GetFileName() + " from " + receiver + " | " + String.format("%.0f", percent * 100) + "%");
                                }
                                updateProgress(GetFileSize(), GetFileSize());
                                updateMessage("receive " + GetFileName() + " from " + receiver + " | 100%");
                                fos.close();
                                if (authorizationHandler.getOpen())
                                {
                                    Runtime.getRuntime().exec("explorer.exe /select," + location);
                                }
                            }
                            else
                            {
                                out.write((P2P_ANS_CONNECT_REQUEST + ":" + P2P_REFUSE_ANS + "::").getBytes());
                                out.flush();
                                updateProgress(0, 1);
                            }
                            RestartConnection();
                        }
                        catch (IOException e)
                        {
                            updateMessage(e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    catch (IOException e)
                    {
                        updateMessage(e.getMessage());
                        e.printStackTrace();
                    }
                }
                return true;
            }
        };
    }

    public void RestartConnection() throws IOException
    {
        String[] input;
        byte[] buf = new byte[BUF_SIZE];
        sock.close();
        in.close();
        out.close();
        if (amout_of_files > 0)
        {
            sock = new Socket(ip, PORT);
            in = sock.getInputStream();
            in.read(buf);
            input = new String(buf, StandardCharsets.UTF_8).split(":");
            if (input[0].equals(P2P_SEND_FILE))
            {
                fileName = input[1];
                fileSize = sizeToInt(input[2]);
                out = sock.getOutputStream();
                Platform.runLater(() -> AlertToClient());
            }
        }
        if (amout_of_files == 0)
        {
            transferHandler.endTransfer(idConnection);
        }
    }

    public void AlertToClient()
    {
        Alert alert = new Alert(Alert.AlertType.NONE, receiver + " want send you " + fileName + " " + sizeToString(fileSize) + " do you want to get it?", ButtonType.APPLY, ButtonType.CANCEL);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.APPLY)
        {
            chose = true;
        }
        else
        {
            chose = false;
        }
        amout_of_files--;
        copyWorker = this.createWorker();
        pbBar.progressProperty().unbind();
        pbBar.setStyle("-fx-accent: blue;");
        pbBar.progressProperty().bind(copyWorker.progressProperty());
        lbl.textProperty().bind(copyWorker.messageProperty());
        copyWorker.setOnSucceeded(e -> Platform.runLater(() ->
        {
            lbl.textProperty().unbind();
            pbBar.progressProperty().unbind();
            pbBar.setStyle("-fx-accent: green;");
        }));
        copyWorker.setOnFailed(e -> Platform.runLater(() ->
        {
            pbBar.progressProperty().unbind();
            pbBar.setStyle("-fx-accent: red;");
        }));
        new Thread(copyWorker).start();
    }

    public boolean runSender(ProgressBar pbBar, Text lbl)
    {
        copyWorker = this.createWorker();
        Platform.runLater(() -> {
            pbBar.setStyle("-fx-accent: blue;");
            pbBar.progressProperty().bind(copyWorker.progressProperty());
            lbl.textProperty().bind(copyWorker.messageProperty());
        });
        new Thread(copyWorker).start();
        copyWorker.setOnSucceeded(e ->
        {
            returnVal = true;
            Platform.runLater(() -> {
                lbl.textProperty().unbind();
                pbBar.setStyle("-fx-accent: green;");
                System.gc();
                copyWorker = this.deleteFiles();
                new Thread(copyWorker).start();
                /*
                for(Path path1 : delete)
                {
                    try
                    {
                        try
                        {
                            Files.delete(path1);
                        }
                        catch (FileNotFoundException e1)
                        {
                            lbl.setText("File not found");
                        }
                        catch (FileSystemException e1)
                        {
                            lbl.setText("Use by other process");
                        }
                    }
                    catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
                */
            });
        });
        copyWorker.setOnFailed(e ->
        {
            returnVal = false;
            Platform.runLater(() -> {
                lbl.textProperty().unbind();
                lbl.setText("");
                pbBar.progressProperty().unbind();
                pbBar.setStyle("-fx-accent: red;");
            });
        });
        return returnVal;
    }

    public boolean runReceiver(ProgressBar pbBar, Text lbl)
    {
        this.lbl = lbl;
        this.pbBar = pbBar;
        String[] input;
        byte[] buf = new byte[BUF_SIZE];
        try
        {
            sock = new Socket(ip, PORT);
            in = sock.getInputStream();
            out = sock.getOutputStream();
            in.read(buf);
            input = new String(buf, StandardCharsets.UTF_8).split(":");
            if (input[0].equals(P2P_CONNECT_REQUEST) && input[1].equals(P2P_AMOUT_OF_FILES))
            {
                amout_of_files = Integer.parseInt(input[2]);
                idConnection = input[3];
                in.read(buf);
                input = new String(buf, StandardCharsets.UTF_8).split(":");
                if (input[0].equals(P2P_SEND_FILE))
                {
                    fileName = input[1];
                    fileSize = sizeToInt(input[2]);
                    Platform.runLater(() -> AlertToClient());
                }
            }
        }
        catch (java.net.SocketException e)
        {
            Platform.runLater(() ->
            {
                lbl.textProperty().unbind();
                lbl.setText("SocketException");
            });
            e.printStackTrace();
        }
        catch (UnknownHostException e)
        {
            Platform.runLater(() ->
            {
                lbl.textProperty().unbind();
                lbl.setText("UnknownHostException");
            });
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Platform.runLater(() ->
            {
                lbl.textProperty().unbind();
                lbl.setText("IOException");
            });
            e.printStackTrace();
        }
        return returnVal;
    }
}