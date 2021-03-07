package dev.adox.bundlesplus.common.init;

import me.shedaniel.architectury.networking.NetworkChannel;
import net.minecraft.resources.ResourceLocation;

/**
 * Bundle Resources
 *
 * @author JimiIT92
 */
public final class BundleResources {
    /**
     * Mod ID
     */
    public static final String MOD_ID = "bundlesplus";
    /**
     * Bundle Item Resource Name
     */
    public static final String BUNDLE_ITEM_RESOURCE_NAME = "bundle";
    /**
     * Bundle Full NBT Tag Resource Location
     */
    public static final ResourceLocation BUNDLE_FULL_NBT_RESOURCE_LOCATION = new ResourceLocation(MOD_ID, "bundle_full");
    /**
     * Bundle Items NBT Tag Resource Location
     */
    public static final String BUNDLE_ITEMS_LIST_NBT_RESOURCE_LOCATION = "bundle_items";
    /**
     * Max Bundle Items Count
     */
    public static final int MAX_BUNDLE_ITEMS = 64;
    /**
     * Network Channel
     */
    public static NetworkChannel NETWORK;
    /**
     * Bundle Server Message ID
     */
    public static final byte BUNDLE_SERVER_MESSAGE_ID = 1;
    /**
     * Bundle Client Message ID
     */
    public static final byte BUNDLE_CLIENT_MESSAGE_ID = 2;
    /**
     * Message Protocol Version
     */
    public static final String MESSAGE_PROTOCOL_VERSION = "1.3";
    /**
     * Network Resource Location
     */
    public static final ResourceLocation NETWORK_RESOURCE_LOCATION = new ResourceLocation(MOD_ID, "network_channel");
    /**
     * Bundle Ignored Items Tag
     */
    public static final ResourceLocation BUNDLE_IGNORED_ITEMS_TAG = new ResourceLocation(MOD_ID, "bundle_ignored");
    /**
     * Shulker Ignored Items Tag
     */
    public static final ResourceLocation SHULKER_IGNORED_ITEMS_TAG = new ResourceLocation(MOD_ID, "shulker_ignored");
    /**
     * Shulker Like Items Tag
     */
    public static final ResourceLocation SHULKER_LIKE = new ResourceLocation(MOD_ID, "shulker_box_like");
}
