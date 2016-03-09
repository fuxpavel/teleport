package com.teleport.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Friendship
{
    private FriendAdder friendAdder;
    private RequestRetriever requestsRetriever;
    private HttpClient httpClient;
    private Authorization authorizationHandler;

    public Friendship(Authorization authorizationHandler) throws IOException
    {
        httpClient = HttpClientBuilder.create().build();
        this.authorizationHandler = authorizationHandler;
        friendAdder = new FriendAdder();
        requestsRetriever = new RequestRetriever();
    }

    public HttpResponse addFriends(String friend) throws IOException
    {
        return friendAdder.post(friend);
    }

    public List<String> getFriendRequests() throws IOException, ParseException
    {
        return requestsRetriever.get();
    }

    private class FriendAdder
    {
        private static final String ADDRESS = "127.0.0.1";
        private static final String PORT = "8000";
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
        private static final String ADDRESS = "127.0.0.1";
        private static final String PORT = "8000";
        private static final String SERVER_URL = "http://" + ADDRESS + ":" + PORT + "/api/friendship/response"

        public void post(String friend, boolean status) throws IOException
        {
            Map<String, String> map = new HashMap<>();
            map.put("reply", friend);
            map.put("status", status ? "confirn" : "denial");
            JSONObject sendData = new JSONObject(map);

            HttpPost request = new HttpPost(SERVER_URL);
            request.addHeader("Authorization", authorizationHandler.getToken());
            request.setHeader("Content-Type", "application/json");
            StringEntity params = new StringEntity(sendData.toJSONString());
            request.setEntity(params);
            httpClient.execute(request);
        }
    }

    private class RequestRetriever
    {
        private static final String ADDRESS = "127.0.0.1";
        private static final String PORT = "8000";
        private static final String SERVER_URL = "http://" + ADDRESS + ":" + PORT + "/api/friendship";

        public List<String> get() throws IOException, ParseException
        {
            HttpGet request = new HttpGet(SERVER_URL);
            request.addHeader("Authorization", authorizationHandler.getToken());
            HttpResponse response = httpClient.execute(request);
            String body = EntityUtils.toString(response.getEntity());
            JSONArray arr = (JSONArray) new JSONParser().parse(body);
            ArrayList<String> friends = new ArrayList<>();
            for (Object obj: arr)
            {
                friends.add(obj.toString());
            }
            return friends;
        }
    }
}

