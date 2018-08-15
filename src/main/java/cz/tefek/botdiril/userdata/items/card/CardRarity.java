package cz.tefek.botdiril.userdata.items.card;

import java.util.List;

public enum CardRarity
{
    BASIC(10, "<:basic:458265423968993280>"), COMMON(30, "<:common:458267281890476032>"), RARE(100, "<:rare:458267282003722253>"), LEGENDARY(500, "<:legendary:458267281923899393>"), ULTIMATE(2500, "<:ultimate:458266825172713474>");

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
