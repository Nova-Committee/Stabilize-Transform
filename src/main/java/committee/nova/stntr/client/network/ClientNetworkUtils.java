package committee.nova.stntr.client.network;

import committee.nova.stntr.common.api.IEntityTransformable;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ClientNetworkUtils {
    public static Runnable handleSync(int id, boolean converting, boolean shouldPlaySound) {
        return () -> {
            final Level w = Minecraft.getInstance().level;
            if (w == null) return;
            final Entity e = w.getEntity(id);
            if (!(e instanceof final LivingEntity l)) return;
            if (!(e instanceof IEntityTransformable)) return;
            ((IEntityTransformable) e).setConverting(converting);
            if (shouldPlaySound && !l.isSilent())
                w.playLocalSound(l.getX(), l.getEyeY(), l.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE,
                        l.getSoundSource(), 1.0F + l.getRandom().nextFloat(), l.getRandom().nextFloat() * 0.7F + 0.3F, false);
        };
    }
}
