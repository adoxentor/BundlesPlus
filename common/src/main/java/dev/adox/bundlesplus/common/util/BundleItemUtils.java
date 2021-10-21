package dev.adox.bundlesplus.common.util;


import dev.adox.bundlesplus.common.BundlesPlusMod;
import dev.adox.bundlesplus.common.PlatformUtil;
import dev.adox.bundlesplus.common.init.BundleResources;
import dev.adox.bundlesplus.common.item.BundleItem;
import me.shedaniel.architectury.hooks.TagHooks;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dev.adox.bundlesplus.common.init.BundleResources.*;

/**
 * Bundle Item Utilities
 *
 * @author JimiIT92
 */
public final class BundleItemUtils {

    /**
     * Check if the Item Stack is a Bundle Item Stack
     *
     * @param bundle Item Stack
     * @return True if is a Bundle Item Stack, False otherwise
     */
    public static boolean isBundle(ItemStack bundle) {
        return bundle.getItem() instanceof BundleItem || isShulkerBox(bundle);
    }

    /**
     * Check if a Bundle is full
     *
     * @param bundle Bundle Item Stack
     * @return True if the Bundle is full, False otherwise
     */
    public static boolean isFull(ItemStack bundle) {
        if (isShulkerBox(bundle)) {
            List<ItemStack> itemsFromBundle = getItemsFromBundle(bundle);
            return itemsFromBundle.stream().anyMatch(ItemStack::isEmpty);
        }
        return getBundleItemsCount(bundle) >= bundle.getMaxDamage();
    }

    /**
     * Check if the Bundle is empty
     *
     * @param bundle Bundle Item Stack
     * @return True if the Bundle is empty, False otherwise
     */
    public static boolean isEmpty(ItemStack bundle) {
        return getBundleItemsCount(bundle) == 0;
    }

    /**
     * Check if an Item Stack can be added to a Bundle
     *
     * @param bundle Bundle Item Stack
     * @param stack  Item Stack to add
     * @return True if the Item Stack can be added to a Bundle, False otherwise
     */
    public static boolean canAddItemStackToBundle(ItemStack bundle, ItemStack stack) {
        if(bundle.getCount()>1){
            return false;
        }

        if (isShulkerBox(bundle)) {
            return (!isIgnoredShulker(stack))
                && (getItemsFromBundle(bundle).isEmpty()
                || getItemsFromBundle(bundle).stream().
                anyMatch(itemStack -> itemStack.isEmpty()
                    || (ItemStack.isSame(itemStack, stack) &&
                    ItemStack.tagMatches(itemStack, stack) &&
                    (getItemStackWeight(itemStack) < itemStack.getMaxStackSize()))
                ));
        }

        if (!isBundle(bundle) || isFull(bundle) || isIgnoredBundle(stack)) {
            return false;
        }
        ItemStack bundleItemStack = getItemStackFor(bundle, stack);
        return bundleItemStack.isEmpty() || stack.getMaxStackSize() == 1 || bundleItemStack.getCount() < getMaxStackSizeForBundle(stack);
    }

    /**
     * Check if an Item Stack is for a Container Block
     *
     * @param stack Item Stack
     * @return True if the Item Stack is for a Container Block, False otherwise
     */
    private static boolean isIgnoredBundle(ItemStack stack) {
        return machingTag(stack, BUNDLE_IGNORED_ITEMS_TAG);
    }

    /**
     * Check if an Item Stack is for a Container Block
     *
     * @param stack Item Stack
     * @return True if the Item Stack is for a Container Block, False otherwise
     */
    private static boolean isIgnoredShulker(ItemStack stack) {
        return machingTag(stack, SHULKER_IGNORED_ITEMS_TAG);
    }

    public static boolean isShulkerBox(ItemStack itemStack) {
        return machingTag(itemStack, SHULKER_LIKE);
    }

    private static boolean machingTag(ItemStack itemStack, ResourceLocation shulkerLike) {
        return TagHooks.getItemOptional(shulkerLike).contains(itemStack.getItem())
            || ((itemStack.getItem() instanceof BlockItem) && TagHooks.getBlockOptional(shulkerLike).contains(((BlockItem) itemStack.getItem()).getBlock()));
    }

