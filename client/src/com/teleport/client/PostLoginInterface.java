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

    @Command
    public void getFriendRequests() throws IOException, ParseException
    {
        List<String> incoming = client.getIncomingFriendRequests();
        List<String> outgoing = client.getOutgoingFriendRequests();
        System.out.println("incoming: " + incoming);
        System.out.println("outgoing: " + outgoing);
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
    public void send(String receiver, String... paths) throws IOException, ParseException
    {
        List<String> p = new ArrayList<>();
        for (String path : paths)
        {
            p.add(path);
        }
        if (client.sendFile(receiver, p))
        {
            System.out.println("Success");
        }
        else
        {
            System.out.println("Failure");
        }
    }

    @Command
    public void receive(String sender) throws IOException, ParseException
    {
        if (client.recvFile(sender))
        {
            System.out.println("Success");
        }
        else
        {
            System.out.println("Failure");
        }
    }
}