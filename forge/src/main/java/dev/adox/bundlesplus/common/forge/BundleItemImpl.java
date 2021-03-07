package dev.adox.bundlesplus.common.forge;

import dev.adox.bundlesplus.common.item.BundleItem;
import dev.adox.bundlesplus.common.util.BundleItemUtils;
import net.minecraft.world.item.ItemStack;

public class BundleItemImpl extends BundleItem {
    /**
     * Get the Durability Bar Color
     *
     * @param stack Item Stack
     * @return Durability Bar Color
     */
    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return 0x0C91FF;
    }

    /**
     * Determine if the durability bar must be shown
     *
     * @param stack Bundle Item Stack
     * @return True if the Bundle is not empty and is not full, False otherwise
     */
    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return !BundleItemUtils.isEmpty(stack) && !BundleItemUtils.isFull(stack);
    }

    /**
     * Get the "Damage" of the Bundle Item Stack
     * This determine the charge of the durability bar
     *
     * @param stack Bundle Item Stack
     * @return Bundle "Damage"
     */
    @Override
    public int getDamage(ItemStack stack) {
        return getMaxDamage() - BundleItemUtils.getBundleItemsCount(stack);
    }

    /**
     * Set the Bundle to not bet damaged,
     * so the "Damage"NBT won't be shown
     * in the tooltip
     *
     * @param stack Bundle Item Stack
     * @return False
     */
    @Override
    public boolean isDamaged(ItemStack stack) {
        return false;
    }

}
