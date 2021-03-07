package dev.adox.bundlesplus.common.item;


import dev.adox.bundlesplus.common.init.BundleResources;
import dev.adox.bundlesplus.common.util.BundleItemUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Bundle Item
 *
 * @author JimiIT92
 */
public class BundleItem extends Item {

    /**
     * Constructor. Set the Bundle Item properties
     */
    public BundleItem() {
        this(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).stacksTo(1).durability(BundleResources.MAX_BUNDLE_ITEMS));
    }

    public BundleItem(Properties properties) {
        super(properties);
    }

    /**
     * Check if the item is damageable
     *
     * @return False
     */
    @Override
    public boolean canBeDepleted() {
        return true;
    }

    /**
     * Check if the item can be enchanted with books
     *
     * @param stack Bundle Item Stack
     * @return False
     */
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isValidRepairItem(ItemStack itemStack, ItemStack itemStack2) {
        return false;
    }


}
