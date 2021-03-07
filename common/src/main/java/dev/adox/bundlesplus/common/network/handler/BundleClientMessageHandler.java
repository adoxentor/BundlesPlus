package dev.adox.bundlesplus.common.network.handler;

import dev.adox.bundlesplus.common.init.BundleResources;
import dev.adox.bundlesplus.common.network.message.BundleClientMessage;
import me.shedaniel.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import java.util.function.Supplier;

import static net.fabricmc.api.EnvType.SERVER;

/**
 * @author JimiIT92
 */
public class BundleClientMessageHandler {

    /**
     * Check if the Protocol is accepted by the Server
     *
     * @param protocolVersion Protocol Version
     * @return True if the Protocol is accepted, False otherwise
     */
    public static boolean isThisProtocolAcceptedByClient(String protocolVersion) {
        return BundleResources.MESSAGE_PROTOCOL_VERSION.equals(protocolVersion);
    }

    /**
     * Handle messages
     *
     * @param message     Message
     * @param ctxSupplier Context Supplier
     */
    public static void onMessageReceived(final BundleClientMessage message, Supplier<NetworkManager.PacketContext> ctxSupplier) {
        NetworkManager.PacketContext context = ctxSupplier.get();

        if (!(context.getPlayer() instanceof AbstractClientPlayer)) {
            return;
        }

        LocalPlayer playerEntity = Minecraft.getInstance().player;
        if (playerEntity == null) {
            return;
        }
        context.queue(() -> processMessage(message, playerEntity));
    }

    /**
     * Process the Message
     *
     * @param message Message
     * @param player  Player
     */
    private static void processMessage(BundleClientMessage message, LocalPlayer player) {
//        AbstractContainerMenu container = player.containerMenu;
//        Slot slot = container.getSlot(message.slotId);
//        slot.set(message.slotStack);
//        slot.getStack().cooldown
        if (message.empty) {
            if (message.playEmptySound) {
                player.playSound(SoundEvents.WOOL_BREAK, 1.0F, 1.0F);
            }
        } else {
            player.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 1.0F, 1.0F);
//            player.inventory.setCarried(message.bundle);
        }
    }

}
