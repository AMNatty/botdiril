package cz.tefek.botdiril.userdata.items.crate;

import org.apache.commons.math3.random.RandomDataGenerator;

public class ItemGoldenCrate extends ItemCrate
{
    public ItemGoldenCrate()
    {
        super("goldencrate", "Golden Crate");
        this.setDescription("Special crate, cannot be purchased, contains unique items.");
        this.setSellValue(40000);
        this.setEmoteIcon("<:goldencrate:446996800348487692>");
    }

    @Override
    public long generateCoins(RandomDataGenerator rdg)
    {
        return rdg.nextLong(getValue(rdg) / 3, getValue(rdg));
    }

    @Override
    public long getValue(RandomDataGenerator rdg)
    {
        return 50000 + rdg.nextInt(7000, 14000);
    }

    @Override
    public long getRngOffset()
    {
        return 1_200_000;
    }
}
