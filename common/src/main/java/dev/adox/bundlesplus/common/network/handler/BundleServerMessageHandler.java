package dev.adox.bundlesplus.common.network.handler;

import dev.adox.bundlesplus.common.init.BundleResources;
import dev.adox.bundlesplus.common.network.message.BundleClientMessage;
import dev.adox.bundlesplus.common.network.message.BundleServerMessage;
import dev.adox.bundlesplus.common.util.BundleItemUtils;
import me.shedaniel.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

import static net.fabricmc.api.EnvType.CLIENT;

/**
 * @author JimiIT92
 */
public class BundleServerMessageHandler {

    /**
     * Check if the Protocol is accepted by the Server
     *
     * @param protocolVersion Protocol Version
     * @return True if the Protocol is accepted, False otherwise
     */
    public static boolean isThisProtocolAcceptedByServer(String protocolVersion) {
        return BundleResources.MESSAGE_PROTOCOL_VERSION.equals(protocolVersion);
    }

    /**
     * Handle messages
     *
     * @param message     Message
     * @param ctxSupplier Context Supplier
     */
    public static void onMessageReceived(final BundleServerMessage message, Supplier<NetworkManager.PacketContext> ctxSupplier) {
        NetworkManager.PacketContext context = ctxSupplier.get();
        final Player playerEntity = context.getPlayer();

        if (!(playerEntity instanceof ServerPlayer)) {
            return;
        }

        if (playerEntity == null) {
            return;
        }

        context.queue(() -> processMessage(message, playerEntity));
    }

    /**
     * Process the Message
     *
     * @param message      Message
     * @param playerEntity Player
     */
    public static void processMessage(BundleServerMessage message, Player playerEntity) {
        assert ItemStack.matches(playerEntity.inventory.getCarried(),message.bundle);
        AbstractContainerMenu container = playerEntity.containerMenu;
        Slot slot = container.getSlot(message.slotId);
        ItemStack slotStack = slot.getItem();
        boolean playEmptySound = false;
        if (message.empty) {
            playEmptySound = !BundleItemUtils.isEmpty(message.bundle);
            BundleItemUtils.emptyBundle(message.bundle, playerEntity);
            slotStack = message.bundle;
        } else {
            if (slotStack.isEmpty()) {
                slotStack = BundleItemUtils.removeFirstItemStack(message.bundle, message.reversed);
            } else {
                BundleItemUtils.addItemStackToBundle(playerEntity.inventory.getCarried(), slotStack);
            }
            if (!playerEntity.isCreative() || !(container instanceof InventoryMenu)) {
                playerEntity.inventory.setCarried(message.bundle);
            }
        }
        slot.set(slotStack);

        if (playerEntity instanceof ServerPlayer) {
            BundleResources.NETWORK.sendToPlayer((ServerPlayer) playerEntity, new BundleClientMessage(message.bundle, message.slotId, slotStack, message.empty, playEmptySound));
            ((ServerPlayer) playerEntity).refreshContainer(container);

        }
    }
}
