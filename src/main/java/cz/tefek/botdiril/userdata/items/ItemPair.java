package cz.tefek.botdiril.userdata.items;

public class ItemPair
{
    Item item;
    long amount;

    public ItemPair(Item item)
    {
        this.item = item;
        this.amount = 1;
    }

    public ItemPair(Item item, long long1)
    {
        this.item = item;
        this.amount = long1;
    }

    public long getAmount()
    {
        return amount;
    }

    public Item getItem()
    {
        return item;
    }

    public void increaseAmount(long amount)
    {
        this.amount += amount;
    }
}
