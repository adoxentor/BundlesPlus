package dev.adox.bundlesplus.fabric;

import dev.adox.bundlesplus.common.BundlesPlusMod;
import dev.adox.bundlesplus.common.init.BundleItems;
import dev.adox.bundlesplus.common.init.BundleResources;
import dev.adox.bundlesplus.common.util.BundleItemUtils;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import static dev.adox.bundlesplus.common.init.BundleResources.MOD_ID;

public class BundlesPlusModFabric extends BundlesPlusMod {
    private static final ResourceLocation SHULKER_BOX_CAP = new ResourceLocation(MOD_ID, "shulker_box_drop_in");

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
//        ItemProperties.register(
//            BundleItems.BUNDLE.get()
//            , BundleResources.BUNDLE_FULL_NBT_RESOURCE_LOCATION
//            , new ItemPropertyFunction() {
//                @ParametersAreNonnullByDefault
//
//                @Override
//                public float call(ItemStack itemStack, @Nullable ClientLevel arg2, @Nullable LivingEntity arg3) {
//                    return BundleItemUtils.isFull(itemStack) ? 1.0F : 0.0F;
//                }
//
//            })
    }


    public static void initialize() {
        BundlesPlusMod.initialize();
    }

    public BundlesPlusModFabric() {
    }


}
