package com.teleport.client;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Authorization
{
    private String token = null;
    private static final String fileName = "token.txt";

    public Authorization() throws IOException
    {
        Path path = Paths.get(fileName);
        if (Files.exists(path))
        {
            token = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        }
    }

    public void setToken(String token) throws IOException
    {
        this.token = token;

        Path path = Paths.get(fileName);
        if (!Files.exists(path))
        {
            Files.createFile(path);
        }
        Files.write(path, token.getBytes());
    }

    public String getToken()
    {
        return token;
    }
}
