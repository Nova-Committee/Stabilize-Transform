package committee.nova.stntr.common.api;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public interface IEntityTransformable {
    boolean canConvert(PlayerEntity player, ItemStack stack);

    EntityType<? extends MobEntity> getConversionTarget();

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

    default void writeConversionData(CompoundNBT tag) {
        tag.putInt("stntr_con", isConverting() ? getConversion() : -1);
    }

    default void readConversionData(CompoundNBT tag) {
        startConversion(tag.getInt("stntr_con"));
    }
}
