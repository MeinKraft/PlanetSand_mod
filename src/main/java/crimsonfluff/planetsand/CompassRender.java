package crimsonfluff.planetsand;

import crimsonfluff.planetsand.init.itemsInit;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import javax.annotation.Nullable;

/**
  *  This class is, more-or-less, a direct copy of the EnderCompassAngleGetter class from
  *  @tfarecnim's Stronghold Compass mod
  *  @Licence: MIT
  *  @https://www.curseforge.com/minecraft/mc-mods/stronghold-compass
*/

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CompassRender implements IItemPropertyGetter {
    @SubscribeEvent
    public static void onItemModelsProperties(FMLClientSetupEvent event) {
        ItemModelsProperties.register(itemsInit.COMPASS_MINESHAFT.get(), new ResourceLocation("angle"), new CompassRender());
    }

    private double prevAngle = 0.0D;
    private double prevWobble = 0.0D;
    private long prevWorldTime = 0L;

    @Override
    public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity livingEntity) {
        boolean isLiving = livingEntity != null;

        if (!isLiving && !stack.isFramed() || !stack.hasTag() || !stack.getTag().contains("pos")) return 0;

        Entity entity = isLiving ? livingEntity : stack.getFrame();

        if (world == null) world = (ClientWorld) entity.level;

        int[] blockPos = stack.getTag().getIntArray("pos");
        double angle;
        double entityAngle = isLiving ? entity.getYHeadRot() : getFrameAngle((ItemFrameEntity) entity);

        entityAngle /= 360.0D;
        entityAngle = MathHelper.positiveModulo(entityAngle, 1.0D);
        double posAngle = getPosToAngle(blockPos, entity);
        posAngle /= Math.PI * 2D;
        angle = 0.5D - (entityAngle - 0.25D - posAngle);

        if (isLiving) angle = wobble(world, angle);

        return MathHelper.positiveModulo((float) angle, 1.0F);
    }

    private double wobble(World world, double angle) {
        long worldTime = world.getGameTime();

        if (worldTime != prevWorldTime) {
            prevWorldTime = worldTime;
            double angleDifference = angle - prevAngle;
            angleDifference = MathHelper.positiveModulo(angleDifference + 0.5D, 1.0D) - 0.5D;

            prevWobble += angleDifference * 0.1D;
            prevWobble *= 0.8D;
            prevAngle = MathHelper.positiveModulo(prevAngle + prevWobble, 1.0D);
        }

        return prevAngle;
    }

    private double getFrameAngle(ItemFrameEntity entity) {
        return MathHelper.wrapDegrees(180 + entity.getDirection().getStepY() * 90);
    }

    private double getPosToAngle(int[] pos, Entity entity) {
        return Math.atan2(pos[2] - entity.getZ(), pos[0] - entity.getX());
    }
}
