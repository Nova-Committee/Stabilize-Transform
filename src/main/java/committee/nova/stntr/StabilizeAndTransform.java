package committee.nova.stntr;

import com.google.common.collect.Lists;
import committee.nova.stntr.common.api.IEntityStabilizable;
import committee.nova.stntr.common.api.IEntityTransformable;
import committee.nova.stntr.common.network.handler.NetworkHandler;
import committee.nova.stntr.common.network.msg.TransformationSyncMsg;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@Mod("stntr")
public class StabilizeAndTransform {
    public static final String MODID = "stntr";
    public static final String HOGLIN_S = "hoglin_stabilizer";
    public static final String PIGLIN_S = "piglin_stabilizer";
    public static final String ZOGLIN_T = "zoglin_transformer";
    public static final String Z_VILLAGER_T = "zombie_villager_transformer";
    public static final String Z_PIGLIN_T = "zombified_piglin_transformer";
    public static final TagKey<Item> hoglinStabilizer = combine(HOGLIN_S);
    public static final TagKey<Item> piglinStabilizer = combine(PIGLIN_S);
    public static final TagKey<Item> zoglinTransformer = combine(ZOGLIN_T);
    public static final TagKey<Item> zVillagerTransformer = combine(Z_VILLAGER_T);
    public static final TagKey<Item> zPiglinTransformer = combine(Z_PIGLIN_T);
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
        if (!(e instanceof final LivingEntity l)) return;
        final ItemStack stack = event.getItemStack();
        final Player player = event.getEntity();
        final boolean shouldConsume = !player.getAbilities().instabuild;
        if (e instanceof final IEntityStabilizable s) {
            if (!s.isImmuneToConversion() && s.canStabilize(player, stack)) {
                s.setImmuneToConversion(true);
                l.playSound(stack.getEatingSound(), 1.0F, 1.0F);
                if (shouldConsume) stack.shrink(1);
                return;
            }
        }
        if ((e instanceof final IEntityTransformable tr) && l.hasEffect(MobEffects.WEAKNESS)) {
            if (!tr.isConverting() && tr.canConvert(player, stack)) {
                final int conversionTime = l.getRandom().nextInt(2401) + 3600;
                tr.startConversion(conversionTime);
                l.removeEffect(MobEffects.WEAKNESS);
                l.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, conversionTime, Math.min(l.level.getDifficulty().getId() - 1, 0)));
                if (!e.level.isClientSide)
                    NetworkHandler.INSTANCE.send(PacketDistributor.DIMENSION.with(() -> e.level.dimension()),
                            new TransformationSyncMsg(e.getId(), true, true));
            }
        }
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingTickEvent event) {
        final LivingEntity e = event.getEntity();
        if (e.level.isClientSide) return;
        if (!(e instanceof IEntityTransformable)) return;
        if (!(e instanceof Mob)) return;
        if (((IEntityTransformable) e).tickConversion()) {
            final Mob m = ((Mob) e).convertTo(((IEntityTransformable) e).getConversionTarget(), false);
            if (m instanceof Hoglin) ((Hoglin) m).setImmuneToZombification(true);
            if (m instanceof Piglin) ((Piglin) m).setImmuneToZombification(true);
        }
        if (e.level.getGameTime() + e.getId() % 20 == 0)
            NetworkHandler.INSTANCE.send(PacketDistributor.DIMENSION.with(() -> e.level.dimension()),
                    new TransformationSyncMsg(e.getId(), ((IEntityTransformable) e).isConverting(), false));
    }

    private static TagKey<Item> combine(String s) {
        return ItemTags.create(new ResourceLocation(MODID, s));
    }

    public static boolean canStabilizeHoglin(ItemStack stack) {
        if (stack.is(hoglinStabilizer)) return true;
        final ResourceLocation l = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (l == null) return false;
        return cfgHoglinStabilizers.get().contains(l.toString());
    }

    public static boolean canStabilizePiglin(ItemStack stack) {
        if (stack.is(piglinStabilizer)) return true;
        final ResourceLocation l = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (l == null) return false;
        return cfgPiglinStabilizers.get().contains(l.toString());
    }

    public static boolean canTransformZoglin(ItemStack stack) {
        if (stack.is(zoglinTransformer)) return true;
        final ResourceLocation l = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (l == null) return false;
        return cfgZoglinTransformers.get().contains(l.toString());
    }

    public static boolean canTransformZVillager(ItemStack stack) {
        if (stack.is(zVillagerTransformer)) return true;
        final ResourceLocation l = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (l == null) return false;
        return cfgZVillagerTransformers.get().contains(l.toString());
    }

    public static boolean canTransformZPiglin(ItemStack stack) {
        if (stack.is(zPiglinTransformer)) return true;
        final ResourceLocation l = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (l == null) return false;
        return cfgZPiglinTransformers.get().contains(l.toString());
    }
}
