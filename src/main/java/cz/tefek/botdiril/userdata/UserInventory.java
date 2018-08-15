package cz.tefek.botdiril.userdata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cz.tefek.botdiril.userdata.items.Item;
import cz.tefek.botdiril.userdata.items.ItemPair;

public class UserInventory
{
    private static final Map<Integer, String> itemMappings = new HashMap<>();
    private static final Map<String, Integer> revItemMappings = new HashMap<>();

    public static void loadMappings(Connection c)
    {
        synchronized (c)
        {
            try
            {
                var rs = c.prepareStatement("SELECT * FROM itemlookup").executeQuery();

                while (rs.next())
                {
                    var id = rs.getInt("itemid");
                    var name = rs.getString("itemname");

                    itemMappings.put(id, name);
                    revItemMappings.put(name, id);
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        System.out.printf("%d item entries loaded.\n", itemMappings.size());
    }

    public static int getIDMappingForItem(String id)
    {
        return revItemMappings.get(id);
    }

    private final Connection c;
    private final long user;
    private final int id;

    public UserInventory(long mid, int id, Connection connection)
    {
        this.user = mid;
        this.id = id;
        this.c = connection;
    }

    public long getUserID()
    {
        return user;
    }

    public int getUID()
    {
        return id;
    }

    public int getOrCreateProp(String id, int defaultValue)
    {
        synchronized (c)
        {
            try
            {
                var stmt = c.prepareStatement("SELECT * FROM properties WHERE userid=(?) AND propid=(?)");
                stmt.setInt(1, this.id);
                stmt.setString(2, id);
                var res = stmt.executeQuery();

                if (res.next())
                {
                    return res.getInt("propvalue");
                }
                else
                {
                    var ust = c.prepareStatement("INSERT INTO properties (userid, propid, propvalue) VALUES (?, ?, ?)");
                    ust.setInt(1, this.id);
                    ust.setString(2, id);
                    ust.setInt(3, defaultValue);
                    ust.executeUpdate();

                    return defaultValue;
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        return defaultValue;
    }

    public void setOrCreateProp(String id, int value)
    {
        synchronized (c)
        {
            try
            {
                var stmt = c.prepareStatement("SELECT * FROM properties WHERE userid=(?) AND propid=(?)");
                stmt.setInt(1, this.id);
                stmt.setString(2, id);
                var res = stmt.executeQuery();

                if (res.next())
                {
                    var ust = c.prepareStatement("UPDATE properties SET propvalue=(?) WHERE userid=(?) AND propid=(?)");
                    ust.setInt(1, value);
                    ust.setInt(2, this.id);
                    ust.setString(3, id);
                    ust.execute();
                }
                else
                {
                    var ust = c.prepareStatement("INSERT INTO properties (userid, propid, propvalue) VALUES (?, ?, ?)");
                    ust.setInt(1, this.id);
                    ust.setString(2, id);
                    ust.setInt(3, value);
                    ust.execute();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    public int incrAndGetProperty(String id)
    {
        int propVal = 0;

        synchronized (c)
        {
            try
            {
                var stmt = c.prepareStatement("SELECT * FROM properties WHERE userid=(?) AND propid=(?)");
                stmt.setInt(1, this.id);
                stmt.setString(2, id);
                var res = stmt.executeQuery();

                if (res.next())
                {
                    propVal = res.getInt("propvalue");
                    var ust = c.prepareStatement("UPDATE properties SET propvalue=(?) WHERE userid=(?) AND propid=(?)");
                    ust.setInt(1, ++propVal);
                    ust.setInt(2, this.id);
                    ust.setString(3, id);
                    ust.execute();
                }
                else
                {
                    var ust = c.prepareStatement("INSERT INTO properties (userid, propid, propvalue) VALUES (?, ?, ?)");
                    ust.setInt(1, this.id);
                    ust.setString(2, id);
                    ust.setInt(3, propVal);
                    ust.execute();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        return propVal;
    }

    public int getProperty(String id)
    {
        return getOrCreateProp(id, 0);
    }

    public void setCoins(long coins)
    {
        synchronized (c)
        {
            try
            {
                var st = c.prepareStatement("INSERT INTO coins (userid, amount) VALUES (?, ?) ON DUPLICATE KEY UPDATE amount=VALUES(?)");
                st.setInt(1, this.id);
                st.setLong(2, coins);
                st.setLong(3, coins);
                st.execute();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    public long getCoins()
    {
        try
        {
            var stmt = c.prepareStatement("SELECT * FROM coins WHERE userid=(?)");
            stmt.setInt(1, this.id);
            var res = stmt.executeQuery();

            if (res.next())
            {
                return res.getLong("amount");
            }
            else
            {
                var ust = c.prepareStatement("INSERT INTO coins (userid, amount) VALUES (?, 0)");
                ust.setInt(1, this.id);
                ust.execute();

                return 0;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    public void addCoins(long coins)
    {
        synchronized (c)
        {
            try
            {
                var st = c.prepareStatement("INSERT INTO coins (userid, amount) VALUES (?, ?) ON DUPLICATE KEY UPDATE `amount`=`amount`+?");
                st.setInt(1, this.id);
                st.setLong(2, coins);
                st.setLong(3, coins);
                st.execute();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void addItem(Item i, long amount)
    {
        var rid = revItemMappings.get(i.getID());

        synchronized (c)
        {
            try
            {
                var stmt = c.prepareStatement("SELECT * FROM inventory WHERE userid=(?) AND itemid=(?)");
                stmt.setInt(1, this.id);
                stmt.setInt(2, rid);
                var res = stmt.executeQuery();

                if (res.next())
                {
                    var ust = c.prepareStatement("UPDATE inventory SET `itemcount`=`itemcount` + ? WHERE userid=(?) AND itemid=(?)");
                    ust.setLong(1, amount);
                    ust.setInt(2, this.id);
                    ust.setInt(3, rid);
                    ust.execute();
                }
                else
                {
                    var ust = c.prepareStatement("INSERT INTO inventory (userid, itemid, itemcount) VALUES (?, ?, ?)");
                    ust.setInt(1, this.id);
                    ust.setInt(2, rid);
                    ust.setLong(3, amount);
                    ust.execute();
                }
            }
            catch (SQLException e)
            {
                System.out.println(i.getID() + " : " + rid);
                e.printStackTrace();
            }
        }
    }

    public List<ItemPair> getInventory(int limit)
    {
        synchronized (c)
        {
            try
            {
                PreparedStatement stmt;

                if (limit < 1)
                {
                    stmt = c.prepareStatement("SELECT inventory.itemcount, itemlookup.itemname, itemlookup.itemsellvalue FROM inventory INNER JOIN itemlookup ON inventory.itemid = itemlookup.itemid WHERE userid=(?) AND inventory.itemcount>0 ORDER BY itemlookup.itemsellvalue DESC");
                    stmt.setInt(1, this.id);
                }
                else
                {
                    stmt = c.prepareStatement("SELECT inventory.itemcount, itemlookup.itemname, itemlookup.itemsellvalue FROM inventory INNER JOIN itemlookup ON inventory.itemid = itemlookup.itemid WHERE userid=(?) AND inventory.itemcount>0 ORDER BY itemlookup.itemsellvalue DESC LIMIT ?");
                    stmt.setInt(1, this.id);
                    stmt.setInt(2, limit);
                }

                var res = stmt.executeQuery();
                var inv = new ArrayList<ItemPair>();

                while (res.next())
                {
                    var strName = res.getString("itemname");
                    var item = Item.getByID(strName);

                    if (item == null)
                    {
                        System.err.println(strName + " is null item.");
                        continue;
                    }

                    inv.add(new ItemPair(item, res.getLong("itemcount")));
                }

                return inv;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }

    public List<ItemPair> getInventoryFiltered(int limit, List<String> filterOut)
    {
        synchronized (c)
        {
            try
            {
                PreparedStatement stmt;

                if (limit < 1)
                {
                    stmt = c.prepareStatement("SELECT inventory.userid, inventory.itemcount, itemlookup.itemname, itemlookup.itemsellvalue FROM inventory INNER JOIN itemlookup ON inventory.itemid = itemlookup.itemid AND inventory.itemcount>0 WHERE userid=(?) AND itemtype NOT RLIKE ? ORDER BY itemlookup.itemsellvalue DESC");
                    stmt.setInt(1, this.id);
                    stmt.setString(2, filterOut.stream().collect(Collectors.joining("|")));
                }
                else
                {
                    stmt = c.prepareStatement("SELECT inventory.userid, inventory.itemcount, itemlookup.itemname, itemlookup.itemsellvalue FROM inventory INNER JOIN itemlookup ON inventory.itemid = itemlookup.itemid AND inventory.itemcount>0 WHERE userid=(?) AND itemtype NOT RLIKE ? ORDER BY itemlookup.itemsellvalue DESC LIMIT ?");
                    stmt.setInt(1, this.id);
                    stmt.setString(2, filterOut.stream().collect(Collectors.joining("|")));
                    stmt.setInt(3, limit);
                }

                var res = stmt.executeQuery();
                var inv = new ArrayList<ItemPair>();

                while (res.next())
                {
                    var strName = res.getString("itemname");
                    var item = Item.getByID(strName);

                    if (item == null)
                    {
                        System.err.println(strName + " is null item.");
                        continue;
                    }

                    inv.add(new ItemPair(item, res.getLong("itemcount")));
                }

                return inv;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }

    public void addItem(Item i)
    {
        addItem(i, 1);
    }

    public boolean sellItem(Item i, long money)
    {
        return sellItems(i, money, 1);
    }

    public boolean buyItem(Item i, long money)
    {
        return this.buyItems(i, money, 1);
    }

    public boolean buyItems(Item i, long money, long amt)
    {
        if (this.getCoins() < money * amt)
            return false;

        addItem(i, amt);

        addCoins(-money * amt);

        return true;
    }

    public long howManyOf(Item i)
    {
        synchronized (c)
        {
            try
            {
                var stmt = c.prepareStatement("SELECT itemcount FROM inventory WHERE userid=(?) AND itemid=(?)");
                stmt.setInt(1, this.id);
                stmt.setInt(2, getIDMappingForItem(i.getID()));
                var res = stmt.executeQuery();

                if (res.next())
                    return res.getLong("itemcount");
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public boolean sellItems(Item i, long money, long amt)
    {
        if (howManyOf(i) < amt)
            return false;

        addItem(i, -amt);
        addCoins(money * amt);

        return true;
    }

    public long getTimer(String timer)
    {
        synchronized (c)
        {
            try
            {
                var stmt = c.prepareStatement("SELECT * FROM timers WHERE userid=(?) AND timerid=(?)");
                stmt.setInt(1, this.id);
                stmt.setString(2, timer);
                var res = stmt.executeQuery();

                if (res.next())
                    return res.getLong("timertime");
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public void setTimer(String tid, long timestamp)
    {
        synchronized (c)
        {
            try
            {
                var stmt = c.prepareStatement("SELECT * FROM timers WHERE userid=(?) AND timerid=(?)");
                stmt.setInt(1, this.id);
                stmt.setString(2, tid);
                var res = stmt.executeQuery();

                if (res.next())
                {
                    var ust = c.prepareStatement("UPDATE timers SET `timertime`=? WHERE userid=(?) AND timerid=(?)");
                    ust.setLong(1, timestamp);
                    ust.setInt(2, this.id);
                    ust.setString(3, tid);
                    ust.execute();
                }
                else
                {
                    var ust = c.prepareStatement("INSERT INTO timers (userid, timertime, timerid) VALUES (?, ?, ?)");
                    ust.setInt(1, this.id);
                    ust.setLong(2, timestamp);
                    ust.setString(3, tid);
                    ust.execute();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    public long useTimer(String tid, long timeout)
    {
        var tt = getTimer(tid);

        if (System.currentTimeMillis() > tt)
        {
            setTimer(tid, System.currentTimeMillis() + timeout);

            return -1;
        }

        return tt - System.currentTimeMillis();
    }

    public long checkTimer(String tid)
    {
        var tt = getTimer(tid);

        if (System.currentTimeMillis() > tt)
        {
            return -1;
        }

        return tt - System.currentTimeMillis();
    }

    // This differentiates in the fact that this overrides the time even when
    // waiting
    public long useTimerOverride(String tid, long timeout)
    {
        var tt = getTimer(tid);

        if (System.currentTimeMillis() > tt)
        {
            setTimer(tid, System.currentTimeMillis() + timeout);

            return -1;
        }

        setTimer(tid, System.currentTimeMillis() + timeout);

        return tt - System.currentTimeMillis();
    }

    public void resetTimer(String id)
    {
        setTimer(id, 0);
    }

    public boolean hasItem(Item i)
    {
        return howManyOf(i) > 0;
    }
}