    /**
     * Add an Item Stack to a Bundle
     *
     * @param bundle Bundle Item Stack
     * @param stack  Item Stack to add
     */
    public static void addItemStackToBundle(ItemStack bundle, ItemStack stack) {
        if(bundle.getCount()>1){
            return;
        }
        if (isShulkerBox(bundle)) {
            CompoundTag blockEntityTag = bundle.getTagElement("BlockEntityTag");
            CompoundTag compoundnbt = blockEntityTag;
            if (compoundnbt == null) {
                compoundnbt = new CompoundTag();
            }
            if (!compoundnbt.contains("Items", 9)) {
                compoundnbt.put("Items", new ListTag());
                bundle.addTagElement("BlockEntityTag", compoundnbt);
            }
            NonNullList<ItemStack> nonnulllist = PlatformUtil.loadAllItems(bundle);

            for (int i = 0; i < nonnulllist.size(); i++) {
                ItemStack itemStack = nonnulllist.get(i);
                if (ItemStack.isSame(itemStack, stack) &&
                    ItemStack.tagMatches(itemStack, stack) &&
                    itemStack.getCount() < itemStack.getMaxStackSize()) {
                    int j = itemStack.getCount() + stack.getCount();
                    int maxSize = stack.getMaxStackSize();
                    if (j <= maxSize) {
                        stack.setCount(0);
                        itemStack.setCount(j);
                        break;
                    } else if (itemStack.getCount() < maxSize) {
                        stack.shrink(maxSize - itemStack.getCount());
                        itemStack.setCount(maxSize);
                    }
                }
            }
            if (!stack.isEmpty()) {
                for (int i = 0; i < nonnulllist.size(); i++) {
                    ItemStack itemStack = nonnulllist.get(i);
                    if (itemStack.isEmpty()) {
                        nonnulllist.set(i, stack.copy());
                        stack.setCount(0);
                        break;
                    }
                }
            }

            ContainerHelper.saveAllItems(compoundnbt, nonnulllist, true);
            bundle.addTagElement("BlockEntityTag", compoundnbt);
            return;
        }

        if (!isBundle(bundle) || isFull(bundle) || isBundle(stack)) {
            return;
        }
        ItemStack stackToAdd = stack.copy();
        int maxItemsToAdd = bundle.getMaxDamage() - getBundleItemsCount(bundle);
        maxItemsToAdd /= getOneItemWeight(stackToAdd);
        if (maxItemsToAdd < 1)
            return;
        stackToAdd.setCount(Math.min(getMaxStackSizeForBundleToInsert(stackToAdd), maxItemsToAdd));
        CompoundTag bundleTag = bundle.getOrCreateTag();
        ListTag items = bundleTag.getList(BundleResources.BUNDLE_ITEMS_LIST_NBT_RESOURCE_LOCATION, 10);
        CompoundTag itemStackNbt = new CompoundTag();
        ItemStack stackFromBundle = getItemStackFor(bundle, stackToAdd);
        int index = getItemStackIndex(bundle, stackFromBundle);
        if (!stackFromBundle.isEmpty() && stack.getMaxStackSize() > 1) {
            stackToAdd.setCount(Math.min(stackToAdd.getCount(), getMaxStackSizeForBundle(stack) - stackFromBundle.getCount()));
            stackFromBundle.setCount(stackFromBundle.getCount() + stackToAdd.getCount());
        }
        if (index != -1 && stack.getMaxStackSize() > 1) {
            stackFromBundle.save(itemStackNbt);
            items.set(index, itemStackNbt);
        } else {
            stackToAdd.save(itemStackNbt);
            items.add(itemStackNbt);
        }
        bundleTag.put(BundleResources.BUNDLE_ITEMS_LIST_NBT_RESOURCE_LOCATION, items);
        bundle.setTag(bundleTag);

        stack.setCount(stack.getCount() - stackToAdd.getCount());
        bundle.setTag(bundleTag);
        int itemsCount = getBundleItemsCount(bundle);
        if (itemsCount > 0) {
            bundleTag.putInt("Damage", bundle.getMaxDamage() - itemsCount);
        } else {
            bundleTag.remove("Damage");
        }
        bundle.setTag(bundleTag);
    }

