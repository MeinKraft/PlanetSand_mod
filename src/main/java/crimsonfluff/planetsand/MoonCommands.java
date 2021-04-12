package crimsonfluff.planetsand;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Set;

public class MoonCommands {
    public MoonCommands(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("planetsand").requires((p_198496_0_) -> {
            return p_198496_0_.hasPermission(2);
        })
            .then(Commands.literal("force_full_moon").executes(this::doFullMoon))
            .then(Commands.literal("remove_moon_spawns").executes(this::doKillMoonSpawns))
            .then(Commands.literal("remove_all_not_player").executes(this::doKillMobs))
        );
    }

    // NOTE: This method forwards time, so the number of days *will* increase !!
    private int doFullMoon(CommandContext<CommandSource> cscc) {
        long startTime = 12400;     // 18000;
        long amount;
        long moonPhase;
        long time;

        // Time Set command loops thru all worlds, so follow suit
        for(ServerWorld serverworld : cscc.getSource().getServer().getAllLevels()) {
            time = (serverworld.getDayTime() % 24000L);         // how far into current day are we
            amount = startTime - time;                          // difference between current time and starting time (startTime)
            amount += serverworld.getDayTime();                 // forward/rewind clock to get to starting time (startTime)

            moonPhase = (amount / 24000L % 8L + 8L) % 8;        // DimensionType.getMoonPhase using our new day time (amount)

            amount += ((8 - moonPhase) * 24000);                // forward number of days necessary to get to new full moon

            serverworld.setDayTime(amount);                     // make it so
        }

        return 0;
    }

    private int doKillMoonSpawns(CommandContext<CommandSource> cscc) {
        AxisAlignedBB area;
        ServerWorld worldIn = cscc.getSource().getLevel();
        Set<Chunk> loadedChunks = MoonUtils.getLoadedChunks(worldIn, 0);

        int a = 0;

        for (Chunk chunk : loadedChunks) {
            area = new AxisAlignedBB(chunk.getPos().getMinBlockX(), 1, chunk.getPos().getMinBlockZ(), chunk.getPos().getMaxBlockX(), 256, chunk.getPos().getMaxBlockZ());

            List<MobEntity> mobEntities = worldIn.getEntitiesOfClass(MobEntity.class, area);
            a += mobEntities.size();
            mobEntities.forEach(MobEntity::remove);
        }

        PlayerEntity player = (PlayerEntity) cscc.getSource().getEntity();
        player.sendMessage(new StringTextComponent("Removed " + a + " FullMoon mobs"), Util.NIL_UUID);
        player.sendMessage(new StringTextComponent("from " + loadedChunks.size() + " loaded chunks"), Util.NIL_UUID);

        return 0;
    }

    private int doKillMobs(CommandContext<CommandSource> cscc) {
        AxisAlignedBB area;
        ServerWorld worldIn = cscc.getSource().getLevel();
        Set<Chunk> loadedChunks = MoonUtils.getLoadedChunks(worldIn, 0);

        int a = 0;
        int b = 0;

        for (Chunk chunk : loadedChunks) {
            area = new AxisAlignedBB(chunk.getPos().getMinBlockX(), 1, chunk.getPos().getMinBlockZ(), chunk.getPos().getMaxBlockX(), 256, chunk.getPos().getMaxBlockZ());

            List<MobEntity> mobEntities = worldIn.getEntitiesOfClass(MobEntity.class, area);
            a += mobEntities.size();
            mobEntities.forEach(MobEntity::remove);

            List<ItemEntity> itemEntities = worldIn.getEntitiesOfClass(ItemEntity.class, area);
            b += itemEntities.size();
            itemEntities.forEach(ItemEntity::remove);
        }

        PlayerEntity player = (PlayerEntity) cscc.getSource().getEntity();
        player.sendMessage(new StringTextComponent("Removed " + a + " mobs"), Util.NIL_UUID);
        player.sendMessage(new StringTextComponent("and " + b + " items"), Util.NIL_UUID);
        player.sendMessage(new StringTextComponent("from " + loadedChunks.size() + " loaded chunks"), Util.NIL_UUID);

        return 0;
    }
}
