package com.company;

/**
 * Created by Nikola on 7/19/2015.
 */
public class Line
{
    private String raw_line;
    private String text;
    private String command = "-1";
    private String channel;
    private String sender;
    private String botname;
    private boolean userLine;

    public Line(String nick)
    {
        botname = nick;
    }

    public void update(String line)
    {
        raw_line = line;
        checkIfUser();
        checkIfPing();
        if (userLine)
        {
            setText();
            setChannel();
            setSender();
            checkCommand();
        }
    }

    public String getSender()
    {
        return sender;
    }
    public String getChannel()
    {
        return channel;
    }
    public String getText()
    {
        return text;
    }
    public String getCommand(){ return command; }
    public boolean isUserLine(){ return userLine;}
    public boolean isCommand()
    {
        if (command.equals("-1"))
            return false;
        else
            return true;
    }
    public boolean isPing()
    {
        if(raw_line.toLowerCase().startsWith("ping"))
            return true;
        else
            return false;
    }

    private void setText()
    {
        text = raw_line.substring(raw_line.indexOf(" :") + 2);
    }
    private void setChannel()
    {
        int lower = raw_line.indexOf("#");
        int upper = (raw_line.substring(lower)).indexOf(" :") + lower;
        channel = raw_line.substring(lower, upper);
    }
    private void setSender()
    {
        sender = raw_line.substring(1, raw_line.indexOf("!"));
    }
    private void checkIfPing()
    {
        if(isPing())
            command = "ping";
    }
    private void checkIfUser()
    {
        userLine = true;
        String[] systemMsgs = {":" + botname.toLowerCase(), "ping", ":irc", ":global", ":py-ctcp", ":peer"};
        for(String msg : systemMsgs)
        {
            if(raw_line.toLowerCase().startsWith(msg))
            {
                userLine = false;
                break;
            }
        }
    }
    private void checkCommand()
    {
        String commandList[] = {"!makepool","vote1","vote2","!count"};

        for(String cmd : commandList)
        {
            if(text.toLowerCase().contains(cmd))
            {
                command = cmd;
                break;
            }
        }
    }


}
