package crimsonfluff.planetsand.items;

import crimsonfluff.planetsand.PlanetSand;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemTinyCoal extends Item {
    public ItemTinyCoal() { super(new Item.Properties().group(PlanetSand.TAB)); }

    @Override
    public int getBurnTime(ItemStack itemStack) { return 200; }
}
