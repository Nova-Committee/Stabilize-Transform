package committee.nova.stntr;

import com.google.common.collect.Lists;
import committee.nova.stntr.common.api.IEntityStabilizable;
import committee.nova.stntr.common.api.IEntityTransformable;
import committee.nova.stntr.common.network.handler.NetworkHandler;
import committee.nova.stntr.common.network.msg.TransformationSyncMsg;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;

@Mod("stntr")
public class StabilizeAndTransform {
    public static final String MODID = "stntr";
    public static final String HOGLIN_S = "hoglin_stabilizer";
    public static final String PIGLIN_S = "piglin_stabilizer";
    public static final String ZOGLIN_T = "zoglin_transformer";
    public static final String Z_VILLAGER_T = "zombie_villager_transformer";
    public static final String Z_PIGLIN_T = "zombified_piglin_transformer";
    public static final ITag.INamedTag<Item> hoglinStabilizer = combine(HOGLIN_S);
    public static final ITag.INamedTag<Item> piglinStabilizer = combine(PIGLIN_S);
    public static final ITag.INamedTag<Item> zoglinTransformer = combine(ZOGLIN_T);
    public static final ITag.INamedTag<Item> zVillagerTransformer = combine(Z_VILLAGER_T);
    public static final ITag.INamedTag<Item> zPiglinTransformer = combine(Z_PIGLIN_T);
    public static final ForgeConfigSpec CFG;
    public static final ForgeConfigSpec.ConfigValue<List<String>> cfgHoglinStabilizers;
    public static final ForgeConfigSpec.ConfigValue<List<String>> cfgPiglinStabilizers;
    public static final ForgeConfigSpec.ConfigValue<List<String>> cfgZoglinTransformers;
    public static final ForgeConfigSpec.ConfigValue<List<String>> cfgZVillagerTransformers;
    public static final ForgeConfigSpec.ConfigValue<List<String>> cfgZPiglinTransformers;

    public StabilizeAndTransform() {
        NetworkHandler.registerMessage();
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CFG);
    }

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("StabilizeAndTransform Settings").push("general");
        cfgHoglinStabilizers = builder.define(HOGLIN_S, Lists.newArrayList("example:stabilizer_item"));
        cfgPiglinStabilizers = builder.define(PIGLIN_S, Lists.newArrayList("example:stabilizer_item"));
        cfgZoglinTransformers = builder.define(ZOGLIN_T, Lists.newArrayList("example:transformer_item"));
        cfgZVillagerTransformers = builder.define(Z_VILLAGER_T, Lists.newArrayList("example:transformer_item"));
        cfgZPiglinTransformers = builder.define(Z_PIGLIN_T, Lists.newArrayList("example:transformer_item"));
        builder.pop();
        CFG = builder.build();
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.EntityInteract event) {
        final Entity e = event.getTarget();
        if (!(e instanceof LivingEntity)) return;
        final LivingEntity l = (LivingEntity) e;
        final ItemStack stack = event.getItemStack();
        final PlayerEntity player = event.getPlayer();
        final boolean shouldConsume = !player.abilities.instabuild;
        if (e instanceof IEntityStabilizable) {
            final IEntityStabilizable s = (IEntityStabilizable) e;
            if (!s.isImmuneToConversion() && s.canStabilize(player, stack)) {
                s.setImmuneToConversion(true);
                l.playSound(stack.getEatingSound(), 1.0F, 1.0F);
                if (shouldConsume) stack.shrink(1);
                return;
            }
        }
        if ((e instanceof IEntityTransformable) && l.hasEffect(Effects.WEAKNESS)) {
            final IEntityTransformable tr = (IEntityTransformable) e;
            if (!tr.isConverting() && tr.canConvert(player, stack)) {
                final int conversionTime = l.getRandom().nextInt(2401) + 3600;
                tr.startConversion(conversionTime);
                l.removeEffect(Effects.WEAKNESS);
                l.addEffect(new EffectInstance(Effects.DAMAGE_BOOST, conversionTime, Math.min(l.level.getDifficulty().getId() - 1, 0)));
                if (!e.level.isClientSide)
                    NetworkHandler.INSTANCE.send(PacketDistributor.DIMENSION.with(() -> e.level.dimension()),
                            new TransformationSyncMsg(e.getId(), true, true));
            }
        }
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        final LivingEntity e = event.getEntityLiving();
        if (e.level.isClientSide) return;
        if (!(e instanceof IEntityTransformable)) return;
        if (!(e instanceof MobEntity)) return;
        if (((IEntityTransformable) e).tickConversion()) {
            final MobEntity m = ((MobEntity) e).convertTo(((IEntityTransformable) e).getConversionTarget(), false);
            if (m instanceof HoglinEntity) ((HoglinEntity) m).setImmuneToZombification(true);
            if (m instanceof PiglinEntity) ((PiglinEntity) m).setImmuneToZombification(true);
        }
        if (e.level.getGameTime() + e.getId() % 20 == 0)
            NetworkHandler.INSTANCE.send(PacketDistributor.DIMENSION.with(() -> e.level.dimension()),
                    new TransformationSyncMsg(e.getId(), ((IEntityTransformable) e).isConverting(), false));
    }

    private static ITag.INamedTag<Item> combine(String s) {
        return ItemTags.bind(MODID + ":" + s);
    }

    public static boolean canStabilizeHoglin(Item item) {
        if (hoglinStabilizer.contains(item)) return true;
        final ResourceLocation l = item.getRegistryName();
        if (l == null) return false;
        return cfgHoglinStabilizers.get().contains(item.getRegistryName().toString());
    }

    public static boolean canStabilizePiglin(Item item) {
        if (piglinStabilizer.contains(item)) return true;
        final ResourceLocation l = item.getRegistryName();
        if (l == null) return false;
        return cfgPiglinStabilizers.get().contains(item.getRegistryName().toString());
    }

    public static boolean canTransformZoglin(Item item) {
        if (zoglinTransformer.contains(item)) return true;
        final ResourceLocation l = item.getRegistryName();
        if (l == null) return false;
        return cfgZoglinTransformers.get().contains(item.getRegistryName().toString());
    }

    public static boolean canTransformZVillager(Item item) {
        if (zVillagerTransformer.contains(item)) return true;
        final ResourceLocation l = item.getRegistryName();
        if (l == null) return false;
        return cfgZVillagerTransformers.get().contains(item.getRegistryName().toString());
    }

    public static boolean canTransformZPiglin(Item item) {
        if (zPiglinTransformer.contains(item)) return true;
        final ResourceLocation l = item.getRegistryName();
        if (l == null) return false;
        return cfgZPiglinTransformers.get().contains(item.getRegistryName().toString());
    }
}
