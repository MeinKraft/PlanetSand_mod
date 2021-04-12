package crimsonfluff.planetsand.items;

import crimsonfluff.planetsand.PlanetSand;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import java.util.Collections;
import java.util.Iterator;

public class PlantMeal extends Item {
    public PlantMeal() { super(new Item.Properties().tab(PlanetSand.TAB)); }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World worldIn = context.getLevel();

        if (worldIn.isClientSide) return ActionResultType.SUCCESS;

        BlockPos pos = context.getClickedPos();
        BlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();

        if (BoneMealItem.applyBonemeal(context.getItemInHand(), worldIn, pos, context.getPlayer())) {
            worldIn.levelEvent(2005, pos, 0);

            return ActionResultType.CONSUME;

        } else {
            BlockPos blockpos1 = pos.relative(context.getClickedFace());
            boolean flag = state.isFaceSturdy(worldIn, pos, context.getClickedFace());

            if (flag && BoneMealItem.growWaterPlant(context.getItemInHand(), worldIn, blockpos1, context.getClickedFace())) {
                worldIn.levelEvent(2005, pos, 0);

                return ActionResultType.CONSUME;
            }

            //context.getPlayer().sendMessage(new StringTextComponent("PlantMeal:"), Util.NIL_UUID);

            if (block instanceof IPlantable) {
                Property<?> property = null;
                int age = 0;
                int max = 0;
                boolean isOK = false;

                Iterator<Property<?>> itp = Collections.unmodifiableCollection(state.getValues().keySet()).iterator();

                if (!itp.hasNext()) {
                    // no age/properties so is a flower?

                    worldIn.levelEvent(2005, pos, 0);        // Spawn Bonemeal Particles
                    if (!context.getPlayer().abilities.instabuild) context.getItemInHand().shrink(1);

                    if (worldIn.random.nextInt(3) == 0) {
                        worldIn.destroyBlock(pos, true, context.getPlayer());
                        worldIn.setBlockAndUpdate(pos, block.defaultBlockState());
                    }

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
                    if (!context.getPlayer().abilities.instabuild) context.getItemInHand().shrink(1);

                    if (age < max) {
                        worldIn.setBlockAndUpdate(pos, state.cycle(property));

                    } else {
                        worldIn.destroyBlock(pos, true, context.getPlayer());
                        worldIn.setBlockAndUpdate(pos, block.defaultBlockState());
                    }

                    return ActionResultType.CONSUME;
                }
            }
        }

        return ActionResultType.PASS;
    }
}
