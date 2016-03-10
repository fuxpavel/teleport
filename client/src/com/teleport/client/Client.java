package com.teleport.client;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client
{
    private Authorization authorizationHandler;
    private Signing signingHandler;
    private Friendship friendshipHandler;
    private Sender sender = new Sender();

    public Client() throws IOException
    {
        authorizationHandler = new Authorization();
        signingHandler = new Signing();
        friendshipHandler = new Friendship(authorizationHandler);
    }

    public boolean register(String username, String password) throws IOException, ParseException
    {
        HttpResponse response = signingHandler.register(username, password);
        String body = EntityUtils.toString(response.getEntity());
        JSONObject json = (JSONObject) (new JSONParser().parse(body));
        return json.get("status").equals("success");
    }

    public boolean login(String username, String password) throws IOException, ParseException
    {
        HttpResponse response = signingHandler.login(username, password);
        String body = EntityUtils.toString(response.getEntity());
        JSONObject json = (JSONObject) (new JSONParser().parse(body));
        authorizationHandler.setToken(json.get("token").toString());
        return json.get("status").equals("success");
    }

    public List<String> getFriendRequests() throws IOException, ParseException
    {
        HttpResponse response = friendshipHandler.getFriendRequests();
        String body = EntityUtils.toString(response.getEntity());
        JSONArray arr = (JSONArray) new JSONParser().parse(body);
        ArrayList<String> friends = new ArrayList<>();
        for (Object obj: arr)
        {
            friends.add(obj.toString());
        }
        return friends;
    }

    public boolean addFriends(String friend) throws IOException, ParseException
    {
        HttpResponse response = friendshipHandler.addFriends(friend);
        String body = EntityUtils.toString(response.getEntity());
        JSONObject json = (JSONObject) (new JSONParser().parse(body));
        return json.get("status").equals("success");
    }

    public boolean respondToRequest(String friend, boolean status) throws IOException, ParseException
    {
        HttpResponse response = friendshipHandler.respondToRequest(friend, status);
        String body = EntityUtils.toString(response.getEntity());
        JSONObject json = (JSONObject) (new JSONParser().parse(body));
        return json.get("status").equals("success");
    }

    public boolean sendFile(String reciever, List<String> fileNames) throws IOException
    {
        Map<String, byte[]> contents = new HashMap<>();
        for (String fileName : fileNames)
        {
            Path path = Paths.get(fileName);
            contents.put(fileName, Files.readAllBytes(path));
        }
        sender.send(contents, reciever);
        return true;
    }
}
