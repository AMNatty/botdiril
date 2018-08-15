package cz.tefek.botdiril.userdata.items;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import cz.tefek.botdiril.sql.DB;
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
    public static final String KEK = "<:kek:472067325080633346>";
    public static final String GEM = "<:gemdiril:458631540952530944>";
    public static final String BETA = "<:beta:471338452865253386>";
    public static final String DUST = "<:dust:470276538009649179>";

    public static final String REDGEM = "<a:redgem:470692296359280650>";
    public static final String GREENGEM = "<a:greengem:470675656628240384>";
    public static final String PURPLEGEM = "<a:purplegem:470686075661320193>";
    public static final String BLACKGEM = "<a:blackgem:470668159704236033>";
    public static final String RAINBOWGEM = "<a:rainbowgem:471071618429354006>";
    public static final String BLUEGEM = "<a:bluegem:470672476880502784>";

    public static final List<Item> items = new ArrayList<>();

    private static final List<List<CardCollection>> crdc = CollectionCombination.all();

    private String id;
    private String emoteIcon;
    private String humanName;
    private long sellValue;
    private long buyValue;
    private String description;
    private long dropValue;

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

        items.add(new Item("betabadge", "Beta Tester's Badge.").setDescription("Given to every user who used Botdiril since the beginning.").setEmoteIcon(BETA));

        var dust = new Item("dust", "Arcane Dust").setDescription("Used for card enchanting.").setBuyValue(1).setDropValue(4).setEmoteIcon(DUST);

        var redGem = new Item("infernalgem", "Infernal Gem").setDescription("Unleash the fury upon your foes.").setSellValue(2000).setEmoteIcon(REDGEM);
        var greenGem = new Item("peacegem", "Gem of Peace").setDescription("Avoid conflicts.").setSellValue(2000).setEmoteIcon(GREENGEM);

        var blueGem = new Item("balancegem", "Gem of Equlibrium").setDescription("Remove any differences.").setSellValue(10000).setEmoteIcon(BLUEGEM);
        var purpleGem = new Item("imbalancegem", "Gem of Imbalance").setDescription("The source of imbalance permeating the universe.").setSellValue(10000).setEmoteIcon(PURPLEGEM);

        var rainbowGem = new Item("ordergem", "Gem of Order").setDescription("Natural enemy of chaos.").setSellValue(50000).setEmoteIcon(RAINBOWGEM);
        var blackGem = new Item("chaosgem", "Chaos Gem").setDescription("The source of all chaos.").setSellValue(50000).setEmoteIcon(BLACKGEM);

        items.add(redGem);
        CrateDrops.drops.add(new ItemDropPair(redGem, 100_000_000L));
        items.add(greenGem);
        CrateDrops.drops.add(new ItemDropPair(greenGem, 100_000_000L));
        items.add(blueGem);
        CrateDrops.drops.add(new ItemDropPair(blueGem, 20_000_000L));
        items.add(purpleGem);
        CrateDrops.drops.add(new ItemDropPair(purpleGem, 20_000_000L));
        items.add(rainbowGem);
        CrateDrops.drops.add(new ItemDropPair(rainbowGem, 4_000_000L));
        items.add(blackGem);
        CrateDrops.drops.add(new ItemDropPair(blackGem, 4_000_000L));

        items.add(dust);

        var kek = new Item("kek", "kek").desc("The kekest of keks.").setEmoteIcon(Item.KEK).setBuyValue(3).setSellValue(2).setDropValue(15);
        var gemdiril = new Item("gemdiril", "Gemdiril").setEmoteIcon(GEM).desc("A very rare gem of unknown value.").setDropValue(80000);

        items.add(kek);
        items.add(gemdiril);

        CrateDrops.drops.add(new ItemDropPair(kek, 100_000_000_000L));
        CrateDrops.drops.add(new ItemDropPair(gemdiril, 2_000_000L));
        // CrateDrops.drops.add(new ItemDropPair(dust, 5_000_000_000_000L));

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
                    ic.setDropValue(ic.getSellValue() * 75);

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

        DB.initItems();
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
        this.sellValue = -1;
        this.dropValue = 0;
    }

    public Item setSellValue(long sellValue)
    {
        this.sellValue = sellValue;

        if (this.dropValue == 0)
            this.dropValue = this.sellValue * 10;

        return this;
    }

    public boolean canBeBought()
    {
        return buyValue > 0;
    }

    public boolean canBeSold()
    {
        return sellValue >= 0;
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

    public Item setDescription(String description)
    {
        this.description = description;

        return this;
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

    public Item setDropValue(long dropValue)
    {
        this.dropValue = dropValue;

        return this;
    }

    public long getDropValue()
    {
        return this.dropValue;
    }
}
