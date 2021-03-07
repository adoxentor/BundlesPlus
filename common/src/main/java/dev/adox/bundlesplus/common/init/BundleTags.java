
package dev.adox.bundlesplus.common.init;

import me.shedaniel.architectury.event.events.BlockEvent;
import me.shedaniel.architectury.hooks.TagHooks;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.Tag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static dev.adox.bundlesplus.common.init.BundleResources.MOD_ID;
import static dev.adox.bundlesplus.common.init.BundleResources.SHULKER_LIKE;

public class BundleTags {
//    public static Tag.Named<Item> SHULKER_LIKE = TagHooks.getItemOptional(SHULKER_LIKE);

    public static void initialize() {
        // This will not be present, but it should return an empty tag
        Tag.Named<Block> heartParticles = TagHooks.getBlockOptional(new ResourceLocation(MOD_ID, "heart_particles"));
        // This will act like a normal tag, we have emerald block here
        Tag.Named<Block> heartParticles2 = TagHooks.getBlockOptional(new ResourceLocation(MOD_ID, "heart_particles2"));
//        Tag.Named<Item> shulkerLike = TagHooks.getItemOptional(SHULKER_LIKE);
        BlockEvent.BREAK.register((world, pos, state, player, xp) -> {
            if (player != null && !world.isClientSide() && (state.is(heartParticles) || state.is(heartParticles2))) {
                ((ServerLevel) world).sendParticles(player, ParticleTypes.HEART, false, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.0, 0.0, 0.0, 0.0);
            }

            return InteractionResult.PASS;
        });
    }
}