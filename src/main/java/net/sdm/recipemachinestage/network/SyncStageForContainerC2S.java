package net.sdm.recipemachinestage.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.sdm.recipemachinestage.api.IContainerMenuSync;

import java.util.function.Supplier;

public class SyncStageForContainerC2S {
    private final int containerId;
    private final String value;

    public SyncStageForContainerC2S(int containerId, String value) {
        this.containerId = containerId;
        this.value = value;
    }

    public SyncStageForContainerC2S(FriendlyByteBuf buf) {
        this.containerId = buf.readInt();
        this.value = buf.readUtf(32767); // Максимальная длина строки
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(containerId);
        buf.writeUtf(value);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // Получаем игрока с сервера
            ServerPlayer serverPlayer = context.getSender();
            if (serverPlayer != null && serverPlayer.containerMenu.containerId == this.containerId) {
                // Синхронизация строки на сервере
                if (serverPlayer.containerMenu instanceof IContainerMenuSync container) {
                    container.sdm$setStage(this.value);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
