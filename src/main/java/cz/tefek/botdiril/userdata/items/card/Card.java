package cz.tefek.botdiril.userdata.items.card;

public class Card
{
    private String id;
    private String what;
    private CardRarity rarity;

    public Card(String id, String what, CardRarity rarity)
    {
        this.id = id;
        this.what = what;
        this.rarity = rarity;
    }

    public String getID()
    {
        return id;
    }

    public CardRarity getRarity()
    {
        return rarity;
    }

    public String getWhat()
    {
        return what;
    }
}
