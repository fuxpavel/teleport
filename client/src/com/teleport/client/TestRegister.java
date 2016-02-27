package com.teleport.client;

import static org.junit.Assert.*;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

public class TestRegister
{
    private Register registerHandler;
    private JSONObject sendData;
    private JSONObject resData;

    @Before
    public void setUp() throws Exception
    {
        registerHandler = new Register();
        sendData = new JSONObject();
        sendData.put("username", "alex");
        sendData.put("password", "1234");
        Page res = registerHandler.post(sendData);
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