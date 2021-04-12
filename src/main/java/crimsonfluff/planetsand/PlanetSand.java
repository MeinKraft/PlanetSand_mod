package crimsonfluff.planetsand;

import crimsonfluff.planetsand.init.blocksInit;
import crimsonfluff.planetsand.init.itemsInit;
import crimsonfluff.planetsand.init.potionsInit;
import crimsonfluff.planetsand.init.soundsInit;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.passive.horse.TraderLlamaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Set;

@Mod(PlanetSand.MOD_ID)
public class PlanetSand {
    private final ConfigBuilder CONFIGURATION = new ConfigBuilder();

    public static final String MOD_ID = "planetsand";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    final IEventBus MOD_EVENTBUS = FMLJavaModLoadingContext.get().getModEventBus();

    public int tickPlayer;
    public int tickWorld = 20;

    public PlanetSand() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIGURATION.COMMON);

        soundsInit.SOUNDS.register(MOD_EVENTBUS);       // NOTE: Remember the sounds.json file !!
        blocksInit.BLOCKS.register(MOD_EVENTBUS);
        itemsInit.ITEMS.register(MOD_EVENTBUS);
        potionsInit.POTIONS.register(MOD_EVENTBUS);
        potionsInit.POTION_EFFECTS.register(MOD_EVENTBUS);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static final ItemGroup TAB = new ItemGroup(PlanetSand.MOD_ID) {
        @OnlyIn(Dist.CLIENT)
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(itemsInit.TINY_COAL.get());
        }
    };

    @SubscribeEvent
    public void onCommandsRegister(RegisterCommandsEvent event) { new MoonCommands(event.getDispatcher()); }


    // TODO: Maybe if can seek sky then take exhaustion, so basically if outside?
    // TODO: No exhaustion if in water?
    @SubscribeEvent
    public void onPlayerTickHandler(TickEvent.PlayerTickEvent event) {
        if (event.player.level.isClientSide) return;

        if (event.phase == TickEvent.Phase.START) {
            tickPlayer++;

            if (tickPlayer == 20) {
                tickPlayer = 0;

                PlayerEntity playerIn = event.player;
                World worldIn = playerIn.level;

                if (CONFIGURATION.exhDesert.get() != 0) {
                    if (worldIn.dimension() == World.OVERWORLD) {
                        if (worldIn.isDay()) {
                            // Temperature(): >1 savanna, > 2 Hot,
                            if (worldIn.getBiome(playerIn.blockPosition()).getTemperature(playerIn.blockPosition()) >= 2.0f)
                                playerIn.getFoodData().addExhaustion(CONFIGURATION.exhDesert.get() / 10f);
                        }
                    }
                }

                if (CONFIGURATION.exhNether.get() != 0) {
                    if (worldIn.getBiome(playerIn.blockPosition()).getBiomeCategory() == Biome.Category.NETHER) {
                        // Precaution: Sure BYG adds cold biomes to the Nether, not sure Temperature() tho?
                        if (worldIn.getBiome(playerIn.blockPosition()).getTemperature(playerIn.blockPosition()) >= 2.0f)
                            playerIn.getFoodData().addExhaustion(CONFIGURATION.exhNether.get() / 10f);
                    }
                }
            }
        }
    }


    // Yes = SPAWNER
    // No  = SPAWN_EGG, BREEDING
//    @SubscribeEvent
//    public void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
//        //PlanetSand.LOGGER.info("CheckSpawn: " + event.getSpawnReason() + " : " +  event.getEntity());
//    }

    // Yes = SPAWN_EGG, SPAWNER
    // No  = BREEDING
    @SubscribeEvent
    public void onSpecialSpawn(LivingSpawnEvent.SpecialSpawn event) {
        //PlanetSand.LOGGER.info("SpecialSpawn: " + event.getSpawnReason() + " : " +  event.getEntity());

        if (event.getSpawnReason() == SpawnReason.SPAWN_EGG || event.getSpawnReason() == SpawnReason.SPAWNER)
            event.getEntity().getPersistentData().putBoolean("allowed", true);
    }

