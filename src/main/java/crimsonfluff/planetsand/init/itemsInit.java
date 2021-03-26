package crimsonfluff.planetsand.init;

import crimsonfluff.planetsand.PlanetSand;
import crimsonfluff.planetsand.items.GoldenFish;
import crimsonfluff.planetsand.items.GoldenSteak;
import crimsonfluff.planetsand.items.ItemTinyCoal;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class itemsInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PlanetSand.MOD_ID);

    // Items
    public static final RegistryObject<Item> TINY_COAL = ITEMS.register("tiny_coal", ItemTinyCoal::new);
    public static final RegistryObject<Item> TINY_CHARCOAL = ITEMS.register("tiny_charcoal", ItemTinyCoal::new);
    public static final RegistryObject<Item> GOLDEN_STEAK = ITEMS.register("golden_steak", GoldenSteak::new);
    public static final RegistryObject<Item> GOLDEN_FISH = ITEMS.register("golden_fish", GoldenFish::new);

    // Block Items
    public static final RegistryObject<Item> CHARCOAL_BLOCK = ITEMS.register("charcoal_block",
    () -> new BlockItem(blocksInit.CHARCOAL_BLOCK.get(), new Item.Properties().group(PlanetSand.TAB)) {
        @Override
        public int getBurnTime(ItemStack itemStack) { return 14400; }
    });
}
