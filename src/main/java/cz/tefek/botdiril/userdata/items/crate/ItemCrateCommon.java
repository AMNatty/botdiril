package cz.tefek.botdiril.userdata.items.crate;

import org.apache.commons.math3.random.RandomDataGenerator;

public class ItemCrateCommon extends ItemCrate
{
    public ItemCrateCommon()
    {
        super("crate", "Crate");
        this.setDescription("Contains basic and some rare items.");
        this.setBuyValue(4000);
        this.setSellValue(2000);
        this.setEmoteIcon("<:basiccrate:446996800524779542>");
    }

    @Override
    public long generateCoins(RandomDataGenerator rdg)
    {
        var val = getValue(rdg);
        return rdg.nextLong(val / 50, val / 20);
    }

    @Override
    public long getValue(RandomDataGenerator rdg)
    {
        return 13000 + rdg.nextInt(5000, 15000);
    }

    @Override
    public long getRngOffset()
    {
        return 0;
    }
}
