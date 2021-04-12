package crimsonfluff.planetsand.init;

import crimsonfluff.planetsand.DeathEffect;
import crimsonfluff.planetsand.PlanetSand;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class potionsInit {
    public static final DeferredRegister<Effect> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, PlanetSand.MOD_ID);
    public static final DeferredRegister<Potion> POTION_EFFECTS = DeferredRegister.create(ForgeRegistries.POTION_TYPES, PlanetSand.MOD_ID);

// DeathClock, Kill the player and used to kill the Angry Bees after 30 seconds
    public static final RegistryObject<Effect> DEATHCLOCK_POTION = POTIONS.register("death",
            () -> new DeathEffect(EffectType.HARMFUL, 37848743));

    public static final RegistryObject<Potion> DEATHCLOCK_EFFECT = POTION_EFFECTS.register("death",
            () -> new Potion(new EffectInstance(DEATHCLOCK_POTION.get(), 1200)));

}
