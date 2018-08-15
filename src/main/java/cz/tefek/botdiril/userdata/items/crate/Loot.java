package cz.tefek.botdiril.userdata.items.crate;

import java.util.ArrayList;

import cz.tefek.botdiril.userdata.items.Item;
import cz.tefek.botdiril.userdata.items.ItemPair;

public class Loot extends ArrayList<ItemPair>
{
    /**
     * 
     */
    private static final long serialVersionUID = -8668551770131264979L;

    public ItemPair getByItem(Item i)
    {
        return this.stream().filter(c -> i.equals(c.getItem())).findAny().orElse(null);
    }

    public void incrementItem(Item i)
    {
        incrementItem(i, 1);
    }

    public void incrementItem(Item i, long amt)
    {
        var gbi = getByItem(i);

        if (gbi == null)
        {
            this.add(new ItemPair(i, amt));
        }
        else
        {
            gbi.increaseAmount(amt);
        }
    }

    public Loot sortedByItemValue()
    {
        this.sort((a, b) -> b.getItem().canBeSold() ? Integer.MAX_VALUE : (int) Math.max(Math.min(b.getItem().getSellValue() * b.getAmount() - a.getItem().getSellValue() * a.getAmount(), Integer.MAX_VALUE), Integer.MIN_VALUE));

        return this;
    }

    public void mergeIn(Loot generateLoot)
    {
        generateLoot.forEach(c -> {
            incrementItem(c.getItem(), c.getAmount());
        });
    }
}
