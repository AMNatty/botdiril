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
        return rdg.nextLong(getValue(rdg) / 30, getValue(rdg) / 10);
    }

    @Override
    public long getValue(RandomDataGenerator rdg)
    {
        return 500_000 + rdg.nextInt(70_000, 140_000);
    }

    @Override
    public long getRngOffset()
    {
        return 1_200_000;
    }
}
