package com.teleport.client;

import com.gargoylesoftware.htmlunit.Page;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class LoginTest
{
    private Login loginHandler;
    private JSONObject sendData;
    private JSONObject resData;

    @Before
    public void setUp() throws Exception
    {
        loginHandler = new Login();
        sendData = new JSONObject();
        sendData.put("username", "alex");
        sendData.put("password", "1234");
        Page res = loginHandler.post(sendData);
        String body = res.getWebResponse().getContentAsString();
        resData = (JSONObject) (new JSONParser().parse(body.toString()));
    }

    @Test
    public void testUsername() throws ParseException
    {
        assertEquals(sendData.get("username"), resData.get("username"));
    }

    @Test
    public void testPassword() throws ParseException
    {
        assertEquals(sendData.get("password"), resData.get("password"));
    }
}