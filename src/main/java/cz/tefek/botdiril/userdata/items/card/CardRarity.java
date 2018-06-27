package cz.tefek.botdiril.userdata.items.card;

import java.util.List;

public enum CardRarity
{
    BASIC(100, "<:basic:458265423968993280>"),
    COMMON(300, "<:common:458267281890476032>"),
    RARE(1000, "<:rare:458267282003722253>"),
    LEGENDARY(5000, "<:legendary:458267281923899393>"),
    ULTIMATE(25000, "<:ultimate:458266825172713474>");

    long baseSellValue;
    String icon;

    CardRarity(long basesell, String icon)
    {
        this.baseSellValue = basesell;
        this.icon = icon;
    }

    public long getSellValue(List<CardCollection> collections)
    {
        long flats = collections.stream().mapToInt(CardCollection::getFlatBonus).sum();
        int mults = Math.max(collections.stream().mapToInt(CardCollection::getMultiplierBonus).sum(), 1);

        return (this.baseSellValue + flats) * mults;
    }

    public String getIcon()
    {
        return icon;
    }
}
