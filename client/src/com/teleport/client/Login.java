package com.teleport.client;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.json.simple.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class Login
{
    private final String ADDRESS = "127.0.0.1";
    private final String PORT = "8000";
    private final String SERVER_URL = "http://" + ADDRESS + ":" + PORT + "/api/login";
    private final String FORM_NAME = "login";
    private final String SUBMIT_BUTTON_NAME = "login-submit";
    private final String USERNAME_FIELD_NAME = "username";
    private final String PASSWORD_FIELD_NAME = "password";

    /*
    Performs a login action to URL.

    Accepts a JSON of the following format:
    { "username": "sample-usarname",
      "password": "sample-password"
    }

    Om success, return the HTML page returned by the web server
    (of type com.gargoylesoftware.htmlunit.html.HtmlPage)

    On failure, return null.
     */

    //public HtmlPage post(JSONObject data)
    public Page post(JSONObject data)
    {
        try (final WebClient webClient = new WebClient())
        {
            final java.net.URL url = new URL(SERVER_URL);
            WebRequest requestSettings = new WebRequest(url, HttpMethod.POST);
            requestSettings.setAdditionalHeader("Content-Type", "application/json");
            requestSettings.setRequestBody(data.toString());
            Page res = webClient.getPage(requestSettings);
            return res;
        }
        catch (MalformedURLException e)
        {
            return null;
        }
        catch (java.io.IOException e)
        {
            return null;
        }


        /*
        try (final WebClient webClient = new WebClient())
        {
            final HtmlPage page = webClient.getPage(URL);
            final HtmlForm form = page.getFormByName(FORM_NAME);
            final HtmlSubmitInput button = form.getInputByName(SUBMIT_BUTTON_NAME);
            final HtmlTextInput usernameField = form.getInputByName(USERNAME_FIELD_NAME);
            final HtmlTextInput passwordField = form.getInputByName(PASSWORD_FIELD_NAME);

            usernameField.setValueAttribute(data.get("0").toString());
            passwordField.setValueAttribute(data.get("1").toString());

            final HtmlPage resp = button.click();
            return resp;
        }
        catch (java.io.IOException e)
        {
            return null;
        }
        */
    }
}