package sample;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

public class Client
{
    private Authorization authorizationHandler;
    private Signing signingHandler;
    private Friendship friendshipHandler;
    private Friendship ipHandler;
    private Sender sender = new Sender();
    private Receiver recv = new Receiver();

    public Client() throws IOException
    {
        authorizationHandler = new Authorization();
        signingHandler = new Signing();
        friendshipHandler = new Friendship(authorizationHandler);
        ipHandler = new Friendship(authorizationHandler);
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

    public Map<String, List<String>> getFriendRequests() throws IOException, ParseException
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


    public boolean addFriends(String friend) throws IOException, ParseException
    {
        HttpResponse response = friendshipHandler.addFriends(friend);
        String body = EntityUtils.toString(response.getEntity());
        JSONObject json = (JSONObject) (new JSONParser().parse(body));
        return json.get("status").equals("success");
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
        HttpResponse response = ipHandler.getSenderIP(sender);
        String body = EntityUtils.toString(response.getEntity());
        JSONParser p = new JSONParser();
        JSONObject jsonObject = (JSONObject) p.parse(body);
        return (String) jsonObject.get("msg");
    }

    public boolean sendFile(List<String> paths) throws IOException
    {
        sender.send(paths);
        return true;
    }

    public boolean recvFile(String ip) throws IOException
    {
        recv.receive(ip);
        return true;
    }
}