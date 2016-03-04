package com.teleport.client;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args) throws IOException, org.json.simple.parser.ParseException
    {
        Scanner sc = new Scanner(System.in);

        String[] arguments;
        CommandLine cmd;
        boolean work = true, validInput;

        Options options = new Options();
        options.addOption("h", "help", false, "display help");
        options.addOption("r", "register", false, "login");
        options.addOption("l", "login", false, "login");
        options.addOption("q", "quit", false, "quit");

        HelpFormatter formatter = new HelpFormatter();
        CommandLineParser parser = new DefaultParser();

        formatter.printHelp("option", options);
        while (work)
        {
            cmd = null;
            validInput = false;
            arguments = sc.nextLine().split(" ");
            try
            {
                cmd = parser.parse(options, arguments);
            }
            catch (ParseException e)
            {
                System.err.println("Wrong argument format");
            }

            if (cmd != null)
            {
                if (cmd.hasOption("h"))
                {
                    validInput = true;
                    formatter.printHelp("option", options);
                }
                if (cmd.hasOption("r"))
                {
                    validInput = true;
                    register();
                }
                if (cmd.hasOption("l"))
                {
                    validInput = true;
                    login();
                }
                if (cmd.hasOption("q"))
                {
                    validInput = true;
                    work = false;
                }
                if (!validInput)
                {
                    System.out.println("Option unsupported");
                }
            }
        }
    }

    private static void register() throws IOException, org.json.simple.parser.ParseException
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
            System.out.println("Registered successfully");
        }
        else
        {
            System.out.println("Could not register");
        }
    }

    private static void login() throws IOException, org.json.simple.parser.ParseException
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
            System.out.println("Logged in successfully");
        }
        else
        {
            System.out.println("Could not login");
        }
    }
}
