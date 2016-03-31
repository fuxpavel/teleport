package sample;


import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.util.*;

public class PostLoginInterface
{
    private Client client;

    public PostLoginInterface() throws IOException
    {
        client = new Client();
    }

    public String getFriendRequests() throws IOException, ParseException
    {
        String temp = "";
        Map<String, List<String>> friends = client.getFriendRequests();
        Iterator t = friends.values().iterator();
        for (String key : friends.keySet())
        {
            temp = temp + key.toString()+": "+t.next().toString();
        }
        return temp;
    }

    public String addFriend(String friend) throws IOException, ParseException
    {
        if (client.addFriends(friend))
        {
            return "Request sent";
        }
        else
        {
            return "Couldn't send request";
        }
    }

    public String respondToRequest(String friend, boolean status) throws IOException, ParseException
    {
        if (client.respondToRequest(friend, status))
        {
            return "Responded successfully";
        }
        else
        {
            return "Couldn't respond";
        }
    }

    public List<String> getUsername(String name) throws IOException, ParseException
    {
        return client.getUsernameList(name);
    }

    public List<String> getFriends() throws IOException, ParseException
    {
        return client.getFriends();
    }

    public void send(String... paths) throws IOException
    {
        List<String> p = new ArrayList<String>();
        for (String path : paths)
        {
            p.add(path);
        }
        Client client = new Client();
        client.sendFile(p);
    }

    public void receive(String sender) throws IOException, ParseException
    {
        Client client = new Client();
        String ip = client.get_sender_ip(sender);
        if (!ip.equals("not friends"))
        {
            client.recvFile(ip);
        }
        else
        {
            System.out.println("not friends");
        }
    }
}