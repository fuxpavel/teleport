package com.teleport.client;

import org.apache.commons.cli.*;
import org.json.simple.JSONObject;

import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
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
                    formatter.printHelp("option", options);
                    validInput = true;
                }
                if (cmd.hasOption("r"))
                {
                    register();
                    validInput = true;
                }
                if (cmd.hasOption("l"))
                {
                    login();
                    validInput = true;
                }
                if (cmd.hasOption("q"))
                {
                    work = false;
                    validInput = true;
                }
                if (!validInput)
                {
                    System.out.println("Option unsupported");
                }
            }
        }
    }

    private static void register()
    {
        Scanner sc = new Scanner(System.in);
        Register registerHandler = new Register();
        System.out.print("username: ");
        String username = sc.nextLine();
        System.out.print("password: ");
        String password = sc.nextLine();
        JSONObject data = new JSONObject();
        data.put("username", username);
        data.put("password", password);
        registerHandler.post(data);
    }

    private static void login()
    {
        Scanner sc = new Scanner(System.in);
        Login loginHandler = new Login();
        System.out.print("username: ");
        String username = sc.nextLine();
        System.out.print("password: ");
        String password = sc.nextLine();
        JSONObject data = new JSONObject();
        data.put("username", username);
        data.put("password", password);
        loginHandler.post(data);
    }
}
