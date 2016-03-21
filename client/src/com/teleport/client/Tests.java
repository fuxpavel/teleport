package com.teleport.client;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import asg.cliche.ShellFactory;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class Tests
{
    @Test
    public void loginInterfaceben() throws IOException, ParseException
    {
        LoginInterface l = new LoginInterface();
        l.register("1", "1");
        l.register("2", "2");
        //l.login("1","1");
    }
    @Test
    public void PostLoginInterfaceben() throws IOException, ParseException
    {
        Client c = new Client();
        c.addFriends("2");
        List<String> n = new ArrayList<>();
        n.add("D:\\Users\\user-pc\\Desktop\\Final_Project\\Teleport\\n.png");
        c.sendFile(n);
    }

    @Test
    public void PostLoginInterfacealex() throws IOException, ParseException
    {
        Client c = new Client();
        c.respondToRequest("1", true);
        c.recvFile(c.get_sender_ip("1"));
    }
}
