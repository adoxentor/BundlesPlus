package dev.adox.bundlesplus.common;

import com.mojang.blaze3d.platform.InputConstants;
import dev.adox.bundlesplus.common.event.BundleEvents;
import dev.adox.bundlesplus.common.init.BundleItems;
import dev.adox.bundlesplus.common.init.BundleResources;
import dev.adox.bundlesplus.common.network.handler.BundleClientMessageHandler;
import dev.adox.bundlesplus.common.network.handler.BundleServerMessageHandler;
import dev.adox.bundlesplus.common.network.message.BundleClientMessage;
import dev.adox.bundlesplus.common.network.message.BundleServerMessage;
import me.shedaniel.architectury.event.events.client.ClientTickEvent;
import me.shedaniel.architectury.networking.NetworkChannel;
import me.shedaniel.architectury.platform.Platform;
import me.shedaniel.architectury.registry.KeyBindings;
import me.shedaniel.architectury.registry.Registries;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.apache.logging.log4j.LogManager;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static dev.adox.bundlesplus.common.init.BundleResources.MOD_ID;
import static dev.adox.bundlesplus.common.init.BundleResources.NETWORK;

public class BundlesPlusMod {
    public static final LazyLoadedValue<Registries> REGISTRIES = new LazyLoadedValue<>(() -> Registries.get(MOD_ID));

    public static final DecimalFormat FORMAT = new DecimalFormat("#.#");
    private static final String KEYBIND_CATEGORY = "key.bundlesplus.category";
    private static final ResourceLocation ENABLE_OVERLAY_KEYBIND = new ResourceLocation("bundlesplus", "enable_overlay");

    private static KeyMapping enableOverlay;
    private static boolean enabled = false;
    //    private static final LazyLoadedValue<EntityType<Entity>> TESTING_ENTITY_TYPE = new LazyLoadedValue<>(() ->
//            EntityType.Builder.createNothing(MobCategory.MONSTER).sized(0f, 0f).noSave().build(null));
//    private static int threadNumber = 0;
//    private static final ThreadPoolExecutor EXECUTOR = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), r -> {
//        Thread thread = new Thread(r, "light-overlay-" + threadNumber++);
//        thread.setDaemon(true);
//        return thread;
//    });
    private static final Set<CubicChunkPos> POS = Collections.synchronizedSet(new HashSet<>());
    private static final Set<CubicChunkPos> CALCULATING_POS = Collections.synchronizedSet(new HashSet<>());
    private static final Minecraft CLIENT = Minecraft.getInstance();
    private static long ticks = 0;
    public static ConfigHolder<BundlesPlusConfig> CONFIG;

    public static void initClient() {
        enableOverlay = createKeyBinding(ENABLE_OVERLAY_KEYBIND, InputConstants.Type.KEYSYM, 296, KEYBIND_CATEGORY);
        KeyBindings.registerKeyBinding(enableOverlay);
//        ClientTickEvent.CLIENT_POST.register(BundlesPlusMod::tick);
    }

    public static void initialize() {
        // Load Config
        CONFIG = AutoConfig.register(BundlesPlusConfig.class, GsonConfigSerializer::new);
        CONFIG.getConfig();
        NETWORK = NetworkChannel.create(BundleResources.NETWORK_RESOURCE_LOCATION);
        NETWORK.register(BundleServerMessage.class,
            BundleServerMessage::encode, BundleServerMessage::decode,
            BundleServerMessageHandler::onMessageReceived);
        NETWORK.register(BundleClientMessage.class,
            BundleClientMessage::encode, BundleClientMessage::decode,
            BundleClientMessageHandler::onMessageReceived);
        BundleEvents.register();
        BundleItems.initialize();
    }


    private static KeyMapping createKeyBinding(ResourceLocation id, InputConstants.Type type, int code, String category) {
        return new KeyMapping("key." + id.getNamespace() + "." + id.getPath(), type, code, category);
    }


    private static void tick(Minecraft minecraft) {
        try {
            ticks++;
            if (CLIENT.player == null || !enabled) {
            } else {
                LocalPlayer player = CLIENT.player;
                ClientLevel world = CLIENT.level;
                CollisionContext collisionContext = CollisionContext.of(player);
            }
        } catch (Throwable throwable) {
            LogManager.getLogger().throwing(throwable);
        }
    }

}
