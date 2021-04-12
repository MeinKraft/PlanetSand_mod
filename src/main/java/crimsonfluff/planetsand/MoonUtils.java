package crimsonfluff.planetsand;

import net.minecraft.entity.*;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MoonUtils {
    public static Set<Chunk> getLoadedChunks(ServerWorld world, int spawnRadius) {
        if (spawnRadius == 0)
            spawnRadius = world.getServer().getPlayerList().getViewDistance();
        else
            spawnRadius = Integer.min(spawnRadius, world.getServer().getPlayerList().getViewDistance());

        Set<Chunk> loadedChunks = new HashSet<>();

        int finalSpawnRadius = spawnRadius;
        world.getServer().getPlayerList().getPlayers().forEach(player -> {
            ChunkPos playerChunkPos = new ChunkPos(player.blockPosition());

            for(int x = -finalSpawnRadius; x <= finalSpawnRadius; x++) {
                for(int z = -finalSpawnRadius; z <= finalSpawnRadius; z++) {
                    ChunkPos offsetChunkPos = new ChunkPos(playerChunkPos.x + x, playerChunkPos.z + z);
                    Chunk offsetChunk = world.getChunk(offsetChunkPos.x, offsetChunkPos.z);

                    loadedChunks.add(offsetChunk);
                }
            }
        });

        return loadedChunks;
    }


    public static Entity findMonsterToSpawn(ServerWorld world, BlockPos pos) {
        ServerChunkProvider scp = world.getChunkSource();
        List<MobSpawnInfo.Spawners> spawnOptions = scp.generator.getMobsAt(world.getBiome(pos), world.structureFeatureManager(), EntityClassification.MONSTER, pos);
        if (spawnOptions.size() == 0) return null;

        MobSpawnInfo.Spawners entry = WeightedRandom.getRandomItem(world.random, spawnOptions);

// ************************************************************************************
        // NOTE: Surely it has to be SPAWNER else they wont get "allowed"

        // NOTE: Setting to NATURAL means it ALWAYS fails no matter the mob type trying to spawn
        if (!EntitySpawnPlacementRegistry.checkSpawnRules(entry.type, world, SpawnReason.SPAWNER, pos, world.random)) {
            //PlanetSand.LOGGER.info("canSpawnEntity: FAILED: " + entry.type + " Y: " + pos.getY());
            return null;
        }
// ************************************************************************************

        EntityType<?> type = entry.type;
        Entity ent = type.create(world);
        if (ent instanceof MobEntity)
            ((MobEntity) ent).finalizeSpawn(world, world.getCurrentDifficultyAt(pos), SpawnReason.SPAWNER, null, null);

        return ent;
    }

    public static void dropExperience(World world, BlockPos pos, int xp) {
        while (xp > 0) {
            int i = ExperienceOrbEntity.getExperienceValue(xp);
            xp -= i;
            world.addFreshEntity(new ExperienceOrbEntity(world, pos.getX(), pos.getY(), pos.getZ(), i));
        }
    }
}
