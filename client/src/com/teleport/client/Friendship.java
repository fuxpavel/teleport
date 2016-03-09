package com.teleport.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
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

    public HttpResponse addFriends(List<String> friendList) throws IOException
    {
        return friendAdder.post(friendList);
    }

    public List<String> getFriendRequests() throws IOException, ParseException
    {
        return requestsRetriever.post();
    }

    private class FriendAdder
    {
        private static final String ADDRESS = "127.0.0.1";
        private static final String PORT = "8000";
        private static final String SERVER_URL = "http://" + ADDRESS + ":" + PORT + "/api/friendship/";

        public HttpResponse post(List<String> friendList) throws IOException
        {
            Map<String, List> map = new HashMap<>();
            map.put("friends", friendList);
            JSONObject sendData = new JSONObject(map);

            HttpPost request = new HttpPost(SERVER_URL);
            StringEntity params = new StringEntity(sendData.toJSONString());
            request.setHeader("Content-Type", "application/json");
            request.setEntity(params);
            return httpClient.execute(request);
        }
    }

    private class RequestRetriever
    {
        private static final String ADDRESS = "127.0.0.1";
        private static final String PORT = "8000";
        private static final String SERVER_URL = "http://" + ADDRESS + ":" + PORT + "/api/friendship";

        public List<String> post() throws IOException, ParseException
        {
            HttpGet request = new HttpGet(SERVER_URL);
            request.addHeader("Authorization", authorizationHandler.getToken());
            HttpResponse response = httpClient.execute(request);
            String body = EntityUtils.toString(response.getEntity());
            JSONObject json = (JSONObject) (new JSONParser().parse(body));
            ArrayList<String> friends = new ArrayList<>();
            for (int i = 0; i < json.size(); ++i)
            {
                friends.add(json.get(i).toString());
            }
            return friends;
        }
    }
}

