package committee.nova.stntr.client.network;

import committee.nova.stntr.common.api.IEntityTransformable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class ClientNetworkUtils {
    public static Runnable handleSync(int id, boolean converting, boolean shouldPlaySound) {
        return () -> {
            final World w = Minecraft.getInstance().level;
            if (w == null) return;
            final Entity e = w.getEntity(id);
            if (!(e instanceof LivingEntity)) return;
            if (!(e instanceof IEntityTransformable)) return;
            final LivingEntity l = (LivingEntity) e;
            ((IEntityTransformable) e).setConverting(converting);
            if (shouldPlaySound && !l.isSilent())
                w.playLocalSound(l.getX(), l.getEyeY(), l.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE,
                        l.getSoundSource(), 1.0F + l.getRandom().nextFloat(), l.getRandom().nextFloat() * 0.7F + 0.3F, false);
        };
    }
}
