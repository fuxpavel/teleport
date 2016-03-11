package com.teleport.client;

import asg.cliche.ShellFactory;
import java.io.IOException;


public class Main
{
    public static void main(String[] args) throws IOException
    {
        ShellFactory.createConsoleShell("Login interface", "teleport-client", new LoginInterface()).commandLoop();
    }
}
