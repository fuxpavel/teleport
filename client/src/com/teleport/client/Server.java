package com.teleport.client;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

class Server
{
    private Process server;

    public void start() throws IOException
    {
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(new File("teleport/server"));
        pb.command("gunicorn", "server");
        server = pb.start();
    }

    public boolean checkServer() throws InterruptedException
    {
        // attempt to connect to localhost:8000
        String ip = "127.0.0.1";
        int port = 8000;
        float timeout = 6; //in seconds
        long delay = 500; //in milliseconds
        long startTime = System.currentTimeMillis();
        boolean success = false;
        while (((System.currentTimeMillis() - startTime) < timeout) && !success)
        {
            success = true;
            try
            {
                Socket sock = new Socket(ip, port);
            }
            catch (IOException e)
            {
                success = false;
            }
            Thread.sleep(delay);
        }
        return success;
    }


    public void shutdown()
    {
        server.destroy();
    }
}
