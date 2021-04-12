package crimsonfluff.planetsand.items;

import crimsonfluff.planetsand.PlanetSand;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class GoldenFish extends Item {
    public GoldenFish() {
        super(new Properties()
            .tab(PlanetSand.TAB)
            .stacksTo(1)
//            .food(new Food.Builder()
//                .hunger(6)
//                .saturation(0.8f)
//                .build())
        );
    }

//    @Override
//    public int getUseDuration(ItemStack stack) { return 2 * 20; }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("tip." + PlanetSand.MOD_ID + ".golden_fish").withStyle(TextFormatting.YELLOW));

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

//    @Override
//    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
//        super.onItemUseFinish(stack.copy(), worldIn, entityLiving);
//
//        if (entityLiving instanceof PlayerEntity)
//            ((PlayerEntity) entityLiving).getCooldownTracker().setCooldown(this, 20 * 60);
//
//        return stack;
//    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World worldIn = context.getLevel();

        return ActionResultType.CONSUME;
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);

        return ActionResult.consume(itemstack);
    }
}
