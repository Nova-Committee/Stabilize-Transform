package committee.nova.stntr.mixin;

import committee.nova.stntr.StabilizeAndTransform;
import committee.nova.stntr.common.api.IEntityTransformable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Zoglin.class)
public abstract class MixinZoglin extends Monster implements IEntityTransformable {
    private boolean converting;
    private int conversionTick;

    protected MixinZoglin(EntityType<? extends Monster> e, Level w) {
        super(e, w);
    }

    @Override
    public boolean canConvert(Player player, ItemStack stack) {
        return StabilizeAndTransform.canTransformZoglin(stack);
    }

    @Override
    public EntityType<? extends Mob> getConversionTarget() {
        return EntityType.HOGLIN;
    }

    @Override
    public void setConversion(int conversionTick) {
        this.conversionTick = conversionTick;
    }

    @Override
    public int getConversion() {
        return conversionTick;
    }

    @Override
    public void setConverting(boolean converting) {
        this.converting = converting;
    }

    @Override
    public boolean isConverting() {
        return converting;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.writeConversionData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readConversionData(tag);
    }
}
