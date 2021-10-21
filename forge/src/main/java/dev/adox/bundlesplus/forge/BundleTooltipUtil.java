package dev.adox.bundlesplus.forge;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import dev.adox.bundlesplus.common.BundlesPlusMod;
import dev.adox.bundlesplus.common.init.BundleResources;
import dev.adox.bundlesplus.common.item.BundleItem;
import dev.adox.bundlesplus.common.util.BundleItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * Bundle Tooltip Utils
 *
 * @author JimiIT92
 */
@Mod.EventBusSubscriber(modid = BundleResources.MOD_ID, value = Dist.CLIENT)
public class BundleTooltipUtil {

    /**
     * Draw the Bundle Tooltip
     *
     * @param event Render Tooltip Event
     */
    @SubscribeEvent
    public static void onTooltipRender(final RenderTooltipEvent.Pre event) {
        if ((event.getStack().getItem() instanceof BundleItem && BundlesPlusMod.CONFIG.get().BUNDLE_TOOLTIP) || (BundleItemUtils.isShulkerBox(event.getStack()) && BundlesPlusMod.CONFIG.get().SHULKER_TOOLTIP)) {
            event.setCanceled(true);
            BundleTooltipUtil.drawBundleTooltip(event);
        }
    }

    /**
     * Cached Bundle Item Stacks
     */
    private static List<ItemStack> CACHED_ITEM_STACKS;
    /**
     * Cached Tooltip Item Stacks
     */
    private static List<ItemStack> CACHED_TOOLTIP_ITEM_STACKS;
    /**
     * Cache Tooltip Item Stacks Positions
     */
    private static HashMap<ItemStack, Map.Entry<Integer, Integer>> CACHED_TOOLTIP_POSITIONS;

    /**
     * Draw the Bundle Tooltip
     *
     * @param event Render Tooltip Event
     */
    public static void drawBundleTooltip(final RenderTooltipEvent.Pre event) {
        final ItemStack stack = event.getStack();
        PoseStack mStack = event.getMatrixStack();
        List<? extends FormattedText> textLines = event.getLines();
        int mouseX = event.getX();
        int mouseY = event.getY();
        int screenWidth = event.getScreenWidth();
        int screenHeight = event.getScreenHeight();
        int maxTextWidth = event.getMaxWidth();
        Font font = event.getFontRenderer();
        int backgroundColor = GuiUtils.DEFAULT_BACKGROUND_COLOR;
        int borderColorStart = GuiUtils.DEFAULT_BORDER_COLOR_START;
        int borderColorEnd = GuiUtils.DEFAULT_BORDER_COLOR_END;
        List<ItemStack> bundleItems = BundleItemUtils.getItemsFromBundle(stack);
        boolean useCached = CACHED_ITEM_STACKS != null && bundleItems.size() == CACHED_ITEM_STACKS.size();
        if (useCached) {
            for (int i = 0; i < bundleItems.size(); i++) {
                if (!ItemStack.isSame(bundleItems.get(i), CACHED_ITEM_STACKS.get(i))) {
                    useCached = false;
                    break;
                }
            }
        }
        if (!useCached) {
            CACHED_ITEM_STACKS = bundleItems;
            CACHED_TOOLTIP_POSITIONS = new HashMap<>();
            List<ItemStack> tooltipItems = new ArrayList<>();
            Random random = new Random();
            bundleItems.forEach(x -> {
                for (int i = 0; i < x.getCount(); i++) {
                    ItemStack tooltipStack = x.copy();
                    tooltipStack.setCount(1);
                    tooltipStack.setHoverName(new TextComponent("stack_" + i));
                    tooltipItems.add(tooltipStack);
                    CACHED_TOOLTIP_POSITIONS.put(tooltipStack,
                        new AbstractMap.SimpleEntry<>(random.nextInt(4), random.nextInt(4)));
                }
            });
            Collections.shuffle(tooltipItems);
            CACHED_TOOLTIP_ITEM_STACKS = tooltipItems;
        }

        int rows = Math.min((CACHED_TOOLTIP_ITEM_STACKS.size() / 16) + 1, 4);

        if (!textLines.isEmpty()) {
            RenderSystem.disableRescaleNormal();
            RenderSystem.disableDepthTest();
            int tooltipTextWidth = 0;

            for (FormattedText textLine : textLines) {
                int textLineWidth = font.width(textLine);
                if (textLineWidth > tooltipTextWidth)
                    tooltipTextWidth = textLineWidth;
            }

            boolean needsWrap = false;

            int titleLinesCount = 1;
            int tooltipX = mouseX + 12;
            if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
                tooltipX = mouseX - 16 - tooltipTextWidth;
                if (tooltipX < 4) {
                    if (mouseX > screenWidth / 2)
                        tooltipTextWidth = mouseX - 12 - 8;
                    else
                        tooltipTextWidth = screenWidth - 16 - mouseX;
                    needsWrap = true;
                }
            }

            if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
                tooltipTextWidth = maxTextWidth;
                needsWrap = true;
            }

