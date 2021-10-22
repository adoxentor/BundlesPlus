package dev.adox.bundlesplus.fabric;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import dev.adox.bundlesplus.common.BundlesPlusMod;
import dev.adox.bundlesplus.common.init.BundleItems;
import dev.adox.bundlesplus.common.init.BundleResources;
import dev.adox.bundlesplus.common.item.BundleItem;
import dev.adox.bundlesplus.common.util.BundleItemUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyShulkerBoxApiImpl implements ShulkerBoxTooltipApi {
    @Override
    public String getModId() {
        return BundleResources.MOD_ID;
    }

    @Override
    public void registerProviders(Map<PreviewProvider, List<Item>> previewProviders) {
        ArrayList<Item> items = new ArrayList<>();
        items.add(BundleItems.BUNDLE.get());
        previewProviders.put(new BundlePreviewProvider(), items);
    }
    static class BundlePreviewProvider implements PreviewProvider {
        @Override
        public boolean shouldDisplay(PreviewContext context) {
            boolean bundle = (context.getStack().getItem() instanceof BundleItem && BundlesPlusMod.CONFIG.get().BUNDLE_TOOLTIP);
            return bundle && !BundleItemUtils.isEmpty(context.getStack());
        }

        @Override
        public List<ItemStack> getInventory(PreviewContext context) {
            return BundleItemUtils.getItemsFromBundle(context.getStack());
        }

        @Override
        public float[] getWindowColor(PreviewContext context) {
            return new float[]{0.533f, 0.454f, 0.227f};
        }

        @Override
        public int getInventoryMaxSize(PreviewContext context) {
            return BundleItemUtils.getItemsFromBundle(context.getStack()).size();
        }

        @Override
        public boolean isFullPreviewAvailable(PreviewContext context) {
            return true;
        }

        @Override
        public int getMaxRowSize(PreviewContext context) {
            return 5;
        }
    }
}
