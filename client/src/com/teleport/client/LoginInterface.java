package com.teleport.client;

import org.json.simple.parser.ParseException;
import java.io.IOException;

public class LoginInterface
{
    public String register(String username, String password) throws IOException, ParseException
    {
        if (client.register(username, password))
        {
            return "Registered successfully";
        }
        else
        {
            return "Could not register";
        }
    }

    public String login(String username, String password) throws IOException, ParseException
    {
        if (client.login(username, password))
        {
            return "Logged in successfully";
        }
        else
        {
            return "Could not login";
        }
    }

    private Client client;

    public LoginInterface() throws IOException
    {
        client = new Client();
    }
}
