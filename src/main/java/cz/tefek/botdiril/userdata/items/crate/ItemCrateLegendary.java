package cz.tefek.botdiril.userdata.items.crate;

import org.apache.commons.math3.random.RandomDataGenerator;

public class ItemCrateLegendary extends ItemCrate
{
    public ItemCrateLegendary()
    {
        super("legendarycrate", "Legendary Crate");
        this.setDescription("Contains many unique or special items.");
        this.setBuyValue(1_234_567);
        this.setSellValue(500_000);
        this.setEmoteIcon("<:legendarycrate:446996800331841538>");
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
        return 3_000_000 + rdg.nextInt(1_000_000, 5_500_000);
    }

    @Override
    public long getRngOffset()
    {
        return 800_000;
    }
}
