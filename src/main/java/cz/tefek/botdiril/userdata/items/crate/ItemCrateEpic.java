package cz.tefek.botdiril.userdata.items.crate;

import org.apache.commons.math3.random.RandomDataGenerator;

public class ItemCrateEpic extends ItemCrate
{
    public ItemCrateEpic()
    {
        super("epiccrate", "Epic Crate");
        this.setDescription("Contains special items.");
        this.setBuyValue(250000);
        this.setSellValue(125000);
        this.setEmoteIcon("<:epiccrate:446996800746946560>");
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
        return 75_000 + rdg.nextInt(40_000, 90_000);
    }

    @Override
    public long getRngOffset()
    {
        return 60_000;
    }
}
