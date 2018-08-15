package cz.tefek.botdiril.core.server;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import cz.tefek.botdiril.sql.DB;

public class ChannelCache
{
    public static Set<Long> disabledChannels = new HashSet<>();

    public static void syncFromSQL()
    {
        try
        {
            var c = DB.getConnection();

            synchronized (c)
            {
                var stmt = c.prepareStatement("SELECT * FROM turnedoff");
                var sq = stmt.executeQuery();

                while (sq.next())
                {
                    disabledChannels.add(sq.getLong("channelid"));
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
