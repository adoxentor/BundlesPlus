package dev.adox.bundlesplus.forge;

import dev.adox.bundlesplus.common.BundlesPlusConfig;
import dev.adox.bundlesplus.common.BundlesPlusMod;
import dev.adox.bundlesplus.common.init.BundleResources;
import dev.adox.bundlesplus.common.util.BundleItemUtils;
import me.shedaniel.architectury.platform.forge.EventBuses;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static dev.adox.bundlesplus.common.init.BundleResources.MOD_ID;

@Mod("bundlesplus")
@Mod.EventBusSubscriber(modid = BundleResources.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BundlesPlusModForge {
    private static final ResourceLocation SHULKER_BOX_CAP = new ResourceLocation(MOD_ID, "shulker_box_drop_in");
    private static final ResourceLocation SHULKER_BOX_WRAPPER_CAP = new ResourceLocation(MOD_ID, "shulker_box_wrapper_cap");

    public BundlesPlusModForge() {
        EventBuses.registerModEventBus(MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        initialize();

        if (ModList.get().isLoaded("autoreglib")) {
            MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, BundlesPlusModForge::onAttachCapabilityItem);
        }

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (client, parent) -> {
            return AutoConfig.getConfigScreen(BundlesPlusConfig.class, parent).get();
        });

    }

    //    @SubscribeEvent
    public static void onAttachCapabilityItem(AttachCapabilitiesEvent<ItemStack> event) {
        if (BundleItemUtils.isBundle(event.getObject()))
            event.addCapability(SHULKER_BOX_CAP, new BundleDropIn());
    }


    public static void initialize() {
        BundlesPlusMod.initialize();

    }


}
