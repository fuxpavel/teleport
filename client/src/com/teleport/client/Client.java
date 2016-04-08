package com.teleport.client;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client
{
    private Authorization authorizationHandler;
    private Signing signingHandler;
    private Friendship friendshipHandler;
    private Transfer transferHandler;
    //private Sender sender = new Sender();
    private Receiver recv = new Receiver();

    public Client() throws IOException
    {
        authorizationHandler = new Authorization();
        signingHandler = new Signing();
        friendshipHandler = new Friendship(authorizationHandler);
        transferHandler = new Transfer(authorizationHandler);
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

    private Map<String, List<String>> getFriendRequests() throws IOException, ParseException
    {
        HttpResponse response = friendshipHandler.getFriendRequests();
        String body = EntityUtils.toString(response.getEntity());
        Map<String, List<String>> map = new HashMap<>();
        JSONObject json = (JSONObject) JSONValue.parse(body);

        for (Object key : json.keySet())
        {
            map.put((String) key, (List<String>) json.get(key));
        }

        return map;
    }

    public List<String> getIncomingFriendRequests() throws IOException, ParseException
    {
        return getFriendRequests().get("incoming");
    }

    public List<String> getOutgoingFriendRequests() throws IOException, ParseException
    {
        return getFriendRequests().get("outgoing");
    }

    public List<String> getUsernameList(String name) throws IOException, ParseException
    {
        HttpResponse response = friendshipHandler.getUsernameList(name);
        String body = EntityUtils.toString(response.getEntity());
        JSONArray arr = (JSONArray) new JSONParser().parse(body);
        ArrayList<String> username = new ArrayList<>();
        for (Object obj : arr)
        {
            username.add(obj.toString());
        }
        return username;
    }

    private Map<String, List<String>> getTransfers() throws IOException, ParseException
    {
        HttpResponse response = transferHandler.getTransfers();
        String body = EntityUtils.toString(response.getEntity());
        Map<String, List<String>> map = new HashMap<>();
        JSONObject json = (JSONObject) JSONValue.parse(body);
        for (Object key : json.keySet())
        {
            map.put((String) key, (List<String>) json.get(key));
        }

        return map;
    }

    public List<String> getIncomingTransfers() throws IOException, ParseException
    {
        return getTransfers().get("incoming");
    }

    public List<String> getOutgoingTransfers() throws IOException, ParseException
    {
        return getTransfers().get("outgoing");
    }

    public boolean addFriend(String friend) throws IOException, ParseException
    {
        HttpResponse response = friendshipHandler.addFriend(friend);
        String body = EntityUtils.toString(response.getEntity());
        JSONObject json = (JSONObject) (new JSONParser().parse(body));
        return json.get("status").equals("success");
    }

    public List<String> getFriends() throws IOException, ParseException
    {
        HttpResponse response = friendshipHandler.getFriends();
        String body = EntityUtils.toString(response.getEntity());
        JSONArray arr = (JSONArray) new JSONParser().parse(body);
        ArrayList<String> friends = new ArrayList<>();
        for (Object obj : arr)
        {
            friends.add(obj.toString());
        }
        return friends;
    }

    public boolean respondToRequest(String friend, boolean status) throws IOException, ParseException
    {
        HttpResponse response = friendshipHandler.respondToRequest(friend, status);
        String body = EntityUtils.toString(response.getEntity());
        JSONObject json = (JSONObject) (new JSONParser().parse(body));
        return json.get("status").equals("success");
    }

    public String get_sender_ip(String sender) throws IOException, ParseException
    {
        HttpResponse response = transferHandler.getSenderIP(sender);
        String body = EntityUtils.toString(response.getEntity());
        JSONObject json = (JSONObject) (new JSONParser().parse(body));
        return ((String) json.get("ip"));
    }

    public boolean sendFile(String receiver, ProgressBar pbBar,Text lbl, List<String> paths) throws IOException, ParseException
    {
        HttpResponse response = transferHandler.beginTransfer(receiver);
        String body = EntityUtils.toString(response.getEntity());
        JSONObject json = (JSONObject) (new JSONParser().parse(body));
        if (json.get("status").equals("success"))
        {
            SendFiles sender = new SendFiles(paths);
            sender.start();
            pbBar.setProgress(0);
            lbl.setText("");
            pbBar.setStyle("-fx-accent: blue;");
            new ProgressBarSend(receiver, sender,lbl, pbBar).start();
            //transferHandler.endTransfer(receiver);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean recvFile(String sender, ProgressBar pbBar,Text lbl, boolean chose) throws IOException, ParseException
    {
        String ip = get_sender_ip(sender);
        if (!ip.equals("failure"))
        {
            SendFiles receuver = new SendFiles(ip, chose);
            receuver.start();
            pbBar.setProgress(0);
            lbl.setText("");
            pbBar.setStyle("-fx-accent: blue;");
            new ProgressBarSend(" ", receuver,lbl, pbBar).start();
            //recv.receive(get_sender_ip(sender), chose);
            return true;
        }
        else
        {
            return false;
        }
    }
}