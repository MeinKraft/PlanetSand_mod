package crimsonfluff.planetsand;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class DeathEffect extends Effect {
    public DeathEffect(EffectType typeIn, int liquidColorIn) {
            super(typeIn, liquidColorIn);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) { return duration == 1; }

    @Override
    public void applyEffectTick(LivingEntity entityLivingBaseIn, int amplifier) {
        if (entityLivingBaseIn.level.isClientSide) return;

        entityLivingBaseIn.kill();
    }
}
