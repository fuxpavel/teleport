package com.teleport.client;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Scanner;

public class Main
{
    @Command
    public String register() throws IOException, ParseException
    {
        Scanner sc = new Scanner(System.in);
        Client client = new Client();
        System.out.print("username: ");
        String username = sc.nextLine();
        System.out.print("password: ");
        String password = sc.nextLine();
        boolean result = client.register(username, password);
        if (result)
        {
            return "Registered successfully";
        }
        else
        {
            return "Could not register";
        }
    }

    @Command
    public String login() throws IOException, ParseException
    {
        Scanner sc = new Scanner(System.in);
        Client client = new Client();
        System.out.print("username: ");
        String username = sc.nextLine();
        System.out.print("password: ");
        String password = sc.nextLine();
        boolean result = client.login(username, password);
        if (result)
        {
            return "Logged in successfully";
        }
        else
        {
            return "Could not login";
        }
    }

    @Command
    public void send()
    {

    }

    @Command
    public void recieve()
    {

    }

    public static void main(String[] args) throws IOException
    {
        ShellFactory.createConsoleShell("$", "teleport-client", new Main()).commandLoop();
    }
}
