package cz.tefek.botdiril.userdata.items.crate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;

import cz.tefek.botdiril.userdata.items.Item;

public class CrateDrops
{
    private static long total = 0;

    public static final List<ItemDropPair> drops = new ArrayList<ItemDropPair>() {

        /**
         * 
         */
        private static final long serialVersionUID = -3452945530560144807L;

        @Override
        public boolean add(ItemDropPair e)
        {
            if (super.add(e))
            {
                total += e.getRarity();
                return true;
            }

            return false;
        }
    };

    public static Item rollItem(long rngoffset, RandomDataGenerator rdg)
    {
        long l = 0;
        var r = rdg.nextLong(0, total + drops.size() * rngoffset);

        for (ItemDropPair entry : drops)
        {
            l += entry.getRarity() + rngoffset;

            if (l > r)
            {
                return entry.getItem();
            }
        }

        return null;
    }

    public static long getTotal()
    {
        return total;
    }
}
