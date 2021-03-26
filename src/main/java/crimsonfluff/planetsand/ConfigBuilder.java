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
    public ForgeConfigSpec.IntValue moreHusks;

    public ConfigBuilder() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Planet Sand Settings");
        builder.push("Natural Spawns");

// TODO: Can't seem to detect villagers/golems/animals when they are spawned in villages !
// have a work-a-round in place
        enableVillagers = builder
            .comment("Should Villagers be allowed to spawn?  WIP!!")
            .define("enableVillagers", false);

        enableTraders = builder
                .comment("Should Wandering Traders be allowed to spawn?  WIP!!")
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

        moreHusks = builder
                .comment("Chance for Zombies to be spawned as Husks?  Default: 10")
                .defineInRange("moreHusks", 10,0,100);
        builder.pop();

        COMMON = builder.build();
    }
}
