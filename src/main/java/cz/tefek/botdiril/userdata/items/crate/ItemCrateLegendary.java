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
        return rdg.nextLong(val / 5, val / 2);
    }

    @Override
    public long getValue(RandomDataGenerator rdg)
    {
        return 300_000 + rdg.nextInt(100_000, 550_000);
    }

    @Override
    public long getRngOffset()
    {
        return 800_000;
    }
}
