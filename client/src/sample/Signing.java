package sample;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
 
import static sample.ServerInfo.*;

public class Signing
{
    private Register registerHandler;
    private Login loginHandler;
    private HttpClient httpClient;

    public Signing()
    {
        registerHandler = new Register();
        loginHandler = new Login();
        httpClient = HttpClientBuilder.create().build();
    }

    public HttpResponse register(String username, String password) throws IOException
    {
        return registerHandler.post(username, password);
    }


    public HttpResponse login(String username, String password) throws IOException
    {
        return loginHandler.post(username, password);
    }

    private class Register
    {
        private static final String SERVER_URL = "http://" + ADDRESS + ":" + PORT + "/api/register";

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
        private static final String SERVER_URL = "http://" + ADDRESS + ":" + PORT + "/api/login";

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
