package crimsonfluff.planetsand.init;

import crimsonfluff.planetsand.PlanetSand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class soundsInit {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, PlanetSand.MOD_ID);

    public static final RegistryObject<SoundEvent> PLAYER_DEATH = SOUNDS.register("player.death",
            () -> new SoundEvent(new ResourceLocation(PlanetSand.MOD_ID, "player.death")));

    public static final RegistryObject<SoundEvent> PLAYER_RESPAWN = SOUNDS.register("player.respawn",
            () -> new SoundEvent(new ResourceLocation(PlanetSand.MOD_ID, "player.respawn")));
}
