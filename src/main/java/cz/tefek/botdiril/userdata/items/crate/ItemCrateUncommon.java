package cz.tefek.botdiril.userdata.items.crate;

import org.apache.commons.math3.random.RandomDataGenerator;

public class ItemCrateUncommon extends ItemCrate
{
    public ItemCrateUncommon()
    {
        super("uncommoncrate", "Uncommon Crate");
        this.setDescription("Contains rare items.");
        this.setBuyValue(40000);
        this.setSellValue(20000);
        this.setEmoteIcon("<:uncommoncrate:446996800554139669>");
    }

    @Override
    public long generateCoins(RandomDataGenerator rdg)
    {
        var val = getValue(rdg);
        return rdg.nextLong(val / 5, val / 2);
    }

    @Override
    public long getValue(RandomDataGenerator rdg)
    {
        return 12000 + rdg.nextInt(6000, 15000);
    }

    @Override
    public long getRngOffset()
    {
        return 5500;
    }
}
