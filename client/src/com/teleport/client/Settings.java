package com.teleport.client;

public class Settings
{
    private String location;
    private boolean single;

    public Settings()
    {
        if(!single)
        {
            location = "";
            single = true;
        }
    }

    public String GetLocation()
    {
        return location;
    }

    public void SetLocation(String newLocation)
    {
        location = newLocation;
    }
}
