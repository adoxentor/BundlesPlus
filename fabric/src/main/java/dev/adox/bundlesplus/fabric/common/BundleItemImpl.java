package dev.adox.bundlesplus.fabric.common;

import dev.adox.bundlesplus.common.init.BundleResources;
import dev.adox.bundlesplus.common.item.BundleItem;
import dev.adox.bundlesplus.common.util.BundleItemUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BundleItemImpl extends BundleItem {
    public BundleItemImpl() {
        super(new FabricItemSettings().customDamage((stack, amount, entity, breakCallback) -> 0)
            .tab(CreativeModeTab.TAB_TOOLS).stacksTo(1)
            .durability(BundleResources.MAX_BUNDLE_ITEMS));
    }
}
