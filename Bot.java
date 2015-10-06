package com.company;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by Nikola on 7/17/2015.
 */
public class Bot
{
    String server;
    String channel;
    String nick;
    String login;
    String raw_line = null;

    Socket socket;
    BufferedWriter writer;
    BufferedReader reader;

    public Bot(String srv,String chnl,String nm, String log)
    {
        server = srv;
        channel = chnl;
        nick = nm;
        login = log;
    }
    public boolean connect()
    {
        try
        {
            socket = new Socket(server, 6667);

            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //logon
            writer.write("NICK " + nick + "\r\n");
            writer.write("USER " + login + " 8 * : fushar Pool bot\r\n");
            writer.flush();

            // Read raw_lines from the server until it tells us we have connected.

            while ((raw_line = reader.readLine()) != null)
            {
                if (raw_line.indexOf("004") >= 0)
                {
                    // We are now logged in.
                    return true;
                } else if (raw_line.indexOf("433") >= 0)
                {
                    System.out.println("Nickname is already in use.");
                    return false;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public void joinChannel(String channel)
    {
        try
        {
            writer.write("JOIN " + channel + "\r\n");
            writer.flush();
        }catch(Exception e){e.printStackTrace();}
    }
    public void work()
    {
        try
        {
            String opt1 = null;
            String opt2 = null;
            boolean voting_started = false;
            int count1 = 0;
            int count2 = 0;

            Line line = new Line(nick);
            while ((raw_line = reader.readLine()) != null)
            {
                System.out.println(raw_line);
                line.update(raw_line);
                System.out.println(line.isUserLine() + " " + line.getText() + " " + line.getSender() + " " + line.getChannel());

                if(line.isCommand() == false)
                    continue;

                if (line.getCommand().equals("ping"))
                {
                    writer.write("PONG " + raw_line.substring(5) + "\r\n");
                    System.out.println("PONG " + raw_line.substring(5) + "\r\n");
                    writer.flush();
                }
                else if(line.getCommand().equals("!makepool"))
                {
                    if(!(line.getText().contains(" ") && line.getText().indexOf(" ") != line.getText().lastIndexOf(" ")))
                    {
                        write("Incorrect syntax");
                        continue;
                    }

                    if(voting_started)
                    {
                        write("Please wait for the current pool to finish before starting a new one.");
                        continue;
                    }
                    int lower = raw_line.indexOf("!makepool") + 10;
                    int counter = lower;

                    while(raw_line.charAt(counter) != ' ') counter++;
                    opt1 = raw_line.substring(lower,counter);
                    opt2 = raw_line.substring(counter+1, raw_line.length());

                    write("WRITE vote1 for ' " + opt1 + " '");
                    write("WRITE vote2 for ' " + opt2 + " '");
                    voting_started = true;

                }
                else if(line.getCommand().equals("vote1"))
                {
                    count1++;
                }
                else if(line.getCommand().equals("vote2"))
                {
                    count2++;
                }
                else if(line.getCommand().equals("!count"))
                {
                    voting_started = false;
                    write("Votes for " + opt1 + ": " + count1);
                    write("Votes for " + opt2 + ": " + count2);
                    count1 = 0;
                    count2 = 0;
                }
                else
                {
                    System.out.println("Error identifying command: " + line.getCommand());
                }
            }

        }
        catch(Exception e){e.printStackTrace();}


    }
    public void write(String text)
    {
        try
        {
            writer.write("PRIVMSG " + channel + " :" + text + "\r\n");
            writer.flush();
        }
        catch(Exception e)
        {e.printStackTrace();}

    }

}
