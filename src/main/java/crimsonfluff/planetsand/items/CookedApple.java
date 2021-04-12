package crimsonfluff.planetsand.items;

import crimsonfluff.planetsand.PlanetSand;
import net.minecraft.item.Food;
import net.minecraft.item.Item;

public class CookedApple extends Item {
    public CookedApple() {
        super(new Properties()
            .tab(PlanetSand.TAB)
            .food(new Food.Builder()
                .nutrition(6)
                .saturationMod(0.6f)
                .build()
            )
        );
    }
}
