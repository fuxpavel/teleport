package com.teleport.client;

import asg.cliche.Command;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

public class PostLoginInterface
{
    private static final String friendshipQuerierThreadName = "FriendshipQuerierThread";
    private static final String transferQuerierThreadName = "TransferQuerierThread";

    private Client client;
    private FriendshipQuerier friendshipQuerier;
    private TransferQuerier transferQuerier;

    public PostLoginInterface() throws IOException
    {
        client = new Client();
        friendshipQuerier = new FriendshipQuerier(friendshipQuerierThreadName);
        friendshipQuerier.start();
        transferQuerier = new TransferQuerier(transferQuerierThreadName);
        transferQuerier.start();
    }

    public void getFriendRequests() throws IOException, ParseException
    {
        List<String> incoming = client.getIncomingFriendRequests();
        List<String> outgoing = client.getOutgoingFriendRequests();
        System.out.println("incoming: " + incoming);
        System.out.println("outgoing: " + outgoing);
    }

    public String addFriend(String friend) throws IOException, ParseException
    {
        if (client.addFriend(friend))
        {
            return "Request sent";
        }
        else
        {
            return "Couldn't send request";
        }
    }

    public String respondToRequest(String friend, boolean status) throws IOException, ParseException
    {
        if (client.respondToRequest(friend, status))
        {
            return "Responded successfully";
        }
        else
        {
            return "Couldn't respond";
        }
    }


    public List<String> getFriends() throws IOException, ParseException
    {
        return client.getFriends();
    }

    public List<String> getUsername(String name) throws IOException, ParseException
    {
        return client.getUsernameList(name);
    }


    public String send(String receiver, String... paths) throws IOException, ParseException
    {
        List<String> p = new ArrayList<>();
        for (String path : paths)
        {
            p.add(path);
        }
        if (client.sendFile(receiver, p))
        {
             return "Success";
        }
        else
        {
            return "Failure";
        }
    }

    public String receive(String sender) throws IOException, ParseException
    {
        if (client.recvFile(sender))
        {
            return "Success";
        }
        else
        {
            return "Failure";
        }
    }
}