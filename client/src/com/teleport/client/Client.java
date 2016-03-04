package com.teleport.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Client
{
    private Authorization authorizationHandler;
    private Register registerHandler;
    private Login loginHandler;
    private Friendship friendshipHandler;
    private HttpClient httpClient;

    public Client() throws IOException
    {
        authorizationHandler = new Authorization();
        registerHandler = new Register();
        loginHandler = new Login();
        friendshipHandler = new Friendship(authorizationHandler);
        httpClient = HttpClientBuilder.create().build();
    }

    public boolean register(String username, String password) throws IOException, ParseException
    {
        HttpResponse response = registerHandler.post(username, password);
        String body = EntityUtils.toString(response.getEntity());
        JSONObject json = (JSONObject) (new JSONParser().parse(body));
        return json.get("status").equals("success");
    }

    public boolean login(String username, String password) throws IOException, ParseException
    {
        HttpResponse response = loginHandler.post(username, password);
        String body = EntityUtils.toString(response.getEntity());
        JSONObject json = (JSONObject) (new JSONParser().parse(body));
        return json.get("status").equals("success");
    }

    private class Register
    {
        private final String ADDRESS = "127.0.0.1";
        private final String PORT = "8000";
        private final String SERVER_URL = "http://" + ADDRESS + ":" + PORT + "/api/register";

        public HttpResponse post(String username, String password) throws IOException
        {
            Map<String, String> map = new HashMap<>();
            map.put("username", username);
            map.put("password", password);
            JSONObject sendData = new JSONObject(map);

            HttpPost request = new HttpPost(SERVER_URL);
            StringEntity params = new StringEntity(sendData.toJSONString());
            request.setHeader("Content-Type", "application/json");
            request.setEntity(params);
            return httpClient.execute(request);
        }
    }

    private class Login
    {
        private final String ADDRESS = "127.0.0.1";
        private final String PORT = "8000";
        private final String SERVER_URL = "http://" + ADDRESS + ":" + PORT + "/api/login";

        public HttpResponse post(String username, String password) throws IOException
        {
            Map<String, String> map = new HashMap<>();
            map.put("username", username);
            map.put("password", password);
            JSONObject sendData = new JSONObject(map);

            HttpPost request = new HttpPost(SERVER_URL);
            StringEntity params = new StringEntity(sendData.toJSONString());
            request.setHeader("Content-Type", "application/json");
            request.setEntity(params);
            return httpClient.execute(request);
        }
    }
}
