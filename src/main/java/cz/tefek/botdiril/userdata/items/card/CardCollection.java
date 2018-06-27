package cz.tefek.botdiril.userdata.items.card;

public enum CardCollection
{
    FOIL(1000, 1, 20), GOLDEN(0, 5, 60), SHINY(1000, 4, 50);

    private int flatbonus;
    private int multiplierbonus;
    private int onein;

    private CardCollection(int flatbonus, int multiplierbonus, int onein)
    {
        this.flatbonus = flatbonus;
        this.multiplierbonus = multiplierbonus;
        this.onein = onein;
    }

    public int getFlatBonus()
    {
        return flatbonus;
    }

    public int getMultiplierBonus()
    {
        return multiplierbonus;
    }

    public int getOneIn()
    {
        return onein;
    }
}
