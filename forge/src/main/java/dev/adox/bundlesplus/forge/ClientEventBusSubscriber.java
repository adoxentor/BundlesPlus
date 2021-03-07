package dev.adox.bundlesplus.forge;

import dev.adox.bundlesplus.common.BundlesPlusMod;
import dev.adox.bundlesplus.common.event.BundleEvents;
import dev.adox.bundlesplus.common.init.BundleItems;
import dev.adox.bundlesplus.common.init.BundleResources;
import dev.adox.bundlesplus.common.util.BundleItemUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Register Client Events
 *
 * @author JimiIT92
 */
@Mod.EventBusSubscriber(modid = BundleResources.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEventBusSubscriber {

    /**
     * Register Client Events
     *
     * @param event FML Client Setup Event
     */
    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(BundleTooltipUtil.class);
        BundlesPlusMod.initClient();
        event.enqueueWork(() -> ItemProperties.register(
            BundleItems.BUNDLE.get()
            , BundleResources.BUNDLE_FULL_NBT_RESOURCE_LOCATION
            , new ItemPropertyFunction() {
                @ParametersAreNonnullByDefault

                @Override
                public float call(ItemStack itemStack, @Nullable ClientLevel arg2, @Nullable LivingEntity arg3) {
                    return BundleItemUtils.isFull(itemStack) ? 1.0F : 0.0F;
                }

            }));
    }
}
