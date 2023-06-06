package committee.nova.stntr.common.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface IEntityStabilizable {
    boolean canStabilize(PlayerEntity player, ItemStack stack);

    boolean isImmuneToConversion();

    void setImmuneToConversion(boolean immune);
}
