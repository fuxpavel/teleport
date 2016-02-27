package com.teleport.client;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.json.simple.JSONObject;

public class Client
{
    private Register registerHandler;
    private Login loginHandler;

    public Client()
    {
        registerHandler = new Register();
        loginHandler = new Login();
    }
}
