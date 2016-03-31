package com.teleport.client;

import asg.cliche.Shell;
import asg.cliche.ShellDependent;
import org.json.simple.parser.ParseException;
import java.io.IOException;

public class LoginInterface implements ShellDependent
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

    private Shell shell;
    private Client client;

    public LoginInterface() throws IOException
    {
        client = new Client();
    }

    public void cliSetShell(Shell shell)
    {
        this.shell = shell;
    }
}
