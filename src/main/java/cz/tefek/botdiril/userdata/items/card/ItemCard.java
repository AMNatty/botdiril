package cz.tefek.botdiril.userdata.items.card;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import cz.tefek.botdiril.userdata.items.Item;

public class ItemCard extends Item
{
    static final String CARD_INFO = "A collectible League of Legends card.";

    public ItemCard(Card c, List<CardCollection> collections)
    {
        super(c.getID(), c.getWhat());

        collections.sort(Comparator.comparingInt(CardCollection::ordinal));

        this.setSellValue(c.getRarity().getSellValue(collections));

        this.setID(collections.stream().map(CardCollection::toString).map(String::toLowerCase).collect(Collectors.joining("")) + super.getID());

        var hname = collections.stream().map(CardCollection::toString).map(consumer -> {
            return consumer.substring(0, 1).toUpperCase() + consumer.substring(1).toLowerCase();
        }).collect(Collectors.joining(" ")) + " " + super.getHumanName();

        if (hname.startsWith(" "))
        {
            hname = hname.substring(1);
        }

        this.setHumanName(hname);

        this.setDescription(CARD_INFO + " " + c.getRarity().toString());
    }

    @Override
    public boolean canBeBought()
    {
        return false;
    }
}
