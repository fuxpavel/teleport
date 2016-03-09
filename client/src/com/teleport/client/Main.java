package com.teleport.client;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main
{
    @Command
    public String register(String username, String password) throws IOException, ParseException
    {
        Client client = new Client();
        if (client.register(username, password))
        {
            return "Registered successfully";
        }
        else
        {
            return "Could not register";
        }

    }

    @Command
    public String login(String username, String password) throws IOException, ParseException
    {
        Client client = new Client();
        if (client.login(username, password))
        {
            return "Logged in successfully";
        }
        else
        {
            return "Could not login";
        }
    }


    @Command
    public void send(String reciever, String dir) throws IOException
    {
        Client client = new Client();
        List<String> files = new ArrayList<>();
        Files.walk(Paths.get(dir)).forEach(path -> {
            if (Files.isRegularFile(path))
            {
                files.add(path.toString());
            }
        });
        client.sendFile(reciever, files);
    }

    @Command
    public void recieve(String sender)
    {

    }

    public static void main(String[] args) throws IOException
    {
        ShellFactory.createConsoleShell("$", "teleport-client", new Main()).commandLoop();
    }
}