            if (needsWrap) {
                int wrappedTooltipWidth = 0;
                List<FormattedText> wrappedTextLines = new ArrayList<>();
                for (int i = 0; i < textLines.size(); i++) {
                    FormattedText textLine = textLines.get(i);
                    List<FormattedText> wrappedLine = font.getSplitter().splitLines(textLine, tooltipTextWidth, Style.EMPTY);
                    if (i == 0)
                        titleLinesCount = wrappedLine.size();

                    for (FormattedText line : wrappedLine) {
                        int lineWidth = font.width(line);
                        if (lineWidth > wrappedTooltipWidth)
                            wrappedTooltipWidth = lineWidth;
                        wrappedTextLines.add(line);
                    }
                }
                tooltipTextWidth = wrappedTooltipWidth;
                textLines = wrappedTextLines;

                if (mouseX > screenWidth / 2)
                    tooltipX = mouseX - 16 - tooltipTextWidth;
                else
                    tooltipX = mouseX + 12;
            }

            int tooltipY = mouseY - 12;
            int tooltipHeight = 8;

            if (textLines.size() > 1) {
                tooltipHeight += (textLines.size() - 1) * 10;
                if (textLines.size() > titleLinesCount)
                    tooltipHeight += 2;
            }

            if (!CACHED_TOOLTIP_ITEM_STACKS.isEmpty()) {
                tooltipTextWidth += (rows == 1 && CACHED_TOOLTIP_ITEM_STACKS.size() <= 9 ? 0 : 16) * 3;
                tooltipHeight += rows * 8;
            }

            if (tooltipY < 4)
                tooltipY = 4;
            else if (tooltipY + tooltipHeight + 4 > screenHeight)
                tooltipY = screenHeight - tooltipHeight - 4;

            final int zLevel = 400;
            RenderTooltipEvent.Color colorEvent = new RenderTooltipEvent.Color(stack, textLines, mStack, tooltipX, tooltipY, font, backgroundColor, borderColorStart, borderColorEnd);
            MinecraftForge.EVENT_BUS.post(colorEvent);
            backgroundColor = colorEvent.getBackground();
            borderColorStart = colorEvent.getBorderStart();
            borderColorEnd = colorEvent.getBorderEnd();

            mStack.pushPose();
            Matrix4f mat = mStack.last().pose();
            GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(mat, zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
            GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            GuiUtils.drawGradientRect(mat, zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
            GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
            GuiUtils.drawGradientRect(mat, zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostBackground(stack, textLines, mStack, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight));

            MultiBufferSource.BufferSource renderType = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            mStack.translate(0.0D, 0.0D, zLevel);

            int tooltipTop = tooltipY;
            for (int lineNumber = 0; lineNumber < ((List) textLines).size(); ++lineNumber) {
                FormattedText line = (FormattedText) ((List) textLines).get(lineNumber);
                if (line != null) {
                    font.drawInBatch(Language.getInstance().getVisualOrder(line), (float) tooltipX, (float) tooltipY, -1, true, mat, renderType, false, 0, 15728880);
                }
                if (lineNumber + 1 == titleLinesCount) {
                    tooltipY += 2;
                }
                tooltipY += 10;
            }

            tooltipX -= 4;
            tooltipY -= 5;

            float prevZLevel = Minecraft.getInstance().getItemRenderer().blitOffset;
            Minecraft.getInstance().getItemRenderer().blitOffset = zLevel + 1;
            for (int r = 0; r < rows; r++) {
                List<ItemStack> rowItemStacks = CACHED_TOOLTIP_ITEM_STACKS.subList(r * 16, Math.min(CACHED_TOOLTIP_ITEM_STACKS.size(), ((r + 1) * 16) - 1));
                for (int i = 0; i < rowItemStacks.size(); i++) {
                    ItemStack bundleItem = rowItemStacks.get(i);
                    Map.Entry<Integer, Integer> position = CACHED_TOOLTIP_POSITIONS.get(bundleItem);
                    renderItemModelIntoGUI(bundleItem, tooltipX + (8 * (i % 16)) + position.getKey(), tooltipY + (8 * r) + position.getValue());
                }
            }
            Minecraft.getInstance().getItemRenderer().blitOffset = prevZLevel;

            renderType.endBatch();
            mStack.popPose();

            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostText(stack, textLines, mStack, tooltipX, tooltipTop, font, tooltipTextWidth, tooltipHeight));

            RenderSystem.enableDepthTest();
            RenderSystem.enableRescaleNormal();
        }
    }

    /**
     * Render Bundle Items
     *
     * @param stack Item Stack
     * @param x     Tooltip X position
     * @param y     Tooltip Y position
     */
    private static void renderItemModelIntoGUI(ItemStack stack, int x, int y) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel bakedmodel = itemRenderer.getModel(stack, null, null);
        RenderSystem.pushMatrix();
        Minecraft.getInstance().textureManager.bind(TextureAtlas.LOCATION_BLOCKS);
        Objects.requireNonNull(Minecraft.getInstance().textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS)).setFilter(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.translatef((float) x, (float) y, 100.0F + itemRenderer.blitOffset);
        RenderSystem.translatef(8.0F, 8.0F, 0.0F);
        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
        RenderSystem.scalef(8.0F, 8.0F, 8.0F);
        PoseStack matrixstack = new PoseStack();
        MultiBufferSource.BufferSource irendertypebuffer$impl = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean flag = !bakedmodel.usesBlockLight();
        if (flag) {
            Lighting.setupForFlatItems();
        }
        itemRenderer.render(stack, ItemTransforms.TransformType.GUI, false, matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);

        irendertypebuffer$impl.endBatch();
        RenderSystem.enableDepthTest();
        if (flag) {
            Lighting.setupFor3DItems();
        }

        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
    }
}
