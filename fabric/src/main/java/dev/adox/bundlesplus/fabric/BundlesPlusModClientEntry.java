package dev.adox.bundlesplus.fabric;

import dev.adox.bundlesplus.common.BundlesPlusMod;
import dev.adox.bundlesplus.common.init.BundleItems;
import dev.adox.bundlesplus.common.init.BundleResources;
import dev.adox.bundlesplus.common.util.BundleItemUtils;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BundlesPlusModClientEntry {
    public static void initClient() {
        BundlesPlusMod.initClient();
        FabricModelPredicateProviderRegistry.register(BundleItems.BUNDLE.get(),
            BundleResources.BUNDLE_FULL_NBT_RESOURCE_LOCATION,
            new ItemPropertyFunction() {
                @Override
                public float call(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
                    return BundleItemUtils.isFull(itemStack) ? 1.0F : 0.0F;
                }
            });
    }
}
