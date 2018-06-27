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
        return rdg.nextLong(val / 5, val / 2);
    }

    @Override
    public long getValue(RandomDataGenerator rdg)
    {
        return 1300 + rdg.nextInt(500, 1500);
    }

    @Override
    public long getRngOffset()
    {
        return 0;
    }
}
