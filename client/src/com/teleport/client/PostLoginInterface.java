package com.teleport.client;

import asg.cliche.Command;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PostLoginInterface
{
    private Client client;

    public PostLoginInterface() throws IOException
    {
        client = new Client();
    }

    @Command
    public void getFriendRequests() throws IOException, ParseException
    {
        List<String> friends = client.getFriendRequests();
        friends.forEach(System.out::println);
    }

    @Command
    public void addFriend(String friend) throws IOException, ParseException
    {
        if (client.addFriends(friend))
        {
            System.out.println("Request sent");
        }
        else
        {
            System.out.println("Couldn't send request");
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
}