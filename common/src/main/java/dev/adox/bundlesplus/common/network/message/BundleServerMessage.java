package dev.adox.bundlesplus.common.network.message;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

/**
 * Bundle Server Message
 *
 * @author JimiIT92
 */
public class BundleServerMessage {

    /**
     * Bundle Item Stack
     */
    public ItemStack bundle;
    /**
     * Item Stack Slot Id
     */
    public int slotId;
    /**
     * If the Bundle should be cleared
     */
    public boolean empty;
    /**
     * If the Bundle should be cleared
     */
    public boolean reversed;

    /**
     * Default constructor
     */
    public BundleServerMessage() {
        this(ItemStack.EMPTY,0, false, false);
    }


    /**
     * Constructor
     *  @param bundle Bundle Item Stack
     * @param slotId Item Stack Slot Id
     * @param empty If the Bundle should be cleared
     * @param reversed
     */
    public BundleServerMessage(ItemStack bundle, int slotId, boolean empty, boolean reversed) {
        this.bundle = bundle;
        this.slotId = slotId;
        this.empty = empty;
        this.reversed = reversed;
    }

    /**
     * Deserialize the Message
     *
     * @param buffer Packet Buffer
     * @return Message
     */
    public static BundleServerMessage decode(FriendlyByteBuf buffer) {
        BundleServerMessage message = new BundleServerMessage();
        message.bundle = buffer.readItem();
        message.slotId = buffer.readInt();
        message.empty = buffer.readBoolean();
        message.reversed = buffer.readBoolean();
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
        buffer.writeBoolean(this.empty);
        buffer.writeBoolean(this.reversed);
    }
}
