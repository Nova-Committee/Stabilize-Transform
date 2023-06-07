package committee.nova.stntr.mixin;

import committee.nova.stntr.StabilizeAndTransform;
import committee.nova.stntr.common.api.IEntityStabilizable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Hoglin.class)
public abstract class MixinHoglin extends Animal implements IEntityStabilizable {

    @Shadow
    @Final
    private static EntityDataAccessor<Boolean> DATA_IMMUNE_TO_ZOMBIFICATION;

    protected MixinHoglin(EntityType<? extends Animal> p_i48568_1_, Level p_i48568_2_) {
        super(p_i48568_1_, p_i48568_2_);
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
        return StabilizeAndTransform.canStabilizeHoglin(stack);
    }
}
