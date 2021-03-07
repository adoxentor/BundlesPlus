package dev.adox.bundlesplus.common.init;

import me.shedaniel.architectury.registry.DeferredRegister;
import me.shedaniel.architectury.registry.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;

import static dev.adox.bundlesplus.common.PlatformUtil.getBundleItem;
import static dev.adox.bundlesplus.common.init.BundleResources.BUNDLE_ITEM_RESOURCE_NAME;
import static dev.adox.bundlesplus.common.init.BundleResources.MOD_ID;

/**
 * Bundle Items
 *
 * @author JimiIT92
 */
public final class BundleItems {

    /**
     * Bundle Items Registry
     */
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registry.ITEM_REGISTRY);

    /**
     * Bundle Item
     */
    public static final RegistrySupplier<Item> BUNDLE = ITEMS.register(BUNDLE_ITEM_RESOURCE_NAME, getBundleItem());

    public static void initialize() {
        ITEMS.register();
    }

}
