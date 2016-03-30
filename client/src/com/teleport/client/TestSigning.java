package com.teleport.client;

import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSigning
{
    private Client client;

    @BeforeClass
    public static void setUpEnvironment() throws IOException
    {
        String cwd = System.getProperty("user.dir");
        String dir = cwd.substring(0, cwd.lastIndexOf("/") + 1) + "server";

        ProcessBuilder pbrm = new ProcessBuilder("rm", "../server/Teleport_DB.db").directory(new File(dir));
        ProcessBuilder pbgunicorn = new ProcessBuilder("gunicorn", "--bind", "0.0.0.0", "server")
                .directory(new File(dir));
        Process rm = pbrm.start();
        Process gunicorn = pbgunicorn.start();
    }

    @Before
    public void setUp() throws IOException
    {
        client = new Client();
    }

    /**************************************************************************************/

    //Testing registration

    // User 1
    @Test
    public void testACorrectRegistration1() throws IOException, ParseException
    {
        String username = "username1";
        String password = "password1";
        assertTrue(client.register(username, password));
    }

    @Test
    public void testBExistingUsernamePasswordRegistration1() throws IOException, ParseException
    {
        String username = "username1";
        String password = "password1";
        assertFalse(client.register(username, password));
    }

    @Test
    public void testCExistingUsernameRegistration1() throws IOException, ParseException
    {
        String username = "username1";
        String password = "wordpass1";
        assertFalse(client.register(username, password));
    }

    //User 2
    @Test
    public void testDCorrectRegistration2() throws IOException, ParseException
    {
        String username = "username2";
        String password = "password2";
        assertTrue(client.register(username, password));
    }

    @Test
    public void testEExistingUsernamePasswordRegistration2() throws IOException, ParseException
    {
        String username = "username2";
        String password = "password2";
        assertFalse(client.register(username, password));
    }

    @Test
    public void testFExistingUsernameRegistration2() throws IOException, ParseException
    {
        String username = "username2";
        String password = "wordpass2";
        assertFalse(client.register(username, password));
    }

    /*****************************************************************************************/

    //Testing login

    //User 1

    @Test
    public void testGIncorrectUsernamePasswordLogin1() throws IOException, ParseException
    {
        String username = "nameuser1";
        String password = "wordpass1";
        assertFalse(client.login(username, password));
    }

    @Test
    public void testHIncorrectUsernameLogin1() throws IOException, ParseException
    {
        String username = "nameuser1";
        String password = "password1";
        assertFalse(client.login(username, password));
    }

    @Test
    public void testIIncorrectPasswordLogin1() throws IOException, ParseException
    {
        String username = "username1";
        String password = "wordpass1";
        assertFalse(client.login(username, password));
    }

    @Test
    public void testJCorrectLogin1() throws IOException, ParseException
    {
        String username = "username1";
        String password = "password1";
        assertTrue(client.login(username, password));
    }

    //User 2

    @Test
    public void testKIncorrectUsernamePasswordLogin2() throws IOException, ParseException
    {
        String username = "nameuser2";
        String password = "wordpass2";
        assertFalse(client.login(username, password));
    }

    @Test
    public void testLIncorrectUsernameLogin2() throws IOException, ParseException
    {
        String username = "nameuser2";
        String password = "password2";
        assertFalse(client.login(username, password));
    }

    @Test
    public void testMIncorrectPasswordLogin2() throws IOException, ParseException
    {
        String username = "username2";
        String password = "wordpass2";
        assertFalse(client.login(username, password));
    }

    @Test
    public void testNCorrectLogin2() throws IOException, ParseException
    {
        String username = "username2";
        String password = "password2";
        assertTrue(client.login(username, password));
    }
}