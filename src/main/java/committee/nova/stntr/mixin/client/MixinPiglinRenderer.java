package committee.nova.stntr.mixin.client;

import committee.nova.stntr.common.api.IEntityTransformable;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.entity.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinRenderer.class)
public abstract class MixinPiglinRenderer {
    @Inject(method = "isShaking(Lnet/minecraft/entity/MobEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void inject$isShaking(MobEntity piglin, CallbackInfoReturnable<Boolean> cir) {
        if (!(piglin instanceof IEntityTransformable)) return;
        if (((IEntityTransformable) piglin).isConverting()) cir.setReturnValue(true);
    }
}
