package com.teleport.client;

import asg.cliche.Command;
import asg.cliche.ShellFactory;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        ShellFactory.createConsoleShell("Login interface", "teleport-client", new LoginInterface()).commandLoop();
    }
}
