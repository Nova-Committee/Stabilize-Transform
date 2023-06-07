package committee.nova.stntr.mixin;

import committee.nova.stntr.StabilizeAndTransform;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(ZombieVillager.class)
public abstract class MixinZombieVillager extends Zombie {
    @Shadow
    protected abstract void startConverting(@Nullable UUID p_191991_1_, int p_191991_2_);

    public MixinZombieVillager(EntityType<? extends Zombie> e, Level w) {
        super(e, w);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void redirect$mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        final ItemStack stack = player.getItemInHand(hand);
        if (StabilizeAndTransform.canTransformZVillager(stack)) {
            if (this.hasEffect(MobEffects.WEAKNESS)) {
                if (!player.getAbilities().instabuild) stack.shrink(1);
                if (!this.level.isClientSide) this.startConverting(player.getUUID(), this.random.nextInt(2401) + 3600);
                cir.setReturnValue(InteractionResult.SUCCESS);
                return;
            }
            cir.setReturnValue(InteractionResult.CONSUME);
            return;
        }
        cir.setReturnValue(super.mobInteract(player, hand));
    }
}