    /**
     * Empty a Bundle
     *
     * @param bundle Bundle
     * @param player Player
     */
    public static void emptyBundle(ItemStack bundle, Player player) {
        if (!isBundle(bundle) || isEmpty(bundle)) {
            return;
        }
        getItemsFromBundle(bundle).forEach(item -> {
            if (!player.addItem(item)) {
                player.drop(item, true);
            } else if (getItemStackWeight(item) > 0) {
                player.drop(item, true);
            }
        });
        if (isShulkerBox(bundle)) {
            CompoundTag compoundnbt = bundle.getTagElement("BlockEntityTag");
            if (compoundnbt == null) {
                compoundnbt = new CompoundTag();
            }
            compoundnbt.put("Items", new ListTag());
            bundle.addTagElement("BlockEntityTag", compoundnbt);
            return;

        }
        CompoundTag bundleTag = bundle.getOrCreateTag();
        ListTag items = bundleTag.getList(BundleResources.BUNDLE_ITEMS_LIST_NBT_RESOURCE_LOCATION, 10);
        items.clear();
        bundleTag.put(BundleResources.BUNDLE_ITEMS_LIST_NBT_RESOURCE_LOCATION, items);
        bundleTag.remove("Damage");
        bundle.setTag(bundleTag);
    }


    /**
     * Get how many Items are inside the Bundle
     *
     * @param bundle Bundle Item Stack
     * @return Bundle Items Count
     */
    public static int getBundleItemsCount(ItemStack bundle) {
        List<ItemStack> itemStacks = Objects.requireNonNull(getItemsFromBundle(bundle));
        return getItemsCount(itemStacks);
    }

    private static int getItemsCount(List<ItemStack> itemStacks) {
        return itemStacks.stream().mapToInt(itemStack -> getItemStackWeight(itemStack)).sum();
    }

    private static int getItemStackWeight(ItemStack itemStack) {
        int count = itemStack.getCount();
        int maxStackSize = itemStack.getMaxStackSize();
        switch (BundlesPlusMod.CONFIG.get().STACK_MODE) {
            case All1:
                return count;
            case Vanilla:
                if (maxStackSize < 4)
                    return 64;
                if (maxStackSize < 16)
                    return 4 * count;
            case UpTo16:
                return maxStackSize < 64 ? 4 * count : count;
        }
        return count;
    }

    private static int getOneItemWeight(ItemStack itemStack) {
        int maxStackSize = itemStack.getMaxStackSize();
        switch (BundlesPlusMod.CONFIG.get().STACK_MODE) {
            case All1:
                return 1;
            case Vanilla:
                if (maxStackSize < 4)
                    return 64;
                if (maxStackSize < 16)
                    return 16;
            case UpTo16:
                if (maxStackSize < 64)
                    return 4;
        }
        return 1;
    }

    /**
     * Get the Item Stacks inside the Bundle
     *
     * @param bundle Bundle Item Stack
     * @return Bundle's Item Stacks
     */
    public static List<ItemStack> getItemsFromBundle(ItemStack bundle) {
        if (isShulkerBox(bundle)) {
            return PlatformUtil.loadAllItems(bundle);
        }
        if (!isBundle(bundle)) {
            return Collections.emptyList();
        }
        CompoundTag bundleTag = bundle.getOrCreateTag();
        ListTag items = bundleTag.getList(BundleResources.BUNDLE_ITEMS_LIST_NBT_RESOURCE_LOCATION, 10);
        return items.stream().map(x -> ItemStack.of((CompoundTag) x)).collect(Collectors.toList());
    }

    /**
     * Get the Item Stack for an Item
     *
     * @param bundle Bundle Item Stack
     * @param stack  Item Stack
     * @return Item Stack for the Item or Empty Item Stack if not found
     */
    private static ItemStack getItemStackFor(ItemStack bundle, ItemStack stack) {
        return getItemsFromBundle(bundle).stream().filter(x -> ItemStack.isSame(x, stack)
            && ItemStack.tagMatches(x, stack)).findFirst().orElse(ItemStack.EMPTY);
    }

    /**
     * Get the Item Stack index inside the Bundle
     *
     * @param bundle Bundle Item Stack
     * @param stack  Item Stack to find
     * @return Item Stack index
     */
    private static int getItemStackIndex(ItemStack bundle, ItemStack stack) {
        List<ItemStack> items = getItemsFromBundle(bundle);
        return IntStream.range(0, items.size())
            .filter(i -> stack.sameItem(items.get(i)) && ItemStack.tagMatches(stack, items.get(i)))
            .findFirst().orElse(-1);
    }

