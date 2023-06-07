package committee.nova.stntr.common.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IEntityStabilizable {
    boolean canStabilize(Player player, ItemStack stack);

    boolean isImmuneToConversion();

    void setImmuneToConversion(boolean immune);
}
