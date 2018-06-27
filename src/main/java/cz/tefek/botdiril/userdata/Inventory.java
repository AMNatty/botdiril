package cz.tefek.botdiril.userdata;

import java.util.concurrent.ConcurrentHashMap;

import cz.tefek.botdiril.userdata.items.Item;

public class Inventory extends ConcurrentHashMap<String, Long>
{
    /**
     * 
     */
    private static final long serialVersionUID = 7835163262087472053L;

    public Long get(Item key)
    {
        return this.get(key.getID());
    }

    public boolean containsKey(Item key)
    {
        return this.containsKey(key.getID().toLowerCase());
    }

    public Long put(Item key, long value)
    {
        return this.put(key.getID(), value);
    }

    public Long replace(Item key, long value)
    {
        return super.replace(key.getID(), value);
    }

    public Long remove(Item key)
    {
        return super.remove(key.getID());
    }
}
