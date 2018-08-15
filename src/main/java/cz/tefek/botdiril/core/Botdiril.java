package cz.tefek.botdiril.core;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

public class Botdiril
{
    private JDABuilder jdaBuilder;
    private JDA jda = null;

    public Botdiril() throws LoginException, InterruptedException, IOException
    {
        jdaBuilder = new JDABuilder(AccountType.BOT);

        jdaBuilder.setToken("Bot " + BotdirilConfig.API_KEY_DISCORD);

        System.out.println("Starting with API key: " + BotdirilConfig.API_KEY_DISCORD.substring(0, 8) + BotdirilConfig.API_KEY_DISCORD.substring(8).replaceAll(".", "*"));

        jdaBuilder.addEventListener(new BEventListener());
    }

    public void build() throws LoginException, InterruptedException
    {
        jda = jdaBuilder.buildBlocking();
    }

    public JDA getJDA()
    {
        return jda;
    }

    public final String getUniversalPrefix()
    {
        return "botdiril.";
    }
}
