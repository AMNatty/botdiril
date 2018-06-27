package cz.tefek.botdiril.userdata.items.crate;

import cz.tefek.botdiril.userdata.items.Item;

public class ItemDropPair
{
    Item item;
    long rarity;

    public ItemDropPair(Item item, long rarity)
    {
        this.item = item;
        this.rarity = rarity;
    }

    public long getRarity()
    {
        return rarity;
    }

    public Item getItem()
    {
        return item;
    }
}
