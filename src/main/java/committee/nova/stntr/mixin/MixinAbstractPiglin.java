package committee.nova.stntr.mixin;

import committee.nova.stntr.StabilizeAndTransform;
import committee.nova.stntr.common.api.IEntityStabilizable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractPiglin.class)
public abstract class MixinAbstractPiglin extends Monster implements IEntityStabilizable {
    @Shadow
    @Final
    protected static EntityDataAccessor<Boolean> DATA_IMMUNE_TO_ZOMBIFICATION;

    protected MixinAbstractPiglin(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    @Override
    public boolean isImmuneToConversion() {
        return getEntityData().get(DATA_IMMUNE_TO_ZOMBIFICATION);
    }

    @Override
    public void setImmuneToConversion(boolean immune) {
        getEntityData().set(DATA_IMMUNE_TO_ZOMBIFICATION, immune);
    }

    @Override
    public boolean canStabilize(Player player, ItemStack stack) {
        return StabilizeAndTransform.canStabilizePiglin(stack);
    }
}
