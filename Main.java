package com.company;

import java.io.*;
import java.net.*;
import com.company.Bot;
public class Main
{

    public static void main(String[] args)
    {
            String server = "irc.rizon.net";
            String nick = "politicsBot";
            String login = "politicsBot";
            String channel = "#fushartest";

            Bot ircBot = new Bot(server,channel,nick,login);
            if(ircBot.connect())
            {
                ircBot.joinChannel(channel);
                ircBot.work();
            }

    }
}
