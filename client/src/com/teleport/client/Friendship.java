package com.teleport.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.teleport.client.ServerInfo.ADDRESS;
import static com.teleport.client.ServerInfo.PORT;

public class Friendship
{
    private FriendsAdder friendsAdder;
    private RequestRetriever requestsRetriever;
    private RequestResponder requestResponder;
    private FriendsRetriever friendsRetriever;
    private HttpClient httpClient;
    private Authorization authorizationHandler;
    private SwitchIP senderIP;
    private List<String> friends;

    public Friendship(Authorization authorizationHandler) throws IOException
    {
        httpClient = HttpClientBuilder.create().build();
        this.authorizationHandler = authorizationHandler;
        friendsAdder = new FriendsAdder();
        requestsRetriever = new RequestRetriever();
        requestResponder = new RequestResponder();
        friendsRetriever = new FriendsRetriever();
        senderIP = new SwitchIP();
    }

    public HttpResponse addFriend(String friend) throws IOException
    {
        return friendsAdder.post(friend);
    }

    public HttpResponse getFriendRequests() throws IOException
    {
        return requestsRetriever.get();
    }

    public HttpResponse respondToRequest(String friend, boolean status) throws IOException
    {
        return requestResponder.post(friend, status);
    }

    public HttpResponse getFriends() throws IOException
    {
        return friendsRetriever.get();
    }

    public HttpResponse getSenderIP(String sender) throws IOException
    {
        return senderIP.post(sender);
    }

    private class FriendsAdder
    {
        private static final String SERVER_URL = "http://" + ADDRESS + ":" + PORT + "/api/friendship/";

        public HttpResponse post(String friend) throws IOException
        {
            Map<String, String> map = new HashMap<>();
            map.put("reply", friend);
            JSONObject sendData = new JSONObject(map);

            HttpPost request = new HttpPost(SERVER_URL);
            request.addHeader("Authorization", authorizationHandler.getToken());
            request.setHeader("Content-Type", "application/json");
            StringEntity params = new StringEntity(sendData.toJSONString());
            request.setEntity(params);
            return httpClient.execute(request);
        }
    }

    private class RequestResponder
    {
        private static final String SERVER_URL = "http://" + ADDRESS + ":" + PORT + "/api/friendship/response";

        public HttpResponse post(String friend, boolean status) throws IOException
        {
            Map<String, String> map = new HashMap<>();
            map.put("reply", friend);
            map.put("status", status ? "confirm" : "denial");
            JSONObject sendData = new JSONObject(map);

            HttpPost request = new HttpPost(SERVER_URL);
            request.addHeader("Authorization", authorizationHandler.getToken());
            request.setHeader("Content-Type", "application/json");
            StringEntity params = new StringEntity(sendData.toJSONString());
            request.setEntity(params);
            return httpClient.execute(request);
        }
    }

    private class RequestRetriever
    {
        private static final String SERVER_URL = "http://" + ADDRESS + ":" + PORT + "/api/friendship/response";

        public HttpResponse get() throws IOException
        {
            HttpGet request = new HttpGet(SERVER_URL);
            request.addHeader("Authorization", authorizationHandler.getToken());
            return httpClient.execute(request);
        }
    }

    private class FriendsRetriever
    {
        private static final String SERVER_URL = "http://" + ADDRESS + ":" + PORT + "/api/friendship";

        public HttpResponse get() throws IOException
        {
            HttpGet request = new HttpGet(SERVER_URL);
            request.addHeader("Authorization", authorizationHandler.getToken());
            return httpClient.execute(request);
        }
    }

    private class SwitchIP
    {
        private static final String SERVER_URL = "http://" + ADDRESS + ":" + PORT + "/api/switch-ip";

        public HttpResponse post(String sender) throws IOException
        {
            Map<String, String> map = new HashMap<>();
            map.put("sender", sender);
            JSONObject sendData = new JSONObject(map);
            HttpPost request = new HttpPost(SERVER_URL);
            request.addHeader("Authorization", authorizationHandler.getToken());
            request.setHeader("Content-Type", "application/json");
            StringEntity params = new StringEntity(sendData.toJSONString());
            request.setEntity(params);
            return httpClient.execute(request);
        }
    }
}