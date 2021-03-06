package dev.adox.bundlesplus.common.network.message;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

/**
 * Bundle Client Message
 *
 * @author JimiIT92
 */
public class BundleClientMessage {

    /**
     * Bundle Item Stack
     */
    public ItemStack bundle;
    /**
     * Item Stack Slot Id
     */
    public int slotId;
    /**
     * Item Stack for Bundle
     */
    public ItemStack slotStack;
    /**
     * If the Bundle should be cleared
     */
    public boolean empty;
    /**
     * If the Empty sound should be played
     */
    public boolean playEmptySound;

    /**
     * Default constructor
     */
    public BundleClientMessage() {
        this(ItemStack.EMPTY,0, ItemStack.EMPTY, false, false);
    }

    /**
     * Constructor
     *
     * @param bundle Bundle Item Stack
     * @param slotId Item Stack Slot Id
     * @param slotStack Item Stack for Bundle
     * @param empty If the Bundle should be cleared
     * @param playEmptySound If the Empty sound should be played
     */
    public BundleClientMessage(ItemStack bundle, int slotId, ItemStack slotStack, boolean empty, boolean playEmptySound) {
        this.bundle = bundle;
        this.slotId = slotId;
        this.slotStack = slotStack;
        this.empty = empty;
        this.playEmptySound = playEmptySound;
    }

    /**
     * Deserialize the Message
     *
     * @param buffer Packet Buffer
     * @return Message
     */
    public static BundleClientMessage decode(FriendlyByteBuf buffer) {
        BundleClientMessage message = new BundleClientMessage();
        message.bundle = buffer.readItem();
        message.slotId = buffer.readInt();
        message.slotStack = buffer.readItem();
        message.empty = buffer.readBoolean();
        message.playEmptySound = buffer.readBoolean();
        return message;
    }

    /**
     * Serialize the Message
     *
     * @param buffer Packet Buffer
     */
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeItem(this.bundle);
        buffer.writeInt(this.slotId);
        buffer.writeItem(this.slotStack);
        buffer.writeBoolean(this.empty);
        buffer.writeBoolean(this.playEmptySound);
    }
}
