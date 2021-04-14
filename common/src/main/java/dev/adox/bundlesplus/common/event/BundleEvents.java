package dev.adox.bundlesplus.common.event;

import dev.adox.bundlesplus.common.BundlesPlusMod;
import dev.adox.bundlesplus.common.PlatformUtil;
import dev.adox.bundlesplus.common.init.BundleResources;
import dev.adox.bundlesplus.common.network.message.BundleServerMessage;
import dev.adox.bundlesplus.common.util.BundleItemUtils;
import me.shedaniel.architectury.event.events.client.ClientScreenInputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;


/**
 * Bundle Events
 *
 * @author JimiIT92
 */
public final class BundleEvents {
    private static Slot oldSelectedSlot = null;
    private static boolean filling = false;

    public static void register() {
        ClientScreenInputEvent.MOUSE_RELEASED_PRE.register(BundleEvents::onMouseReleased);
        ClientScreenInputEvent.MOUSE_CLICKED_PRE.register(BundleEvents::onMouseClicked1);
        ClientScreenInputEvent.MOUSE_DRAGGED_PRE.register(BundleEvents::mouseDragged);
    }

    public static InteractionResult onMouseReleased(Minecraft client, Screen screen, double mouseX, double mouseY, int button) {
        if (oldSelectedSlot != null && BundlesPlusMod.CONFIG.get().BUTTON.value == button) {
            oldSelectedSlot = null;
            return InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult onMouseClicked1(Minecraft client, Screen screen, double mouseX, double mouseY, int button) {
        if (screen instanceof AbstractContainerScreen
            && BundlesPlusMod.CONFIG.get().BUTTON.value == button) {
            AbstractContainerScreen containerScreen = (AbstractContainerScreen) screen;
            Slot slot = PlatformUtil.getHoveredSlot(containerScreen);

            LocalPlayer player = Minecraft.getInstance().player;
            if (slot == null || (slot instanceof ResultSlot) || player == null)
                return InteractionResult.PASS;

            ItemStack draggedItemStack = player.inventory.getCarried();
            ItemStack slotStack = slot.getItem();
            AbstractContainerMenu container = containerScreen.getMenu();
            int slotIndex = getSlotIndex(slot, player, container);
            if (slot.mayPickup(player)
                && slot.isActive()
                && slot.mayPlace(draggedItemStack)) {
                if (slot.hasItem()
                    && BundleItemUtils.isBundle(draggedItemStack)
                    && BundleItemUtils.canAddItemStackToBundle(draggedItemStack, slotStack)
                ) {
                    oldSelectedSlot = slot;
                    filling = true;
                    BundleServerMessage message = new BundleServerMessage(draggedItemStack, slotIndex, false, Screen.hasShiftDown());
//                    processMessage(message, player);
                    BundleResources.NETWORK.sendToServer(message);
                    return InteractionResult.FAIL;

                } else if (!slot.hasItem()
                    && BundleItemUtils.isBundle(draggedItemStack)
                    && !BundleItemUtils.isEmpty(draggedItemStack)
                ) {
                    oldSelectedSlot = slot;
                    filling = false;
                    BundleServerMessage message = new BundleServerMessage(draggedItemStack, slotIndex, false, Screen.hasShiftDown());
                    BundleResources.NETWORK.sendToServer(message);
//                    processMessage(message, player);
                    return InteractionResult.FAIL;
                } else {
                    oldSelectedSlot = null;
                    if (slot.hasItem() && draggedItemStack.isEmpty() && BundleItemUtils.isBundle(slotStack) && !BundleItemUtils.isEmpty(slotStack)) {
                        BundleResources.NETWORK.sendToServer(new BundleServerMessage(slotStack, slotIndex, true, net.minecraft.client.gui.screens.Screen.hasShiftDown()));
                        return InteractionResult.FAIL;

                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    private static int getSlotIndex(Slot slot, LocalPlayer player, AbstractContainerMenu container) {
        if (player.isCreative() && (container instanceof CreativeModeInventoryScreen.ItemPickerMenu)) {
            return PlatformUtil.getSlotIndex(slot);

        }
        return slot.index;
    }

    /**
     * Handle mouse clicks on Containers
     * to determine if an Item Stack should
     * be put inside a Bundle
     */
    public static InteractionResult mouseDragged(Minecraft client, Screen screen, double mouseX1, double mouseY1, int button, double mouseX2, double mouseY2) {

        if (screen instanceof AbstractContainerScreen
            && BundlesPlusMod.CONFIG.get().BUTTON.value == button) {
            AbstractContainerScreen containerScreen = (AbstractContainerScreen) screen;
            Slot slot = PlatformUtil.getHoveredSlot(containerScreen);
            if (slot == oldSelectedSlot || slot == null)
                return InteractionResult.PASS;

            LocalPlayer player = Minecraft.getInstance().player;
            ItemStack draggedItemStack = player.inventory.getCarried();
            ItemStack slotStack = slot.getItem();
            AbstractContainerMenu container = containerScreen.getMenu();
            int slotIndex = getSlotIndex(slot, player, container);
            if (slot != null
                && !(slot instanceof ResultSlot)
                && player != null
                && slot.mayPickup(player)
                && slot.isActive()
                && slot.mayPlace(draggedItemStack)) {
                if ((filling || oldSelectedSlot == null)
                    && slot.hasItem()
                    && BundleItemUtils.isBundle(draggedItemStack)
                    && BundleItemUtils.canAddItemStackToBundle(draggedItemStack, slotStack)
                ) {
                    oldSelectedSlot = slot;
                    filling = true;
                    BundleServerMessage message = new BundleServerMessage(draggedItemStack, slotIndex, false, Screen.hasShiftDown());
//                    processMessage(message, player);
                    BundleResources.NETWORK.sendToServer(message);
                    return InteractionResult.FAIL;

                } else if ((!filling || oldSelectedSlot == null)
                    && !slot.hasItem()
                    && BundleItemUtils.isBundle(draggedItemStack)
                    && !BundleItemUtils.isEmpty(draggedItemStack)
                ) {
                    oldSelectedSlot = slot;
                    filling = false;
                    BundleServerMessage message = new BundleServerMessage(draggedItemStack, slotIndex, false, Screen.hasShiftDown());
                    BundleResources.NETWORK.sendToServer(message);
//                    processMessage(message, player);
                    return InteractionResult.FAIL;
                }
            }
        }
        return InteractionResult.PASS;
    }
}
