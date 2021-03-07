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
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;

import static dev.adox.bundlesplus.common.network.handler.BundleServerMessageHandler.processMessage;


/**
 * Bundle Events
 *
 * @author JimiIT92
 */
public final class  BundleEvents {
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
            if (slot == null || (slot instanceof ResultSlot) || player==null)
                return InteractionResult.PASS;

            ItemStack draggedItemStack = player.inventory.getCarried();
            ItemStack slotStack = slot.getItem();
            AbstractContainerMenu container = containerScreen.getMenu();
            if (slot.mayPickup(player)
                && slot.isActive()
                && slot.mayPlace(draggedItemStack)
                && container.canTakeItemForPickAll(draggedItemStack, slot)) {
                if (slot.hasItem()
                    && BundleItemUtils.isBundle(draggedItemStack)
                    && BundleItemUtils.canAddItemStackToBundle(draggedItemStack, slotStack)
                ) {
                    oldSelectedSlot = slot;
                    filling = true;
                    BundleServerMessage message = new BundleServerMessage(draggedItemStack, slot.index, false, Screen.hasShiftDown());
                    processMessage(message, player);
                    BundleResources.NETWORK.sendToServer(message);
                    return InteractionResult.FAIL;

                } else if (!slot.hasItem()
                    && BundleItemUtils.isBundle(draggedItemStack)
                    && !BundleItemUtils.isEmpty(draggedItemStack)
                ) {
                    oldSelectedSlot = slot;
                    filling = false;
                    BundleServerMessage message = new BundleServerMessage(draggedItemStack, slot.index, false, Screen.hasShiftDown());
                    BundleResources.NETWORK.sendToServer(message);
                    processMessage(message, player);
                    return InteractionResult.FAIL;
                } else {
                    oldSelectedSlot = null;
                    if (slot.hasItem() && draggedItemStack.isEmpty() && BundleItemUtils.isBundle(slotStack)&& !BundleItemUtils.isEmpty(slotStack)) {
                        BundleResources.NETWORK.sendToServer(new BundleServerMessage(slotStack, slot.index, true, net.minecraft.client.gui.screens.Screen.hasShiftDown()));
                        return InteractionResult.FAIL;

                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    /**
     * Handle mouse clicks on Containers
     * to determine if an Item Stack should
     * be put inside a Bundle
     *
     * @param event Mouse Released Event
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

            if (slot != null
                && !(slot instanceof ResultSlot)
                && player != null
                && slot.mayPickup(player)
                && slot.isActive()
                && slot.mayPlace(draggedItemStack)
                && container.canTakeItemForPickAll(draggedItemStack, slot)) {
                if ((filling || oldSelectedSlot == null)
                    && slot.hasItem()
                    && BundleItemUtils.isBundle(draggedItemStack)
                    && BundleItemUtils.canAddItemStackToBundle(draggedItemStack, slotStack)
                ) {
                    oldSelectedSlot = slot;
                    filling = true;
                    BundleServerMessage message = new BundleServerMessage(draggedItemStack, slot.index, false, Screen.hasShiftDown());
                    processMessage(message, player);
                    BundleResources.NETWORK.sendToServer(message);
                    return InteractionResult.FAIL;

                } else if ((!filling || oldSelectedSlot == null)
                    && !slot.hasItem()
                    && BundleItemUtils.isBundle(draggedItemStack)
                    && !BundleItemUtils.isEmpty(draggedItemStack)
                ) {
                    oldSelectedSlot = slot;
                    filling = false;
                    BundleServerMessage message = new BundleServerMessage(draggedItemStack, slot.index, false, Screen.hasShiftDown());
                    BundleResources.NETWORK.sendToServer(message);
                    processMessage(message, player);
                    return InteractionResult.FAIL;
                }
            }
        }
        return InteractionResult.PASS;
    }
//    public static void onMouseDrag(final GuiScreenEvent.MouseDragEvent event) {
//        if (!event.isCanceled()
//            && event.getGui() instanceof ContainerScreen<?>
//            && event.getMouseButton() == 1) {
//            ContainerScreen<?> containerScreen = (ContainerScreen<?>) event.getGui();
//            Slot slot = containerScreen.getSlotUnderMouse();
//            Player player = Minecraft.getInstance().player;
//            if (slot == oldSelectedSlot || slot == null)
//                return;
//            if (!(slot instanceof CraftingResultSlot)
//                && player != null
//                && slot.canTakeStack(player)
//                && slot.isEnabled()) {
//                ItemStack draggedItemStack = player.inventory.getItemStack();
//                ItemStack slotStack = slot.getStack();
//                Container container = containerScreen.getContainer();
//                if ((filling || oldSelectedSlot == null)
//                    && container.canMergeSlot(draggedItemStack, slot)
//                    && slot.isItemValid(draggedItemStack)
//                    && slot.getHasStack()
//                    && BundleItemUtils.isBundle(draggedItemStack)
//                    && BundleItemUtils.canAddItemStackToBundle(draggedItemStack, slotStack)
//                ) {
//                    try {
//                        Field slotIndexField = getSlotIndexField();
//                        if (slotIndexField != null) {
//                            oldSelectedSlot = slot;
//                            slotIndexField.setAccessible(true);
//                            int slotIndex = player.isCreative() && container instanceof CreativeScreen.CreativeContainer ?
//                                (int) slotIndexField.get(slot)
//                                : slot.slotNumber;
//                            BundleServerMessage message = new BundleServerMessage(draggedItemStack, slotIndex, false, Screen.hasShiftDown());
//                            processMessage(message, player);
//                            BundleResources.NETWORK.sendToServer(message);
//                            event.setResult(Event.Result.DENY);
//                            event.setCanceled(true);
//                        }
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                } else if ((!filling || oldSelectedSlot == null) &&
//                    slot.canTakeStack(player) && slot.isEnabled()
//                    && container.canMergeSlot(draggedItemStack, slot)
//                    && slot.isItemValid(draggedItemStack)
//                    && !slot.getHasStack()
//                    && BundleItemUtils.isBundle(draggedItemStack)
//                    && !BundleItemUtils.isEmpty(draggedItemStack)
//                ) {
//                    try {
//                        oldSelectedSlot = slot;
//
//                        Field slotIndexField = getSlotIndexField();
//                        if (slotIndexField != null) {
//                            slotIndexField.setAccessible(true);
//                            int slotIndex = player.isCreative() && container instanceof CreativeScreen.CreativeContainer ?
//                                (int) slotIndexField.get(slot)
//                                : slot.slotNumber;
//                            BundleResources.NETWORK.sendToServer(new BundleServerMessage(draggedItemStack, slotIndex, false, Screen.hasShiftDown()));
//                            event.setResult(Event.Result.DENY);
//                            event.setCanceled(true);
//                        }
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//        }
//    }

    /**
     * Handle mouse clicks on Containers
     * to determine if a Bundle should be cleared
     *
     * @param event Mouse Clicked Event
     */
//    public static void onMouseClick(final GuiScreenEvent.MouseClickedEvent event) {
//        if (!event.isCanceled() && event.getGui() instanceof ContainerScreen<?>) {
//            ContainerScreen<?> containerScreen = (ContainerScreen<?>) event.getGui();
//            Slot slot = containerScreen.getSlotUnderMouse();
//            if (slot != null && !(slot instanceof CraftingResultSlot)) {
//                Player player = Minecraft.getInstance().player;
//                if (player != null) {
//                    ItemStack slotStack = slot.getStack();
//                    if (slot.canTakeStack(player) && slot.isEnabled()
//                        && slot.getHasStack() && event.getButton() == 1
//                        && BundleItemUtils.isBundle(slotStack)
//                        && !BundleItemUtils.isEmpty(slotStack)) {
//                        try {
//                            Field slotIndexField = getSlotIndexField();
//                            if (slotIndexField != null) {
//                                slotIndexField.setAccessible(true);
//                                int slotIndex = player.isCreative() && containerScreen.getContainer() instanceof CreativeScreen.CreativeContainer ?
//                                    (int) slotIndexField.get(slot)
//                                    : slot.slotNumber;
//                                BundleResources.NETWORK.sendToServer(new BundleServerMessage(slotStack, player.isCreative() ? slotIndex : slot.slotNumber, true, net.minecraft.client.gui.screen.Screen.hasShiftDown()));
//                                event.setResult(Event.Result.DENY);
//                                event.setCanceled(true);
//                            }
//                        } catch (IllegalAccessException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }
//    }


    /**
     * Get the Slot Index Field
     *
     * @return Slot Index Field
     */
    private static Field getSlotIndexField() {
        Field slotIndexField = null;
        try {
            slotIndexField = Slot.class.getDeclaredField("slotIndex");
        } catch (NoSuchFieldException e) {
            try {
                slotIndexField = Slot.class.getDeclaredField("field_75225_a");
            } catch (NoSuchFieldException ex) {
                ex.printStackTrace();
            }
        }
        return slotIndexField;
    }

}
