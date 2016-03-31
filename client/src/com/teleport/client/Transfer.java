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
import java.util.Map;

import static com.teleport.client.ServerInfo.ADDRESS;
import static com.teleport.client.ServerInfo.PORT;

public class Transfer
{
    private TransferTracker transferTracker;
    private TransferRetriever transferRetriever;
    private HttpClient httpClient;
    private Authorization authorizationHandler;
    private SwitchIP switchIP;

    public Transfer(Authorization authorizationHandler) throws IOException
    {
        httpClient = HttpClientBuilder.create().build();
        this.authorizationHandler = authorizationHandler;
        transferTracker = new TransferTracker();
        transferRetriever = new TransferRetriever();
        switchIP = new SwitchIP();
    }

    public HttpResponse beginTransfer(String receiver) throws IOException
    {
        return transferTracker.postBegin(receiver);
    }

    public HttpResponse endTransfer(String receiver) throws IOException
    {
        return transferTracker.postEnd(receiver);
    }

    public HttpResponse getTransfers() throws IOException
    {
        return transferRetriever.get();
    }

    public HttpResponse getSenderIP(String sender) throws IOException
    {
        return switchIP.post(sender);
    }

    private class TransferTracker
    {
        private static final String SERVER_URL = "http://" + ADDRESS + ":" + PORT + "/api/transfer/";

        public HttpResponse postBegin(String receiver) throws IOException
        {
            Map<String, String> map = new HashMap<>();
            map.put("user", receiver);
            map.put("action", "begin");
            JSONObject sendData = new JSONObject(map);

            HttpPost request = new HttpPost(SERVER_URL);
            request.addHeader("Authorization", authorizationHandler.getToken());
            request.setHeader("Content-Type", "application/json");
            StringEntity params = new StringEntity(sendData.toJSONString());
            request.setEntity(params);
            return httpClient.execute(request);
        }

        public HttpResponse postEnd(String receiver) throws IOException
        {
            Map<String, String> map = new HashMap<>();
            map.put("user", receiver);
            map.put("action", "end");
            JSONObject sendData = new JSONObject(map);

            HttpPost request = new HttpPost(SERVER_URL);
            request.addHeader("Authorization", authorizationHandler.getToken());
            request.setHeader("Content-Type", "application/json");
            StringEntity params = new StringEntity(sendData.toJSONString());
            request.setEntity(params);
            return httpClient.execute(request);
        }
    }

    private class TransferRetriever
    {
        private static final String SERVER_URL = "http://" + ADDRESS + ":" + PORT + "/api/transfer";

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
