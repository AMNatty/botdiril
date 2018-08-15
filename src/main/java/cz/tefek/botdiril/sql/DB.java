package cz.tefek.botdiril.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.jdbc.datasource.init.ScriptUtils;

import cz.tefek.botdiril.core.BotdirilConfig;
import cz.tefek.botdiril.userdata.UserInventory;
import cz.tefek.botdiril.userdata.items.Item;

public class DB
{
    public static final char DELIMETER = ';';
    private static Connection c = null;

    public static void open()
    {
        var BOTDIRIL_HOST = BotdirilConfig.SQL_HOST;
        var BOTDIRIL_SQL_USER = BotdirilConfig.SQL_KEY;
        var BOTDIRIL_SQL_PASS = BotdirilConfig.SQL_PASS;

        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            c = DriverManager.getConnection("jdbc:mysql://" + BOTDIRIL_HOST + "/?useUnicode=true" + "&autoReconnect=true" + "&useJDBCCompliantTimezoneShift=true" + "&useLegacyDatetimeCode=false" + "&serverTimezone=UTC", BOTDIRIL_SQL_USER, BOTDIRIL_SQL_PASS);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void init() throws SQLException
    {
        synchronized (c)
        {
            var rs = c.createStatement().executeQuery("SHOW DATABASES LIKE 'botdiril'");

            if (!rs.first())
            {
                var statements = new ArrayList<String>();
                ScriptUtils.splitSqlScript(SQLStatementLoader.getCode("createtables"), DELIMETER, statements);

                for (var sql : statements)
                {
                    c.createStatement().execute(sql);
                }

                System.out.println("Botdiril database created.");
            }
            else
            {
                System.out.println("Botdiril database exists.");

                c.createStatement().execute("USE botdiril");
            }
        }
    }

    public static Connection getConnection()
    {
        synchronized (c)
        {
            try
            {
                c.prepareStatement("SELECT 1").execute();
            }
            catch (SQLException e)
            {
                System.out.println("Connection died, attempting to reconnect...");

                try
                {
                    c.close();
                    var BOTDIRIL_HOST = BotdirilConfig.SQL_HOST;
                    var BOTDIRIL_SQL_USER = BotdirilConfig.SQL_KEY;
                    var BOTDIRIL_SQL_PASS = BotdirilConfig.SQL_PASS;

                    c = DriverManager.getConnection("jdbc:mysql://" + BOTDIRIL_HOST + "/?useUnicode=true" + "&autoReconnect=true" + "&useJDBCCompliantTimezoneShift=true" + "&useLegacyDatetimeCode=false" + "&serverTimezone=UTC", BOTDIRIL_SQL_USER, BOTDIRIL_SQL_PASS);

                    c.createStatement().execute("USE botdiril");
                }
                catch (SQLException e1)
                {
                    e1.printStackTrace();
                }
            }
        }

        return c;
    }

    public static void exit() throws SQLException
    {
        synchronized (c)
        {
            c.close();
        }
    }

    public static void initItems()
    {
        try
        {
            synchronized (c)
            {
                var st = c.prepareStatement("INSERT INTO itemlookup (itemname, itemtype, itemprice, itemsellvalue) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE itemprice = ?, itemsellvalue = ?");

                for (var item : Item.items)
                {
                    st.setString(1, item.getID());
                    st.setString(2, item.getClass().getSimpleName());
                    st.setLong(3, item.getBuyValue());
                    st.setLong(4, item.getSellValue());
                    st.setLong(5, item.getBuyValue());
                    st.setLong(6, item.getSellValue());
                    st.addBatch();
                    st.clearParameters();
                }

                st.executeBatch();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        UserInventory.loadMappings(getConnection());
    }
}
