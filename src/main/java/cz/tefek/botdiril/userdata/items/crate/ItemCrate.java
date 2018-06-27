package cz.tefek.botdiril.userdata.items.crate;

import org.apache.commons.math3.random.RandomDataGenerator;

import cz.tefek.botdiril.userdata.items.Item;

public abstract class ItemCrate extends Item
{
    public ItemCrate(String id, String humanName)
    {
        super(id, humanName);
    }

    public Loot generateLoot(RandomDataGenerator rdg)
    {
        var loot = new Loot();
        var value = getValue(rdg);

        var cval = 0L;

        do
        {
            var i = CrateDrops.rollItem(this.getRngOffset(), rdg);
            cval += i.getSellValue();
            loot.incrementItem(i);
        }
        while (cval < value);

        return loot;
    }

    public void generateLoots(RandomDataGenerator rdg, Loot loots, long times)
    {
        var value = 0L;
        var rngoffset = this.getRngOffset();

        for (long j = 0; j < times; j++)
        {
            value += getValue(rdg);
        }

        var cval = 0L;

        do
        {
            var i = CrateDrops.rollItem(rngoffset, rdg);
            cval += i.getSellValue();
            loots.incrementItem(i);
        }
        while (cval < value);
    }

    public abstract long generateCoins(RandomDataGenerator rdg);

    public abstract long getValue(RandomDataGenerator rdg);

    public abstract long getRngOffset();
}
