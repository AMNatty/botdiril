package cz.tefek.botdiril.userdata;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import cz.tefek.botdiril.userdata.items.Item;
import cz.tefek.botdiril.userdata.items.ItemPair;

public class UserInventory
{

    private final long user;
    private AtomicLong coins = new AtomicLong(0);
    private Inventory inventory = new Inventory();
    private UUID uuid;
    private ConcurrentHashMap<String, Long> timers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> properties = new ConcurrentHashMap<>();
    private int luckyStreak = 0;
    private boolean luckyStreakEnabled = false;
    private int streak;
    private ItemPair undo = null;

    public UserInventory(long mid, UUID uuid)
    {
        this.user = mid;
        this.uuid = uuid;
    }

    public void setStreak(int streak)
    {
        this.streak = streak;
    }

    public void setCoins(long coins)
    {
        this.coins.set(coins);

        this.serialize();
    }

    public void addCoins(long coins)
    {
        this.coins.addAndGet(coins);

        this.serialize();
    }

    public void addItem(Item i)
    {
        if (inventory.containsKey(i))
            inventory.replace(i, inventory.get(i) + 1);
        else
            inventory.put(i, 1L);

        this.serialize();
    }

    public void addItem(Item i, long add)
    {
        if (inventory.containsKey(i))
            inventory.replace(i, inventory.get(i) + add);
        else
            inventory.put(i, add);

        if (inventory.get(i) == 0)
            inventory.remove(i);

        this.serialize();
    }

    public void addItemUnsafe(Item i, long add)
    {
        if (inventory.containsKey(i))
            inventory.replace(i, inventory.get(i) + add);
        else
            inventory.put(i, add);

        if (inventory.get(i) == 0)
            inventory.remove(i);
    }

    public ConcurrentHashMap<String, Integer> getProperties()
    {
        return properties;
    }

    public int getProperty(String id)
    {
        return properties.get(id);
    }

    public void updateProperty(String id, int value)
    {
        if (properties.containsKey(id))
        {
            properties.replace(id, value);
        }
        else
        {
            properties.put(id, value);
        }
    }

    public int incrAndGetProperty(String id)
    {
        if (!properties.containsKey(id))
        {
            properties.put(id, 0);
        }

        var nval = properties.get(id) + 1;

        properties.replace(id, nval);

        return nval;
    }

    public UUID getUUID()
    {
        return uuid;
    }

    public void setInventory(Inventory inventory)
    {
        this.inventory = inventory;
    }

    public Set<ItemPair> getInventory()
    {
        return inventory.entrySet().stream().map(e -> new ItemPair(Item.getByID(e.getKey()), e.getValue())).collect(Collectors.toSet());
    }

    public Map<String, Long> getInventoryRaw()
    {
        return inventory;
    }

    public boolean sellItem(Item i, long money)
    {
        if (!inventory.containsKey(i) || inventory.get(i) <= 0)
            return false;

        if (inventory.get(i) == 1)
            inventory.remove(i);
        else
            inventory.replace(i, inventory.get(i) - 1);

        this.coins.getAndAdd(money);

        this.serialize();

        return true;
    }

    public boolean buyItem(Item i, long money)
    {
        if (this.coins.get() < money)
            return false;

        if (inventory.containsKey(i))
            inventory.replace(i, inventory.get(i) + 1);
        else
            inventory.put(i, 1L);

        this.coins.getAndAdd(-money);

        this.serialize();

        return true;
    }

    public boolean hasItem(Item i, int amt)
    {
        return howManyOf(i) >= amt;
    }

    public long howManyOf(Item i)
    {
        if (!inventory.containsKey(i))
            return 0;

        return inventory.get(i);
    }

    public boolean hasItem(Item i)
    {
        return hasItem(i, 1);
    }

    public boolean sellItems(Item i, long money, long amt)
    {
        if (!inventory.containsKey(i) || inventory.get(i) <= 0)
            return false;

        if (inventory.get(i) < amt)
            return false;

        if (inventory.get(i) == amt)
            inventory.remove(i);
        else
            inventory.replace(i, inventory.get(i) - amt);

        this.coins.getAndAdd(money * amt);

        this.serialize();

        return true;
    }

    public boolean buyItems(Item i, long money, long amt)
    {
        if (this.coins.get() < money * amt)
            return false;

        if (inventory.containsKey(i))
            inventory.replace(i, inventory.get(i) + amt);
        else
            inventory.put(i, amt);

        this.coins.getAndAdd(-money * amt);

        this.serialize();

        return true;
    }

    public void serialize()
    {
        // Any action that requires an inventory update resets the undo command
        undo = null;

        UserStorage.serialize(this);
    }

    public void setUndo(ItemPair undo)
    {
        this.undo = undo;
    }

    public ItemPair getUndo()
    {
        return undo;
    }

    public boolean canUndo()
    {
        return undo != null;
    }

    public long getCoins()
    {
        return coins.get();
    }

    public long getUserID()
    {
        return user;
    }

    public int getStreak()
    {
        return streak;
    }

    public long useTimer(String tid, long timeout)
    {
        if (timers.get(tid) != null)
        {
            if (System.currentTimeMillis() > timers.get(tid))
            {
                timers.replace(tid, System.currentTimeMillis() + timeout);

                this.serialize();

                return -1;
            }

            return timers.get(tid) - System.currentTimeMillis();
        }

        timers.put(tid, System.currentTimeMillis() + timeout);

        this.serialize();

        return -1;
    }

    public long checkTimer(String tid)
    {
        if (timers.get(tid) != null)
        {
            if (System.currentTimeMillis() > timers.get(tid))
            {
                return -1;
            }

            return timers.get(tid) - System.currentTimeMillis();
        }

        return -1;
    }

    public long useTimerOverride(String tid, long timeout)
    {
        if (timers.get(tid) != null)
        {
            if (System.currentTimeMillis() > timers.get(tid))
            {
                timers.replace(tid, System.currentTimeMillis() + timeout);

                this.serialize();

                return -1;
            }

            timers.replace(tid, System.currentTimeMillis() + timeout);
            return timers.get(tid) - System.currentTimeMillis();
        }

        this.serialize();

        timers.put(tid, System.currentTimeMillis() + timeout);

        return -1;
    }

    public ConcurrentHashMap<String, Long> getTimers()
    {
        return timers;
    }

    public void setTimer(String tid, long timestamp)
    {
        timers.put(tid, timestamp);
    }

    public int getLuckyStreak()
    {
        return luckyStreak;
    }

    public void incrStreak()
    {
        this.luckyStreak++;
    }

    public void resetLuckyStreak()
    {
        this.luckyStreak = 0;
        this.luckyStreakEnabled = false;
    }

    public boolean isLuckyStreakEnabled()
    {
        return luckyStreakEnabled;
    }

    public void setLuckyStreakEnabled(boolean luckyStreakEnabled)
    {
        this.luckyStreakEnabled = luckyStreakEnabled;
    }

    public void resetTimer(String id)
    {
        timers.remove(id);
    }
}
