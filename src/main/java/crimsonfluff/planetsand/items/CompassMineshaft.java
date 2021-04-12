package crimsonfluff.planetsand.items;

import crimsonfluff.planetsand.PlanetSand;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class CompassMineshaft extends Item {
    public CompassMineshaft() { super(new Item.Properties().tab(PlanetSand.TAB).stacksTo(1)); }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

        if (!worldIn.isClientSide()) {
            playerIn.sendMessage(new TranslationTextComponent("tip." + PlanetSand.MOD_ID + ".searching"), Util.NIL_UUID);
            BlockPos blockPos = ((ServerWorld) worldIn).getChunkSource().generator.findNearestMapFeature((ServerWorld) worldIn, Structure.MINESHAFT, playerIn.blockPosition(), 100, false);

            if (blockPos != null) {
                stack.getOrCreateTag().putIntArray("pos", new int[] {blockPos.getX(), blockPos.getY(), blockPos.getZ()});
                playerIn.sendMessage(new TranslationTextComponent("tip." + PlanetSand.MOD_ID + ".found"),  Util.NIL_UUID);
                playerIn.playSound(SoundEvents.VILLAGER_YES, 1f, 1f);

            } else {
                playerIn.sendMessage(new TranslationTextComponent("tip." + PlanetSand.MOD_ID + ".notfound").withStyle(TextFormatting.GRAY),  Util.NIL_UUID);
                playerIn.playSound(SoundEvents.VILLAGER_NO, 1f, 1f);
            }
        }

        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTag() && stack.getTag().contains("pos")) {
            int[] xyz = stack.getTag().getIntArray("pos");

            tooltip.add(new StringTextComponent("Location x: " + xyz[0] + " , z: " + xyz[2]).withStyle(TextFormatting.GRAY));

        } else
            tooltip.add(new TranslationTextComponent("tip." + PlanetSand.MOD_ID + ".notfound").withStyle(TextFormatting.GRAY));
    }
}
