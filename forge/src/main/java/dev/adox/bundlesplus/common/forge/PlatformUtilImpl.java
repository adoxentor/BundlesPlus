package dev.adox.bundlesplus.common.forge;

import dev.adox.bundlesplus.common.PlatformUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class PlatformUtilImpl extends PlatformUtil {


    public static Slot getHoveredSlot(AbstractContainerScreen screen){
        return screen.getSlotUnderMouse();
    }

    public static int getSlotIndex(Slot slot) {
        if (Minecraft.getInstance().player.isCreative()) {
//            if (slot.getClass().equals(Slot.class)) {
//                return slot.index;
//            }
            return slot.getSlotIndex();
        }
        return slot.index;
    }

    @NotNull
    public static Supplier<Item> getBundleItem() {
        return BundleItemImpl::new;

    }
    public static NonNullList<ItemStack> loadAllItems(ItemStack shulkerBox) {
        CompoundTag compoundnbt = shulkerBox.getTagElement("BlockEntityTag");
        if (compoundnbt == null) {
            return NonNullList.create();
        }
        BlockEntity te = null;
        compoundnbt = compoundnbt.copy();
        compoundnbt.putString("id", "minecraft:shulker_box");
        final int[] size = {27};

        if (shulkerBox.getItem() instanceof BlockItem) {
            Block shulkerBoxBlock = Block.byItem(shulkerBox.getItem());
            BlockState defaultState = shulkerBoxBlock.defaultBlockState();
            if (shulkerBoxBlock.hasTileEntity(defaultState)) {
                te = shulkerBoxBlock.createTileEntity(defaultState, null);
                if (te != null) {
                    te.load(defaultState, compoundnbt);
                    LazyOptional<IItemHandler> handlerHolder = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                    handlerHolder.ifPresent(handler -> {
                        size[0] = handler.getSlots();});
                }
            }
        }

        if (compoundnbt.contains("Items", 9)) {
            ListTag listTag = compoundnbt.getList("Items", 10);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag2 = listTag.getCompound(i);
                int j = compoundTag2.getByte("Slot") & 255;
                if (j >= size[0]) {
                    size[0] = j + 1;
                }
            }
            NonNullList<ItemStack> nonNullList = NonNullList.withSize(size[0], ItemStack.EMPTY);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag2 = listTag.getCompound(i);
                int j = compoundTag2.getByte("Slot") & 255;
                assert j <= size[0];
                nonNullList.set(j, ItemStack.of(compoundTag2));
            }
            return nonNullList;
        }
        return NonNullList.create();

    }

}
