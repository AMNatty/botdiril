package cz.tefek.botdiril.userdata.items;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import cz.tefek.botdiril.userdata.items.card.Card;
import cz.tefek.botdiril.userdata.items.card.CardCollection;
import cz.tefek.botdiril.userdata.items.card.CardRarity;
import cz.tefek.botdiril.userdata.items.card.CollectionCombination;
import cz.tefek.botdiril.userdata.items.card.ItemCard;
import cz.tefek.botdiril.userdata.items.crate.CrateDrops;
import cz.tefek.botdiril.userdata.items.crate.ItemCrateCommon;
import cz.tefek.botdiril.userdata.items.crate.ItemCrateEpic;
import cz.tefek.botdiril.userdata.items.crate.ItemCrateLegendary;
import cz.tefek.botdiril.userdata.items.crate.ItemCrateUncommon;
import cz.tefek.botdiril.userdata.items.crate.ItemDropPair;
import cz.tefek.botdiril.userdata.items.crate.ItemGoldenCrate;

public class Item
{
    public static final String COINDIRIL = "<:coindiril:446988933763563520>";
    public static final String KEK = "<:keks:458631472572661761>";
    public static final String GEM = "<:gemdiril:458631540952530944>";

    public static final List<Item> items = new ArrayList<>();

    private static final List<List<CardCollection>> crdc = CollectionCombination.all();

    private String id;
    private String emoteIcon;
    private String humanName;
    private long sellValue;
    private long buyValue;
    private String description;

    public static long powerN(long number, int power)
    {
        if (power == 0)
            return 1;

        long result = number;

        while (power > 1)
        {
            result *= number;
            power--;
        }

        return result;
    }

    public static void loadFirst()
    {
        items.add(new ItemCrateCommon());
        items.add(new ItemCrateUncommon());
        items.add(new ItemGoldenCrate());
        items.add(new ItemCrateEpic());
        items.add(new ItemCrateLegendary());

        var kek = new Item("kek", "kek").desc("The kekest of keks.").setEmoteIcon("<:keks:458631472572661761>").setBuyValue(3).setSellValue(1);
        var gemdiril = new Item("gemdiril", "Gemdiril").setEmoteIcon(GEM).desc("A very rare gem of unknown value.").setSellValue(4096);

        items.add(kek);
        items.add(gemdiril);

        CrateDrops.drops.add(new ItemDropPair(kek, 100_000_000_000L));
        CrateDrops.drops.add(new ItemDropPair(gemdiril, 500_000L));

        try
        {
            var fr = new FileReader("static/skins.json");
            var jarr = new JSONArray(new JSONTokener(fr));

            jarr.forEach(c -> {
                var co = (JSONObject) c;

                crdc.forEach(con -> {
                    var rarity = CardRarity.values()[co.getInt("rarity")];
                    var ic = new ItemCard(new Card(co.getString("id"), co.getString("name"), rarity), con);
                    ic.setEmoteIcon(rarity.getIcon());

                    var mod = Math.max(con.stream().mapToInt(CardCollection::getOneIn).reduce(1, (a, b) -> a * b), 1);
                    var cols = Arrays.stream(CardCollection.values()).mapToLong(CardCollection::getOneIn).reduce(1, (a, b) -> a * b);

                    var exp = CardRarity.values().length - 1 - rarity.ordinal();

                    var occurences = powerN(5, exp) * cols / mod;

                    CrateDrops.drops.add(new ItemDropPair(ic, occurences));
                    items.add(ic);
                });

            });

            fr.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static Item getByID(String iid)
    {
        return items.stream().filter(x -> iid.equalsIgnoreCase(x.id)).findFirst().orElse(null);
    }

    public Item(String id, String humanName)
    {
        this.id = id;
        this.humanName = humanName;
        this.buyValue = -1;
    }

    public Item setSellValue(long sellValue)
    {
        this.sellValue = sellValue;

        return this;
    }

    public boolean canBeBought()
    {
        return buyValue > 0;
    }

    public Item desc(String description)
    {
        this.description = description;

        return this;
    }

    public String getDescription()
    {
        return description;
    }

    public long getBuyValue()
    {
        return buyValue;
    }

    public Item setBuyValue(long buyValue)
    {
        this.buyValue = buyValue;

        return this;
    }

    public long getSellValue()
    {
        return sellValue;
    }

    public String getIcon()
    {
        return emoteIcon;
    }

    public boolean hasIcon()
    {
        return emoteIcon != null;
    }

    public String getID()
    {
        return id;
    }

    public Item setEmoteIcon(String emoteIcon)
    {
        this.emoteIcon = emoteIcon;

        return this;
    }

    public String getHumanName()
    {
        return humanName;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    protected void setID(String id)
    {
        this.id = id;
    }

    public void setHumanName(String humanName)
    {
        this.humanName = humanName;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Item))
            return false;

        return ((Item) obj).getID().equals(this.getID());
    }

    @Override
    public int hashCode()
    {
        return this.getID().hashCode();
    }
}
