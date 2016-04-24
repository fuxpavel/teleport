package com.teleport.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Authorization
{
    private String token = null;
    private static final String fileName = "conf.txt";
    private String path;
    private boolean zip;
    private boolean open;
    private int timeout;

    public Authorization() throws IOException
    {
        Path path = Paths.get(fileName);
        if (Files.exists(path))
        {
            String info = new String(Files.readAllBytes(path), "UTF-8");

            Map<String, String> myMap = new HashMap<>();
            String[] pairs = info.split("\n");
            for (int i = 0; i < pairs.length; i++)
            {
                String pair = pairs[i];
                String[] keyValue = pair.split("\"");
                myMap.put(keyValue[0], keyValue[1]);
                if (keyValue[0].equals("zip"))
                {
                    this.zip = Boolean.parseBoolean(keyValue[1]);
                }
                else if (keyValue[0].equals("path"))
                {
                    this.path = keyValue[1];
                }
                else if (keyValue[0].equals("token"))
                {
                    this.token = keyValue[1];
                }
                else if (keyValue[0].equals("open"))
                {
                    this.open = Boolean.parseBoolean(keyValue[1]);
                }
                else if(keyValue[0].equals("timeout"))
                {
                    this.timeout = Integer.parseInt(keyValue[1]);
                }
            }
        }
    }

    public void setToken(String newToken) throws IOException
    {
        Path file = Paths.get(fileName);
        if (Files.exists(file))
        {
            String content = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            String oldToken = this.token;
            this.token = newToken;
            content = content.replaceAll("token\"" + oldToken, "token\"" + this.token);
            Files.write(file, content.getBytes(StandardCharsets.UTF_8));
        }
    }

    public void setPath(String newPath) throws IOException
    {
        Path file = Paths.get(fileName);
        if (Files.exists(file))
        {
            String content = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            String oldPath = this.getPath();
            this.path = newPath;
            content = content.replace("path\"" + oldPath, "path\"" + this.path);
            Files.write(file, content.getBytes(StandardCharsets.UTF_8));
        }
    }

    public void setZip(boolean newZip) throws IOException
    {
        Path file = Paths.get(fileName);
        if (Files.exists(file))
        {
            String content = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            boolean oldZip = this.zip;
            this.zip = newZip;
            content = content.replaceAll("zip\"" + oldZip, "zip\"" + this.zip);
            Files.write(file, content.getBytes(StandardCharsets.UTF_8));
        }
    }

    public void setOpen(boolean newOpen) throws IOException
    {
        Path file = Paths.get(fileName);
        if (Files.exists(file))
        {
            String content = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            boolean oldOpen = this.open;
            this.open = newOpen;
            content = content.replaceAll("open\"" + oldOpen, "open\"" + this.open);
            Files.write(file, content.getBytes(StandardCharsets.UTF_8));
        }
    }
    public void setTimeout(int newTimeout) throws IOException
    {
        Path file = Paths.get(fileName);
        if (Files.exists(file))
        {
            String content = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            int oldTimeout = this.timeout;
            this.timeout = newTimeout;
            content = content.replaceAll("timeout\"" + oldTimeout, "timeout\"" + this.timeout);
            Files.write(file, content.getBytes(StandardCharsets.UTF_8));
        }
    }

    public int getTimeout()
    {
        return this.timeout;
    }

    public boolean getOpen()
    {
        return this.open;
    }

    public String getToken()
    {
        return this.token;
    }

    public String getPath()
    {
        return this.path;
    }

    public boolean getZip()
    {
        return this.zip;
    }
}
