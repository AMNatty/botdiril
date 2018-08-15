package cz.tefek.botdiril.userdata.items.card;

public enum CardCollection
{
    FOIL(100, 1, 200), GOLDEN(0, 5, 600), SHINY(250, 4, 500);

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