    /**
     * Get the max stack size for an Item Stack
     * to be put inside a Bundle
     *
     * @return Max stack size for a Bundle
     */
    private static int getMaxStackSizeForBundleToInsert(ItemStack itemStack) {
        int maxStackSize = itemStack.getMaxStackSize();
        int count = itemStack.getCount();
        return Math.min(getMaxStackSizeForBundle(itemStack), count);
//        switch (BundlesPlusMod.CONFIG.get().STACK_MODE) {
//            case Vanilla:
//                if (maxStackSize < 4)
//                    return Math.min(count,maxStackSize);
//                if (maxStackSize < 16)
//                    return Math.min(count,maxStackSize);
//            case UpTo16:
//                if (maxStackSize < 16)
//                    return Math.min(count,16);
//                if (maxStackSize < 32)
//                    return Math.min(count,maxStackSize);
//            default:
//            case All1:
//                return Math.min(count,32);
//        }
    }

    /**
     * Get the max stack size allowed inside
     * a Bundle for an Item
     *
     * @return Max Item Stack size inside the Bundle
     */
    private static int getMaxStackSizeForBundle(ItemStack itemStack) {
        int maxStackSize = itemStack.getMaxStackSize();
        switch (BundlesPlusMod.CONFIG.get().STACK_MODE) {
            case Vanilla:
                return maxStackSize;
            case UpTo16:
                if (maxStackSize < 16)
                    return 16;
            default:
            case All1:
                return 64;
        }
    }

    public static ItemStack removeFirstItemStack(ItemStack bundle, boolean reversed) {
        ItemStack stack = ItemStack.EMPTY;
        if (isShulkerBox(bundle)) {
            CompoundTag compoundnbt = bundle.getTagElement("BlockEntityTag");
            if (compoundnbt == null) {
                compoundnbt = new CompoundTag();
            }
            if (!compoundnbt.contains("Items", 9)) {
                compoundnbt.put("Items", new ListTag());
                bundle.addTagElement("BlockEntityTag", compoundnbt);
            }

            NonNullList<ItemStack> nonnulllist = NonNullList.create();
            nonnulllist.addAll(PlatformUtil.loadAllItems(bundle));
            if (reversed) {
                for (int i = 0; i < nonnulllist.size(); i++) {
                    ItemStack itemStack = nonnulllist.get(i);
                    if (!itemStack.isEmpty()) {
                        nonnulllist.remove(i);
                        NonNullList<ItemStack> subList = NonNullList.create();
                        subList.addAll(nonnulllist.subList(i, nonnulllist.size()));
                        nonnulllist = subList;
                        stack = itemStack;
                        break;
                    }
                }
            } else {
                for (int i = nonnulllist.size() - 1; i >= 0; i--) {
                    ItemStack itemStack = nonnulllist.get(i);
                    if (!itemStack.isEmpty()) {
                        nonnulllist.set(i, ItemStack.EMPTY);
                        stack = itemStack;
                        break;
                    }
                }
            }

//            nonnulllist.add(stack);
            ContainerHelper.saveAllItems(compoundnbt, nonnulllist, true);
            bundle.addTagElement("BlockEntityTag", compoundnbt);
            return stack;
        }

        if (!isBundle(bundle) || isEmpty(bundle)) {
            return stack;
        }
//        ItemStack stackToAdd = stack.copy();
        CompoundTag bundleTag = bundle.getOrCreateTag();
        ListTag items = bundleTag.getList(BundleResources.BUNDLE_ITEMS_LIST_NBT_RESOURCE_LOCATION, 10);
        CompoundTag itemStackNbt = new CompoundTag();
        List<ItemStack> itemsFromBundle = getItemsFromBundle(bundle);
        int index = reversed ? 0 : itemsFromBundle.size() - 1;
        stack = itemsFromBundle.get(index);
        items.remove(index);
        itemsFromBundle.remove(index);
        bundleTag.put(BundleResources.BUNDLE_ITEMS_LIST_NBT_RESOURCE_LOCATION, items);
        int itemsCount = getItemsCount(itemsFromBundle);
        if (itemsCount > 0) {
            bundleTag.putInt("Damage", bundle.getMaxDamage() - itemsCount);
        } else {
            bundleTag.remove("Damage");
        }
        bundle.setTag(bundleTag);
        return stack;
    }
}