// because villages spawn whatever they want and bypass CheckSpawn/SpecialSpawn
// we must cancel Animals/Villagers manually
// yet still allow Spawners/SpawnEggs/Breeding
    @SubscribeEvent()
    public void onBabyEntitySpawnEvent(BabyEntitySpawnEvent event) {
        // already on server thread
        event.getChild().getPersistentData().putBoolean("allowed", true);
    }

    @SubscribeEvent()
    public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        if (event.getWorld().isClientSide) return;

        // fireballs, paintings, ender pearls, snowballs, minecarts, FallingBlock, PlayerEntity etc
        // Villagers are the only MISC we need to worry about. Maybe Snow/Golems?
        if (event.getEntity().getClassification(false) == EntityClassification.MISC)
            if (event.getEntity().getType() != EntityType.VILLAGER) return;

        if (event.getEntity().getPersistentData().getBoolean("allowed")) return;


        // TODO: Cancel Mobs ?!  Just in case
        if (event.getEntity().getClassification(false) == EntityClassification.MONSTER) event.setCanceled(!CONFIGURATION.enableMobs.get());

        if (event.getEntity() instanceof WanderingTraderEntity) event.setCanceled(!CONFIGURATION.enableTraders.get());
        if (event.getEntity() instanceof TraderLlamaEntity) event.setCanceled(!CONFIGURATION.enableTraders.get());
        if (event.getEntity() instanceof VillagerEntity) event.setCanceled(!CONFIGURATION.enableVillagers.get());
        if (event.getEntity().getClassification(false) == EntityClassification.CREATURE)
            event.setCanceled(!CONFIGURATION.enableAnimals.get());
    }


    @SubscribeEvent
    public void onAllowDespawn(LivingSpawnEvent.AllowDespawn event) {
        //PlanetSand.LOGGER.info("AllowDespawn: " + event.getSpawnReason() + " : " +  event.getEntity());
        if (event.getEntity().getPersistentData().getBoolean("moonspawn"))
            if (!CONFIGURATION.moonSpawnsKill.get())
                event.setResult(Event.Result.DENY);
            else
                event.setResult(((ServerWorld) event.getWorld()).isNight() ? Event.Result.DENY : Event.Result.DEFAULT);
    }


    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBiomeLoadingEvent(BiomeLoadingEvent event) {
        if (!CONFIGURATION.enableMobs.get()) {
            List<MobSpawnInfo.Spawners> spawns = event.getSpawns().getSpawner(EntityClassification.MONSTER);
            spawns.clear();

        } else {
            List<MobSpawnInfo.Spawners> spawns = event.getSpawns().getSpawner(EntityClassification.MONSTER);

            if (spawns.removeIf(e -> e.type == EntityType.HUSK))
                spawns.add(new MobSpawnInfo.Spawners(EntityType.HUSK, 200, 4, 4));
            if (spawns.removeIf(e -> e.type == EntityType.SKELETON))
                spawns.add(new MobSpawnInfo.Spawners(EntityType.SKELETON, 50, 1, 1));
            if (spawns.removeIf(e -> e.type == EntityType.SPIDER))
                spawns.add(new MobSpawnInfo.Spawners(EntityType.SPIDER, 50, 1, 1));
        }
        if (!CONFIGURATION.enableWaterCreatures.get()) {
            List<MobSpawnInfo.Spawners> spawns = event.getSpawns().getSpawner(EntityClassification.WATER_AMBIENT);
            spawns.clear();

            spawns = event.getSpawns().getSpawner(EntityClassification.WATER_CREATURE);
            spawns.clear();
        }
        if (!CONFIGURATION.enableAnimals.get()) {
            List<MobSpawnInfo.Spawners> spawns = event.getSpawns().getSpawner(EntityClassification.CREATURE);
            spawns.clear();
        }
        if (!CONFIGURATION.enableBats.get()) {
            List<MobSpawnInfo.Spawners> spawns = event.getSpawns().getSpawner(EntityClassification.AMBIENT);
            spawns.removeIf(e -> e.type == EntityType.BAT);     // just make sure in case AMBIENT adds new Entities in future
        }
    }


    @SubscribeEvent
    public void onCreateFluidSourceEvent(BlockEvent.CreateFluidSourceEvent event) {
        Fluid fluid = event.getState().getFluidState().getType();
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


    @SubscribeEvent
    public void onPlayerSleepInBedEvent(PlayerSleepInBedEvent event) {
        if (!CONFIGURATION.moonSleeping.get()) {
            PlayerEntity playerIn = event.getPlayer();
            World worldIn = playerIn.level;

            if (worldIn.getMoonPhase() == 0) {
                if (worldIn.dimension() == World.OVERWORLD) {
                    if (worldIn.isNight()) {
                        event.setResult(PlayerEntity.SleepResult.OTHER_PROBLEM);

                        playerIn.sendMessage(new TranslationTextComponent("tip." + PlanetSand.MOD_ID + ".moonnosleep"), Util.NIL_UUID);
                        worldIn.playSound(null, playerIn, SoundEvents.VILLAGER_NO, SoundCategory.PLAYERS, 1f, 1f);
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public void onWorldTickEvent(TickEvent.WorldTickEvent event) {
        if (event.world.isClientSide) return;

        if (CONFIGURATION.moonSpawns.get()) {
            if (event.phase == TickEvent.Phase.END) {
                tickWorld--;

                if (tickWorld == 0) {
                    World worldIn = event.world;
                    tickWorld = (worldIn.random.nextInt(5) + 1) * 20;      // max 5 seconds, minimum 1 second

                    if (worldIn.dimension() == World.OVERWORLD) {
                        long gdt = worldIn.getDayTime() % 24000L;
                        //PlanetSand.LOGGER.info("WorldTick: Time: " + gdt);

                        if (gdt>13000 && gdt<23000) {
                            if (worldIn.getMoonPhase() == 0) {
                                //PlanetSand.LOGGER.info("WorldTick: Spawn a mob");

                                int totMobs = 0;
                                AxisAlignedBB area;
                                Set<Chunk> loadedChunks = MoonUtils.getLoadedChunks((ServerWorld) worldIn, 2);

                                for (Chunk chunk : loadedChunks) {
                                    area = new AxisAlignedBB(chunk.getPos().getMinBlockX(), 1, chunk.getPos().getMinBlockZ(), chunk.getPos().getMaxBlockX(), 256, chunk.getPos().getMaxBlockZ());
                                    totMobs += worldIn.getEntitiesOfClass(MobEntity.class, area).size();
                                }
                                //PlanetSand.LOGGER.info("WorldTick: MobCount: " + totMobs);

                                if (totMobs >= 150) return;

                                int r = CONFIGURATION.moonDistance.get();
                                loadedChunks.forEach(chunk -> {
                                    ChunkPos chunkPos = chunk.getPos();
                                    int randomX = chunkPos.getMinBlockX() + worldIn.random.nextInt(16);
                                    int randomZ = chunkPos.getMinBlockZ() + worldIn.random.nextInt(16);
                                    int y = worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, randomX, randomZ);

                                    BlockPos pos2 = new BlockPos(randomX, y, randomZ);
                                    Entity en = MoonUtils.findMonsterToSpawn((ServerWorld) worldIn, pos2);

                                    if (en != null) {
                                        AxisAlignedBB areaPlayer = new AxisAlignedBB(randomX - r, y - r, randomZ - r, randomX + r, y + r, randomZ + r);

                                        if (worldIn.getEntitiesOfClass(PlayerEntity.class, areaPlayer).size() == 0) {
                                            en.setPos(randomX, y, randomZ);

//                                            en.setCustomName(new StringTextComponent("Moon"));
//                                            en.setCustomNameVisible(true);

                                            //((LivingEntity) en).addPotionEffect();

                                            if (CONFIGURATION.moonSpawnsKill.get())
                                                en.getPersistentData().putBoolean("moonspawn", true);

                                            worldIn.addFreshEntity(en);
                                            //PlanetSand.LOGGER.info("WorldTick: Mob Added: " + en.getName().getString() + " ChunkX: " + chunkPos.getXStart()+ " ChunkZ: " + chunkPos.getZStart());
                                         }
                                         //else PlanetSand.LOGGER.info("WorldTick: SpawnMob: TooClose2Player");
                                    }
                                    //else PlanetSand.LOGGER.info("WorldTick: SpawnMob: Entity=null");
                                });

                                //PlanetSand.LOGGER.info("WorldTick: Mobs Added");
                            }

                        } else {
                            if (gdt > 23000 && gdt < 24000) {
                                if (CONFIGURATION.moonSpawnsKill.get()) {
                                    //PlanetSand.LOGGER.info("Working...");
                                    //mobDawnKillsComplete = true;

                                    AxisAlignedBB area;
                                    Set<Chunk> loadedChunks = MoonUtils.getLoadedChunks((ServerWorld) worldIn, 0);

                                    for (Chunk chunk : loadedChunks) {
                                        area = new AxisAlignedBB(chunk.getPos().getMinBlockX(), 1, chunk.getPos().getMinBlockZ(), chunk.getPos().getMaxBlockX(), 256, chunk.getPos().getMaxBlockZ());

                                        worldIn.getEntitiesOfClass(MobEntity.class, area, mob -> mob.getPersistentData().contains("moonspawn"))
                                                .forEach(mob -> mob.addEffect(new EffectInstance(potionsInit.DEATHCLOCK_POTION.get(),
                                                (worldIn.random.nextInt(10) + 1) * 20, 0, false, false)));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public void onEntityTravelToDimensionEvent(EntityTravelToDimensionEvent event) {
        if (!CONFIGURATION.moonTeleporting.get()) {
            if (event.getEntity() instanceof PlayerEntity) {
                World worldIn = event.getEntity().level;

                // if Destination is Overworld then is ok, cancel all other dimension travel
                if (worldIn.getMoonPhase() == 0) {
                    if (event.getDimension() != World.OVERWORLD) {
                        if (worldIn.isNight()) {
                            PlayerEntity playerIn = (PlayerEntity) event.getEntity();

                            playerIn.sendMessage(new TranslationTextComponent("tip." + PlanetSand.MOD_ID + ".moonnotp"), Util.NIL_UUID);
                            worldIn.playSound(null, playerIn, SoundEvents.VILLAGER_NO, SoundCategory.PLAYERS, 1f, 1f);

                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public void onLivingDeathEvent(LivingDeathEvent event) {
        if (event.getEntity() instanceof EnderDragonEntity) {
            EnderDragonEntity dragon = (EnderDragonEntity) event.getEntity();

            if (dragon.getDragonFight() != null && dragon.getDragonFight().hasPreviouslyKilledDragon()) {
                if (CONFIGURATION.dragonEggDrops.get())
                    dragon.level.setBlockAndUpdate(dragon.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION), Blocks.DRAGON_EGG.defaultBlockState());

                // First dragon kill drops 12000xp, 2nd onwards drops 500xp
                if (CONFIGURATION.dragonXpDrops.get())
                    if (dragon.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))
                        MoonUtils.dropExperience(dragon.level, dragon.blockPosition(), 11500);
            }

            return;
        }

        if (event.getEntity() instanceof PlayerEntity) {
            // below line: sounds get cut short
            //event.getEntity().level.playSound(null, event.getEntity(), soundsInit.PLAYER_DEATH.get(), SoundCategory.PLAYERS, 1f, 1f);

            // NOTE: the only one I found to play the sound without cutting it short
            BlockPos pos = event.getEntity().blockPosition();
            event.getEntity().level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), soundsInit.PLAYER_DEATH.get(), SoundCategory.PLAYERS,1f,1f);
        }
    }


    // RespawnEvent is NOT what we need ?
    // TODO: Fix this
    @SubscribeEvent
    public void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            //PlanetSand.LOGGER.info("Here:");
            //event.getPlayer().level.playSound(null, (PlayerEntity) event.getEntity(), soundsInit.PLAYER_RESPAWN.get(), SoundCategory.PLAYERS,1f,1f);

            //BlockPos pos = event.getPlayer().blockPosition();
            //BlockPos pos = ((ServerPlayerEntity)event.getPlayer()).getRespawnPosition();
            //event.getPlayer().playSound(soundsInit.PLAYER_RESPAWN.get(), 1f,1f);
            //event.getPlayer().level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), soundsInit.PLAYER_RESPAWN.get(), SoundCategory.PLAYERS,1f,1f);
            //event.getPlayer().level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), soundsInit.PLAYER_RESPAWN.get(), SoundCategory.PLAYERS,1f,1f);
            //event.getPlayer().level.playSound(null, pos, soundsInit.PLAYER_RESPAWN.get(), SoundCategory.PLAYERS, 1f,1f);
        }
    }
}
