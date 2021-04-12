package crimsonfluff.planetsand;
import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigBuilder {
    public final ForgeConfigSpec COMMON;

    public ForgeConfigSpec.BooleanValue enableVillagers;
    public ForgeConfigSpec.BooleanValue enableTraders;
    public ForgeConfigSpec.BooleanValue enableBats;
    public ForgeConfigSpec.BooleanValue enableWaterCreatures;
    public ForgeConfigSpec.BooleanValue enableAnimals;
    public ForgeConfigSpec.BooleanValue enableMobs;

    public ForgeConfigSpec.IntValue exhDesert;
    public ForgeConfigSpec.IntValue exhNether;

    public ForgeConfigSpec.BooleanValue infiniteWater;
    public ForgeConfigSpec.BooleanValue infiniteLava;
    public ForgeConfigSpec.BooleanValue infiniteFluids;

    public ForgeConfigSpec.BooleanValue dragonEggDrops;
    public ForgeConfigSpec.BooleanValue dragonXpDrops;

    public ForgeConfigSpec.BooleanValue moonSpawns;
    public ForgeConfigSpec.IntValue moonDistance;
    public ForgeConfigSpec.BooleanValue moonSpawnsKill;
    public ForgeConfigSpec.BooleanValue moonSleeping;
    public ForgeConfigSpec.BooleanValue moonBedSplosion;
    public ForgeConfigSpec.BooleanValue moonTeleporting;

    public ConfigBuilder() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Planet Sand Settings");
        builder.push("Natural Spawns");

// TODO: Can't seem to detect villagers/golems/animals when they are spawned in villages !
//       have a work-a-round in place

        enableVillagers = builder
            .comment("Should Villagers be allowed to spawn?")
            .define("enableVillagers", false);

        enableTraders = builder
            .comment("Should Wandering Traders be allowed to spawn?")
            .define("enableTraders", false);

        enableBats = builder
            .comment("Should Bats be allowed to spawn?")
            .define("enableBats", false);

        enableWaterCreatures = builder
            .comment("Should Water Creatures be allowed to spawn?  [Salmon, Cod, Squid, Dolphin etc]")
            .define("enableWaterCreatures", false);

        enableAnimals = builder
            .comment("Should Animals be allowed to spawn?  [Sheep, Pig, Cow, Turtle etc]")
            .define("enableAnimals", false);

        enableMobs = builder
            .comment("Should Mobs be allowed to spawn?  [Spider, Zombie etc]")
            .define("enableMobs", true);
        builder.pop();


        builder.push("Exhaustion");
        exhDesert = builder
            .comment("How much extra exhaustion lost, per second, while in a Desert biome?  Default: 1")
            .defineInRange("desertExhaustion", 1,0,10);

        exhNether = builder
            .comment("How much extra exhaustion lost, per second, while in the Nether?  Default: 2")
            .defineInRange("netherExhaustion", 2,0,10);
        builder.pop();


        builder.push("World Settings");
        infiniteWater = builder
            .comment("Allow infinite Water source blocks?  Default: false")
            .define("infiniteWater", false);

        infiniteLava = builder
            .comment("Allow infinite Lava source blocks?  Default: false")
            .define("infiniteLava", false);

        infiniteFluids = builder
            .comment("Allow other infinite fluid source blocks?  Default: false")
            .define("infiniteFluids", false);
        builder.pop();


        builder.push("Full Moon");
        moonSpawns = builder
            .comment("Increased mob spawns during full moons?  Default: true")
            .define("moonSpawns", true);

        moonDistance = builder
            .comment("Closest full moon mobs can spawn to player?  Default: 8 blocks")
            .defineInRange("moonDistance", 8,2,16);

        moonSpawnsKill = builder
            .comment("Mobs spawned during full moon die at Dawn?  Default: true  WIP!!")
            .define("moonSpawnsKill", true);

        moonSleeping = builder
            .comment("Allow sleeping during full moons?  Default: false")
            .define("moonSleeping", false);

        moonBedSplosion = builder
            .comment("Full moons cause beds to explode when trying to sleep?  Only Joking!?  Default: true")
            .define("moonBedSplosion", true);

        moonTeleporting = builder
            .comment("Allow teleporting from the Overworld during full moons?  Default: false")
            .define("moonTeleporting", false);
        builder.pop();


        builder.push("Dragons");
        dragonEggDrops = builder
            .comment("Every Dragon drops a Dragon Egg?  Default: true")
            .define("dragonEggDrops", true);

        dragonXpDrops = builder
            .comment("Every Dragon drops full xp?  Default: true")
            .define("dragonXPDrops", true);
        builder.pop();


        COMMON = builder.build();
    }
}
