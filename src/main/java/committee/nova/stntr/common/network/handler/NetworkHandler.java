package committee.nova.stntr.common.network.handler;

import committee.nova.stntr.StabilizeAndTransform;
import committee.nova.stntr.common.network.msg.TransformationSyncMsg;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    public static SimpleChannel INSTANCE;
    public static final String VERSION = "1.0";
    private static int id = 0;

    public static int nextId() {
        return id++;
    }

    public static void registerMessage() {
        INSTANCE = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(StabilizeAndTransform.MODID, "sync"),
                () -> VERSION,
                (version) -> version.equals(VERSION),
                (version) -> version.equals(VERSION)
        );
        INSTANCE.messageBuilder(TransformationSyncMsg.class, nextId())
                .encoder(TransformationSyncMsg::toBytes)
                .decoder(TransformationSyncMsg::new)
                .consumerMainThread(TransformationSyncMsg::handler)
                .add();
    }
}
