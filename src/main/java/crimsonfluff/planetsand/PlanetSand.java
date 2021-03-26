package crimsonfluff.planetsand;

import crimsonfluff.planetsand.init.blocksInit;
import crimsonfluff.planetsand.init.itemsInit;
import net.minecraft.entity.*;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.passive.horse.TraderLlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PlanetSand.MOD_ID)
public class PlanetSand {
    private final ConfigBuilder CONFIGURATION = new ConfigBuilder();;

    public static final String MOD_ID = "planetsand";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    final IEventBus MOD_EVENTBUS = FMLJavaModLoadingContext.get().getModEventBus();

    public int tick;

    public PlanetSand() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIGURATION.COMMON);
        
        blocksInit.BLOCKS.register(MOD_EVENTBUS);
        itemsInit.ITEMS.register(MOD_EVENTBUS);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static final ItemGroup TAB = new ItemGroup(PlanetSand.MOD_ID) {
        @OnlyIn(Dist.CLIENT)
        @Override
        public ItemStack createIcon() { return new ItemStack(itemsInit.TINY_COAL.get()); }
    };


    @SubscribeEvent
    public void playerTickHandler(TickEvent.PlayerTickEvent event) {
        if (event.player.getEntityWorld() instanceof ServerWorld) {
            if (event.phase == TickEvent.Phase.START) {
                PlayerEntity playerIn = event.player;
                World worldIn = playerIn.world;

                tick++;
                if (tick == 20) {
                    tick = 0;

                    if (CONFIGURATION.exhDesert.get() != 0) {
                        if (worldIn.getDimensionKey().equals(DimensionType.OVERWORLD)) {
                            if (worldIn.isDaytime()) {
                                if (worldIn.getBiome(playerIn.getPosition()).getCategory() == Biome.Category.DESERT) {
                                    //PlanetSand.LOGGER.info("PlayerTick: OVERWORLD");
                                    playerIn.addExhaustion(CONFIGURATION.exhDesert.get()/10f);
                                }
                            }
                        }
                    }

                    if (CONFIGURATION.exhNether.get() != 0) {
                        if (worldIn.getBiome(playerIn.getPosition()).getCategory() == Biome.Category.NETHER) {
                            //PlanetSand.LOGGER.info("PlayerTick: NETHER");
                            playerIn.addExhaustion(CONFIGURATION.exhNether.get()/10f);
                        }
                    }
                }
            }
        }
    }


// SpawnReason.SPAWNER is sent to Both Methods
//    @SubscribeEvent
//    public void checkSpawn (LivingSpawnEvent.CheckSpawn event) { }

    @SubscribeEvent
    public void specialSpawn (LivingSpawnEvent.SpecialSpawn event) {
        switch (event.getSpawnReason()) {
            case CHUNK_GENERATION:
            case NATURAL:
            case PATROL:
                break;

            default:
                //PlanetSand.LOGGER.info("SpecialSpawn: " + event.getEntity().getType() + " : " + event.getSpawnReason());
                event.getEntity().getPersistentData().putBoolean("allowed", true);
        }
    }

    @SubscribeEvent
    public void entityJoinWorld (EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote()) return;

    // fireballs, paintings, ender pearls, snowballs, minecarts, etc
    // FallingBlock, PlayerEntity
    // Villagers are the only MISC we need to worry about. Maybe Snow/Golems?
        if (event.getEntity().getClassification(false) == EntityClassification.MISC)
            if (event.getEntity().getType() != EntityType.VILLAGER) return;

        if (!event.getEntity().getPersistentData().getBoolean("allowed")) {
            if (event.getEntity().getType() == EntityType.WANDERING_TRADER)
                if (CONFIGURATION.enableTraders.get()) return;

            // NOTE: Must allow babies else breeding won't work when enableVillagers = false
            if (event.getEntity().getType() == EntityType.VILLAGER) {
                VillagerEntity ve = (VillagerEntity) event.getEntity();
                if (ve.isChild()) {
                    ve.getPersistentData().putBoolean("allowed", true);
                    return;
                } else if (CONFIGURATION.enableVillagers.get()) return;
            }

            // Turtle, Cow, Pig, Sheep
            // NOTE: Must allow babies else breeding won't work when enableAnimals = false
            if (event.getEntity().getClassification(false) == EntityClassification.CREATURE) {
                AnimalEntity ae = (AnimalEntity) event.getEntity();
                if (ae.isChild()) {
                    ae.getPersistentData().putBoolean("allowed", true);
                    return;
                } else if (CONFIGURATION.enableAnimals.get()) return;
            }

            if (CONFIGURATION.enableMobs.get()) {
                if (event.getEntity().getClassification(false) == EntityClassification.MONSTER) {
                    if (event.getEntity().getType() == EntityType.ZOMBIE) {
                        if (CONFIGURATION.moreHusks.get() != 0) {
                            int r = event.getWorld().rand.nextInt(100);
                            if (r == 0) return;
                            if (r > CONFIGURATION.moreHusks.get()) return;

                            event.setCanceled(true);
                            event.getEntity().remove();

                            Entity ent = EntityType.HUSK.spawn((ServerWorld) event.getWorld(), null, null, event.getEntity().getPosition(), SpawnReason.EVENT, false, false);
                            if (ent != null) ent.getPersistentData().putBoolean("allowed", true);
                        }
                    }

                    return;
                }
            }

            // Fish, Cod, Salmon, Puffer, Tropical
            if (event.getEntity().getClassification(false) == EntityClassification.WATER_AMBIENT)
                if (CONFIGURATION.enableWaterCreatures.get()) return;

            // Squid, Dolphin
            if (event.getEntity().getClassification(false) == EntityClassification.WATER_CREATURE)
                if (CONFIGURATION.enableWaterCreatures.get()) return;

        // could use == EntityClassification.AMBIENT as Bat is only type of this class
            if (event.getEntity().getType() == EntityType.BAT)
                if (CONFIGURATION.enableBats.get()) return;


            event.setCanceled(true);
            event.getEntity().remove();
            //PlanetSand.LOGGER.info("JoinWorld: FORCED REMOVE: " + event.getEntity().toString());
        }
    }


    @SubscribeEvent
    public void createFluidSourceEvent(BlockEvent.CreateFluidSourceEvent event) {
        Fluid fluid = event.getState().getFluidState().getFluid();
        if (fluid == Fluids.WATER) {
            event.setResult(CONFIGURATION.infiniteWater.get() ? Event.Result.ALLOW : Event.Result.DENY);
            return;
        }
        if (fluid == Fluids.LAVA) {
            event.setResult(CONFIGURATION.infiniteLava.get() ? Event.Result.ALLOW : Event.Result.DENY);
            return;
        }

        event.setResult(CONFIGURATION.infiniteFluids.get() ? Event.Result.ALLOW : Event.Result.DENY);
    }
}
