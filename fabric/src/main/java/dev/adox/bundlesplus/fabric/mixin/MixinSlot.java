package dev.adox.bundlesplus.fabric.mixin;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Slot.class)
public interface MixinSlot {
    @Accessor
    public abstract int getSlot();

}
