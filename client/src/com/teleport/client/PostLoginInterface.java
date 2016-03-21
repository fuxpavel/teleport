package com.teleport.client;

import asg.cliche.Command;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        Map<String, String> friends = client.getFriendRequests();
        List list = new ArrayList(friends.values());
        for(Object friend : list)
        {
            System.out.println(friend.toString().substring(1,friend.toString().length()-1));
        }

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
    public void send(String... paths) throws IOException
    {
        List<String> p = new ArrayList<String>();
        for (String path : paths)
        {
            p.add(path);
        }
        Client client = new Client();
        client.sendFile(p);
    }

    @Command
    public void receive(String sender) throws IOException, ParseException
    {
        Client client = new Client();
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