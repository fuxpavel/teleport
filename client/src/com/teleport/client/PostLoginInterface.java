package com.teleport.client;

import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.*;

public class PostLoginInterface
{
    //private static final String friendshipQuerierThreadName = "FriendshipQuerierThread";

    private Client client;
    //private FriendshipQuerier friendshipQuerier;

    public PostLoginInterface() throws IOException
    {
        client = new Client();
       // friendshipQuerier = new FriendshipQuerier(friendshipQuerierThreadName);
        //friendshipQuerier.start();
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

    public String send(String receiver, ProgressBar progressBar, Text lbl, List<String> paths) throws IOException, ParseException
    {
        if (client.sendFile(receiver, progressBar, lbl, paths))
        {
            return "Success";
        }
        else
        {
            return "Failure";
        }
    }

    public String receive(String sender, ProgressBar pbBar, Text lbl, boolean chose) throws IOException, ParseException
    {
        if (client.recvFile(sender, pbBar, lbl, chose))
        {
            return "Success";
        }
        else
        {
            return "Failure";
        }
    }
}