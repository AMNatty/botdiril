package cz.tefek.botdiril.userdata;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cz.tefek.botdiril.sql.DB;

public class UserStorage
{
    public static UserInventory getByID(long uid)
    {
        try
        {
            var c = DB.getConnection();

            synchronized (c)
            {
                var st = c.prepareStatement("SELECT * FROM users WHERE userid=(?)");
                st.setLong(1, uid);
                var rs = st.executeQuery();

                if (rs.next())
                {
                    return new UserInventory(rs.getLong("userid"), rs.getInt("pid"), c);
                }
                else
                {
                    var sta = c.prepareStatement("INSERT INTO users (userid) VALUES(?)");
                    sta.setLong(1, uid);
                    sta.execute();

                    var psa = c.prepareStatement("SELECT * FROM users WHERE userid=(?)");
                    psa.setLong(1, uid);
                    var eq = psa.executeQuery();

                    if (eq.next())
                    {
                        return new UserInventory(uid, eq.getInt("pid"), c);
                    }

                    return null;
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static List<UserInventory> getUsersSortedByCoins(int limit)
    {
        var ul = new ArrayList<UserInventory>(limit);

        var c = DB.getConnection();

        synchronized (c)
        {
            try
            {
                var stmt = c.prepareStatement("SELECT users.pid, users.userid, coins.amount FROM users INNER JOIN coins ON users.pid = coins.userid ORDER BY amount DESC LIMIT ?");
                stmt.setInt(1, limit);

                var res = stmt.executeQuery();

                while (res.next())
                {
                    ul.add(new UserInventory(res.getLong("users.userid"), res.getInt("users.pid"), c));
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        return ul;
    }

    public static List<UserInventory> getUsersSortedByAmountOfItem(String itemId, int limit)
    {
        var ul = new ArrayList<UserInventory>(limit);

        var c = DB.getConnection();

        synchronized (c)
        {
            try
            {
                var stmt = c.prepareStatement("SELECT users.pid, users.userid, inventory.itemcount FROM users INNER JOIN inventory ON users.pid = inventory.userid WHERE itemid = ? ORDER BY itemcount DESC LIMIT ?");
                stmt.setInt(1, UserInventory.getIDMappingForItem(itemId));
                stmt.setInt(2, limit);

                var res = stmt.executeQuery();

                while (res.next())
                {
                    ul.add(new UserInventory(res.getLong("users.userid"), res.getInt("users.pid"), c));
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        return ul;
    }
}
