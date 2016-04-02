package com.teleport.client;

import asg.cliche.Command;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

public class PostLoginInterface
{
    private static final String friendshipQuerierThreadName = "FriendshipQuerierThread";

    private Client client;
    private FriendshipQuerier friendshipQuerier;

    public PostLoginInterface() throws IOException
    {
        client = new Client();
        friendshipQuerier = new FriendshipQuerier(friendshipQuerierThreadName);
        friendshipQuerier.start();
    }

    @Command
    public void getFriendRequests() throws IOException, ParseException
    {
        Map<String, List<String>> friends = client.getFriendRequests();
        Iterator t = friends.values().iterator();
        for (String key : friends.keySet())
        {
            System.out.println(key.toString()+": "+t.next().toString());
        }
    }

    @Command
    public void addFriend(String friend) throws IOException, ParseException
    {
        if (client.addFriend(friend))
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
    public void send(String... paths) throws IOException
    {
        List<String> p = new ArrayList<String>();
        for (String path : paths)
        {
            p.add(path);
        }
        client.sendFile(p);
    }

    @Command
    public void receive(String sender) throws IOException, ParseException
    {
        String ip = client.get_sender_ip(sender);
        if (!ip.equals("not friends"))
        {
            client.recvFile(ip);
        }
        else
        {
            System.out.println("not friends");
        }
    }
}