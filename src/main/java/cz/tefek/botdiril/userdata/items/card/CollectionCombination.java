package cz.tefek.botdiril.userdata.items.card;

import java.util.ArrayList;
import java.util.List;

public class CollectionCombination
{
    public static List<List<CardCollection>> all()
    {
        List<List<CardCollection>> total = new ArrayList<>();

        for (int i = 0; i < 1 << CardCollection.values().length; i++)
        {
            var temp = new ArrayList<CardCollection>();

            for (int j = 0; j < CardCollection.values().length; j++)
            {
                if (((1 << j) & i) > 0)
                    temp.add(CardCollection.values()[j]);
            }

            total.add(temp);
        }

        return total;
    }
}
