package dev.adox.bundlesplus.common;

import me.shedaniel.architectury.annotations.ExpectPlatform;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class PlatformUtil {
    @ExpectPlatform
    public static Slot getHoveredSlot(AbstractContainerScreen screen){
        throw new AssertionError();
    }
    @ExpectPlatform
    @NotNull
    public static Supplier<Item> getBundleItem() {
        throw new AssertionError();

    }
    @ExpectPlatform
    public static NonNullList<ItemStack> loadAllItems(ItemStack shulker) {
        throw new AssertionError();
    }
}
