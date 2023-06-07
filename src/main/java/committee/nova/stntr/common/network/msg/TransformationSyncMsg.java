package committee.nova.stntr.common.network.msg;

import committee.nova.stntr.client.network.ClientNetworkUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TransformationSyncMsg {
    private final int id;
    private final boolean converting;
    private final boolean shouldPlaySound;

    public TransformationSyncMsg(FriendlyByteBuf buffer) {
        id = buffer.readInt();
        converting = buffer.readBoolean();
        shouldPlaySound = buffer.readBoolean();
    }

    public TransformationSyncMsg(int id, boolean converting, boolean shouldPlaySound) {
        this.id = id;
        this.converting = converting;
        this.shouldPlaySound = shouldPlaySound;
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(id);
        buffer.writeBoolean(converting);
        buffer.writeBoolean(shouldPlaySound);
    }

    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientNetworkUtils.handleSync(id, converting, shouldPlaySound)));
        ctx.get().setPacketHandled(true);
    }
}
