package committee.nova.stntr.mixin;

import committee.nova.stntr.StabilizeAndTransform;
import committee.nova.stntr.common.api.IEntityTransformable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZoglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ZoglinEntity.class)
public abstract class MixinZoglin extends MonsterEntity implements IEntityTransformable {
    private boolean converting;
    private int conversionTick;

    protected MixinZoglin(EntityType<? extends MonsterEntity> e, World w) {
        super(e, w);
    }

    @Override
    public boolean canConvert(PlayerEntity player, ItemStack stack) {
        return StabilizeAndTransform.canTransformZoglin(stack.getItem());
    }

    @Override
    public EntityType<? extends MobEntity> getConversionTarget() {
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
    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
        this.writeConversionData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
        this.readConversionData(tag);
    }
}
