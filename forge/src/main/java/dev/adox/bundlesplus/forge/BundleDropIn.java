package dev.adox.bundlesplus.forge;

import dev.adox.bundlesplus.common.util.BundleItemUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.EmptyHandler;
import vazkii.arl.util.AbstractDropIn;
import vazkii.arl.util.ItemNBTHelper;

public class BundleDropIn extends AbstractDropIn {

	@Override
	public boolean canDropItemIn(Player player, ItemStack stack, ItemStack incoming) {
		return BundleItemUtils.canAddItemStackToBundle(stack,incoming);
	}

	@Override
	public ItemStack dropItemIn(Player player, ItemStack stack, ItemStack incoming) {
		BundleItemUtils.addItemStackToBundle(stack,incoming, false);
		return stack;
	}

	private boolean tryAddToShulkerBox(ItemStack shulkerBox, ItemStack stack, boolean simulate) {
		if (!BundleItemUtils.isShulkerBox(shulkerBox))
			return false;

		CompoundTag cmp = ItemNBTHelper.getCompound(shulkerBox, "BlockEntityTag", false);
		if(cmp.contains("LootTable"))
			return false;
		
		if (cmp != null) {
			BlockEntity te = null;
			cmp = cmp.copy();	
			cmp.putString("id", "minecraft:shulker_box");				
			if (shulkerBox.getItem() instanceof BlockItem) {
				Block shulkerBoxBlock = Block.byItem(shulkerBox.getItem());
				BlockState defaultState = shulkerBoxBlock.defaultBlockState();
				if (shulkerBoxBlock.hasTileEntity(defaultState)) {
					te = shulkerBoxBlock.createTileEntity(defaultState, null);
					te.load(defaultState, cmp);
				}
			}

			if (te != null) {
				LazyOptional<IItemHandler> handlerHolder = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				if (handlerHolder.isPresent()) {
					IItemHandler handler = handlerHolder.orElseGet(EmptyHandler::new);
					ItemStack result = ItemHandlerHelper.insertItem(handler, stack.copy(), simulate);
					boolean did = result.isEmpty() || result.getCount() != stack.getCount();

					if (!simulate && did) {
						stack.setCount(result.getCount());
						te.save(cmp);
						ItemNBTHelper.setCompound(shulkerBox, "BlockEntityTag", cmp);
					}

					return did;
				}
			}
		}

		return false;
	}

}
