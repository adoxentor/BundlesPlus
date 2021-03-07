package dev.adox.bundlesplus.common.fabric;

import dev.adox.bundlesplus.common.PlatformUtil;
import dev.adox.bundlesplus.fabric.common.BundleItemImpl;
import dev.adox.bundlesplus.fabric.mixin.MixinAbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class PlatformUtilImpl extends PlatformUtil {


    public static Slot getHoveredSlot(AbstractContainerScreen screen){
        return ((MixinAbstractContainerScreen)screen).getHoveredSlot();
    }
    @NotNull
    public static Supplier<Item> getBundleItem() {
        return BundleItemImpl::new;
    }
    public static NonNullList<ItemStack> loadAllItems(ItemStack shulker) {
        CompoundTag compoundnbt = shulker.getTagElement("BlockEntityTag");
        if (compoundnbt == null) {
            return NonNullList.create();
        }
        if (compoundnbt.contains("Items", 9)) {
            ListTag listTag = compoundnbt.getList("Items", 10);
            int size = Math.max(27, listTag.size());
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag2 = listTag.getCompound(i);
                int j = compoundTag2.getByte("Slot") & 255;
                if (j >= size) {
                    size = j + 1;
                }
            }
            NonNullList<ItemStack> nonNullList = NonNullList.withSize(size, ItemStack.EMPTY);
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag2 = listTag.getCompound(i);
                int j = compoundTag2.getByte("Slot") & 255;
                assert j <= size;
                nonNullList.set(j, ItemStack.of(compoundTag2));
            }
            return nonNullList;
        }
        return NonNullList.create();

    }
}
