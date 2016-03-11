package com.teleport.client;

import asg.cliche.Command;
import asg.cliche.Shell;
import asg.cliche.ShellDependent;
import asg.cliche.ShellFactory;
import org.json.simple.parser.ParseException;
import java.io.IOException;


public class LoginInterface implements ShellDependent
{
    @Command
    public void register(String username, String password) throws IOException, ParseException
    {
        if (client.register(username, password))
        {
            System.out.println("Registered successfully");
        }
        else
        {
            System.out.println("Could not register");
        }

    }

    @Command
    public void login(String username, String password) throws IOException, ParseException
    {
        if (client.login(username, password))
        {
            ShellFactory.createSubshell("PostLoginInterface", shell, "teleport-client", new PostLoginInterface())
                    .commandLoop();
            System.out.println("Logged in successfully");
        }
        else
        {
            System.out.println("Could not login");
        }
    }


    @Command
    public void send(String path) throws IOException
    {
        client.sendFile(path);
    }

    @Command
    public void receive(String ip) throws IOException
    {
        client.recvFile(ip);
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