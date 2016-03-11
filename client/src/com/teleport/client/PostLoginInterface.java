package com.teleport.client;

import asg.cliche.Command;
import org.json.simple.parser.ParseException;
import java.io.IOException;
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
    public void respondToRequest(String friend, boolean status) throws IOException, ParseException
    {
        if (client.respondToRequest(friend, status))
        {
            System.out.println("Responded successfully");
        }
        else
        {
            System.out.println("Couldn't respond");
        }
    }

    @Command
    public void getFriends() throws IOException, ParseException
    {
        List<String> friends = client.getFriends();
        friends.forEach(System.out::println);
    }

    @Command
    public void send(String path) throws IOException
    {
        Client client = new Client();
        client.sendFile(path);
    }

    @Command
    public void receive(String sender) throws IOException, ParseException
    {
        Client client = new Client();
        String ip = client.get_sender_ip(sender);
        client.recvFile(ip);
    }
}