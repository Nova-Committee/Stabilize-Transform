package committee.nova.stntr.mixin;

import committee.nova.stntr.StabilizeAndTransform;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(ZombieVillagerEntity.class)
public abstract class MixinZombieVillager extends ZombieEntity {
    @Shadow
    protected abstract void startConverting(@Nullable UUID p_191991_1_, int p_191991_2_);

    public MixinZombieVillager(EntityType<? extends ZombieEntity> e, World w) {
        super(e, w);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void redirect$mobInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResultType> cir) {
        final ItemStack stack = player.getItemInHand(hand);
        if (StabilizeAndTransform.canTransformZVillager(stack.getItem())) {
            if (this.hasEffect(Effects.WEAKNESS)) {
                if (!player.abilities.instabuild) stack.shrink(1);
                if (!this.level.isClientSide) this.startConverting(player.getUUID(), this.random.nextInt(2401) + 3600);
                cir.setReturnValue(ActionResultType.SUCCESS);
                return;
            }
            cir.setReturnValue(ActionResultType.CONSUME);
            return;
        }
        cir.setReturnValue(super.mobInteract(player, hand));
    }
}
