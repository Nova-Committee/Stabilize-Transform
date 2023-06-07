package committee.nova.stntr.common.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IEntityTransformable {
    boolean canConvert(Player player, ItemStack stack);

    EntityType<? extends Mob> getConversionTarget();

    default void startConversion(int conversionTick) {
        setConverting(conversionTick > 0);
        setConversion(Math.max(0, conversionTick));
    }

    void setConversion(int conversionTick);

    int getConversion();

    void setConverting(boolean converting);

    boolean isConverting();

    default boolean tickConversion() {
        if (!isConverting()) {
            setConversion(0);
            return false;
        }
        setConversion(Math.max(0, getConversion() - 1));
        if (getConversion() == 0) {
            setConverting(false);
            return true;
        } else return false;
    }

    default void writeConversionData(CompoundTag tag) {
        tag.putInt("stntr_con", isConverting() ? getConversion() : -1);
    }

    default void readConversionData(CompoundTag tag) {
        startConversion(tag.getInt("stntr_con"));
    }
}
