package committee.nova.stntr.mixin.client;

import committee.nova.stntr.common.api.IEntityTransformable;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingRenderer.class)
public abstract class MixinLivingRenderer<T extends LivingEntity> {
    @Inject(method = "isShaking", at = @At("HEAD"), cancellable = true)
    private void inject$isShaking(T living, CallbackInfoReturnable<Boolean> cir) {
        if (living instanceof IEntityTransformable && ((IEntityTransformable) living).isConverting())
            cir.setReturnValue(true);
    }
}
