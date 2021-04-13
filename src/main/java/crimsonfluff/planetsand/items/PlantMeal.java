package crimsonfluff.planetsand.items;

import crimsonfluff.planetsand.PlanetSand;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import java.util.Collections;
import java.util.Iterator;

public class PlantMeal extends Item {
    public PlantMeal() { super(new Item.Properties().tab(PlanetSand.TAB)); }

    // NOTE: Beware when testing: Reap, PamsHc2Crops have this feature!
    //    -> Pams was stopping RightClick on not fully grown crops !!
    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if (context.getLevel().isClientSide) return ActionResultType.SUCCESS;

        World worldIn = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();
        ItemStack item;

        // for some reason Creative mode shrinks the itemstack, so pass a copy
        if (context.getPlayer().abilities.instabuild)
            item = context.getItemInHand().copy();
        else
            item = context.getItemInHand();

        //event.getPlayer().sendMessage(new StringTextComponent("Here"), Util.NIL_UUID);

        if (BoneMealItem.applyBonemeal(item, worldIn, pos, context.getPlayer())) {
            worldIn.levelEvent(2005, pos, 0);
            //context.getPlayer().sendMessage(new StringTextComponent("BONE MEAL"), Util.NIL_UUID);

            return ActionResultType.CONSUME;

        } else {
            BlockPos blockpos1 = pos.relative(context.getClickedFace());
            boolean flag = state.isFaceSturdy(worldIn, pos, context.getClickedFace());

            if (flag && BoneMealItem.growWaterPlant(item, worldIn, blockpos1, context.getClickedFace())) {
                worldIn.levelEvent(2005, pos, 0);

                return ActionResultType.CONSUME;
            }
        }

        // if here then Vanilla BoneMeal failed so...
        if ((block instanceof IPlantable) || (block instanceof IGrowable)) {
            Property<?> property = null;
            int age = 0;
            int max = 0;
            boolean isOK = false;

            Iterator<Property<?>> itp = Collections.unmodifiableCollection(state.getValues().keySet()).iterator();

            if (!itp.hasNext()) {
                // no age/properties so is a flower?

                worldIn.levelEvent(2005, pos, 0);        // Spawn Bonemeal Particles
                if (!context.getPlayer().abilities.instabuild) item.shrink(1);

                if (worldIn.random.nextInt(3) == 0) {
                    worldIn.destroyBlock(pos, true, context.getPlayer());
                    worldIn.setBlockAndUpdate(pos, block.defaultBlockState());
                }
                //context.getPlayer().sendMessage(new StringTextComponent("PlantMeal: No Age"), Util.NIL_UUID);

                return ActionResultType.CONSUME;

            } else {
                while (itp.hasNext()) {
                    property = itp.next();

                    if (property instanceof IntegerProperty) {
                        IntegerProperty prop = (IntegerProperty) property;

                        if (prop.getName().equals("age")) {
                            Comparable<?> cv = state.getValues().get(property);

                            age = Integer.parseUnsignedInt(cv.toString());
                            max = Collections.max(prop.getPossibleValues());

                            isOK = true;
                            //context.getPlayer().sendMessage(new StringTextComponent("PlantMeal: Has AGE Property"), Util.NIL_UUID);
                            break;
                        }
                    }
                }
            }

            if (isOK) {
                worldIn.levelEvent(2005, pos, 0);        // Spawn Bonemeal Particles
                if (!context.getPlayer().abilities.instabuild) item.shrink(1);

                //MathHelper.nextInt(p_185529_1_.random, 2, 5);  // CropBlocks.getBonemealAgeIncrease
                if (age < max) {
                    int r = MathHelper.nextInt(worldIn.random, 1, 3);
                    if (age+r>max) r=max-age;
                    //context.getPlayer().sendMessage(new StringTextComponent("PlantMeal: AGE Increase: " + r), Util.NIL_UUID);

                    for (int a = 0; a < r; a++)
                        state = state.cycle(property);
                    worldIn.setBlockAndUpdate(pos, state);

                } else {
                    if (PlanetSand.CONFIGURATION.plantmealBreaksCrops.get()) {
                        worldIn.destroyBlock(pos, true, context.getPlayer());
                        worldIn.setBlockAndUpdate(pos, block.defaultBlockState());
                    }

                    //context.getPlayer().sendMessage(new StringTextComponent("PlantMeal: BreakBlock"), Util.NIL_UUID);
                }
            }
        }

        return ActionResultType.PASS;
    }
}
