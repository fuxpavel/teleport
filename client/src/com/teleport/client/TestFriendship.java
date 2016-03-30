package com.teleport.client;

import org.json.simple.parser.ParseException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestFriendship
{
    private Client client;

    @AfterClass
    public static void tearDownEnvironment() throws IOException
    {
        String cwd = System.getProperty("user.dir");
        String dir = cwd.substring(0, cwd.lastIndexOf("/") + 1) + "server";

        ProcessBuilder pbrm = new ProcessBuilder("rm", "Teleport_DB.db").directory(new File(dir));
        ProcessBuilder pbgunicorn = new ProcessBuilder("pkill", "gunicorn").directory(new File(dir));
        Process rm = pbrm.start();
        Process gunicorn = pbgunicorn.start();
    }

    @Before
    public void setUp() throws IOException, ParseException
    {
        client = new Client();
    }

    /***********************************************************************************/

    @Test
    public void testANonExistingUser() throws IOException, ParseException
    {
        String username = "username1";
        String password = "password1";
        String friend = "nameuser4";

        client.login(username, password);

        boolean status = client.addFriend(friend);

        List<String> friends = client.getFriends();
        Map<String, List<String>> res = client.getFriendRequests();
        List<String> incoming = res.get("incoming");
        List<String> outgoing = res.get("outgoing");

        assertTrue(!status && friends.size() == 0 && incoming.size() == 0 && outgoing.size() == 0);
    }

    @Test
    public void testBExistingUserDeny() throws IOException, ParseException
    {
        String username = "username1";
        String password = "password1";
        String friend = "username2";
        String friendPassword = "password2";

        client.login(username, password);

        boolean status1 = client.addFriend(friend);

        List<String> friends1 = client.getFriends();
        Map<String, List<String>> res1 = client.getFriendRequests();
        List<String> incoming1 = res1.get("incoming");
        List<String> outgoing1 = res1.get("outgoing");

        client.login(friend, friendPassword);

        List<String> friends2 = client.getFriends();
        Map<String, List<String>> res2 = client.getFriendRequests();
        List<String> incoming2 = res2.get("incoming");
        List<String> outgoing2 = res2.get("outgoing");

        boolean status2 = client.respondToRequest(username, false);

        client.login(friend, friendPassword);

        List<String> friends3 = client.getFriends();
        Map<String, List<String>> res3 = client.getFriendRequests();
        List<String> incoming3 = res3.get("incoming");
        List<String> outgoing3 = res3.get("outgoing");

        assertTrue(status1 && friends1.size() == 0 && incoming1.size() == 0 && outgoing1.size() == 1 &&
                   outgoing1.contains(friend) && friends2.size() == 0 && incoming2.size() == 1 &&
                   incoming2.contains(username) && outgoing2.size() == 0 && status2 && friends3.size() == 0 &&
                   incoming3.size() == 0 && outgoing3.size() == 0);
    }

    @Test
    public void testCExistingUserConfirm() throws IOException, ParseException
    {
        String username = "username1";
        String password = "password1";
        String friend = "username2";
        String friendPassword = "password2";

        client.login(username, password);

        boolean status1 = client.addFriend(friend);

        List<String> friends1 = client.getFriends();
        Map<String, List<String>> res1 = client.getFriendRequests();
        List<String> incoming1 = res1.get("incoming");
        List<String> outgoing1 = res1.get("outgoing");

        client.login(friend, friendPassword);

        List<String> friends2 = client.getFriends();
        Map<String, List<String>> res2 = client.getFriendRequests();
        List<String> incoming2 = res2.get("incoming");
        List<String> outgoing2 = res2.get("outgoing");

        boolean status2 = client.respondToRequest(username, true);

        List<String> friends3 = client.getFriends();
        Map<String, List<String>> res3 = client.getFriendRequests();
        List<String> incoming3 = res3.get("incoming");
        List<String> outgoing3 = res3.get("outgoing");


        client.login(username, password);

        List<String> friends4 = client.getFriends();
        Map<String, List<String>> res4 = client.getFriendRequests();
        List<String> incoming4 = res4.get("incoming");
        List<String> outgoing4 = res4.get("outgoing");

        assertTrue(status1 && friends1.isEmpty() && incoming1.isEmpty() && outgoing1.size() == 1 &&
                   outgoing1.contains(friend) && friends2.isEmpty() && incoming2.size() == 1 &&
                   incoming2.contains(username) && outgoing2.isEmpty() && status2 && friends3.size() == 1 &&
                   friends3.contains(username) && incoming3.size() == 0 && outgoing3.isEmpty() &&
                   friends4.size() == 1 && friends4.contains(friend) && incoming4.isEmpty() && outgoing4.isEmpty());
    }
}
