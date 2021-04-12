package crimsonfluff.planetsand.items;

import crimsonfluff.planetsand.PlanetSand;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class GoldenSteak extends Item {
    public GoldenSteak() {
        super(new Properties()
            .tab(PlanetSand.TAB)
            .stacksTo(1)
            .food(new Food.Builder()
                .nutrition(8)
                .saturationMod(0.8f)
                .meat()
                .build())
    ); }

    @Override
    public int getUseDuration(ItemStack stack) { return 2 * 20; }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("tip." + PlanetSand.MOD_ID + ".golden_steak").withStyle(TextFormatting.YELLOW));

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.finishUsingItem(stack.copy(), worldIn, entityLiving);

        if (entityLiving instanceof PlayerEntity)
            ((PlayerEntity) entityLiving).getCooldowns().addCooldown(this, 1200);  // 1min

        return stack;
    }
}
