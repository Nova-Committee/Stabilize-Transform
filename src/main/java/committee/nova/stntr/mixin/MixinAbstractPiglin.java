package committee.nova.stntr.mixin;

import committee.nova.stntr.StabilizeAndTransform;
import committee.nova.stntr.common.api.IEntityStabilizable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.piglin.AbstractPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractPiglinEntity.class)
public abstract class MixinAbstractPiglin extends MonsterEntity implements IEntityStabilizable {
    @Shadow
    @Final
    protected static DataParameter<Boolean> DATA_IMMUNE_TO_ZOMBIFICATION;

    protected MixinAbstractPiglin(EntityType<? extends MonsterEntity> p_i48553_1_, World p_i48553_2_) {
        super(p_i48553_1_, p_i48553_2_);
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
    public boolean canStabilize(PlayerEntity player, ItemStack stack) {
        return StabilizeAndTransform.canStabilizePiglin(stack.getItem());
    }
}
