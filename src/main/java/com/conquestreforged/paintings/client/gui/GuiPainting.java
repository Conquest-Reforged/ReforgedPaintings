package com.conquestreforged.paintings.client.gui;

import com.conquestreforged.paintings.common.art.Art;
import com.conquestreforged.paintings.common.art.ModArt;
import com.conquestreforged.paintings.common.art.VanillaArt;
import com.conquestreforged.paintings.common.entity.PaintingArt;
import com.conquestreforged.paintings.common.entity.PaintingVariant;
import com.mojang.blaze3d.platform.GlStateManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public class GuiPainting extends Screen {

    private final List<Art> arts;
    private final ItemStack stack;
    private final ResourceLocation texture;
    private final String type;
    private final String typeUnlocal;

    private int artIndex;
    private int hoveredIndex = -1;

    public GuiPainting(ItemStack stack, PaintingVariant type, PaintingArt art) {
        super(new StringTextComponent("Painting Selector"));
        this.stack = stack;
        this.type = type.getName();
        this.typeUnlocal = type.getUnlocalizedName();
        this.texture = type.getResourceLocation();
        this.arts = ModArt.ALL;
        this.artIndex = Art.indexOf(art, ModArt.ALL);
        this.font = Minecraft.getInstance().fontRenderer;
    }

    public GuiPainting(ItemStack stack, PaintingType art) {
        super(new StringTextComponent("Painting Selector"));
        this.stack = stack;
        this.type = "Vanilla";
        this.typeUnlocal = "";
        this.arts = VanillaArt.ALL;
        this.texture = VanillaArt.location;
        this.artIndex = Art.indexOf(art, VanillaArt.ALL);
        this.font = Minecraft.getInstance().fontRenderer;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        Minecraft.getInstance().displayGuiScreen(null);
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean keyPressed(int typedChar, int keyCode, int what) {
        Minecraft.getInstance().displayGuiScreen(null);
        return super.keyPressed(typedChar, keyCode, what);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();

        int centerX = width / 2;
        int centerY = height / 2;
        hoveredIndex = -1;

        GlStateManager.enableAlphaTest();
        GlStateManager.enableTexture();
        Minecraft.getInstance().getTextureManager().bindTexture(texture);

        for (int i = 5; i >= 0; i--) {
            if (i > 0) {
                drawArt(mouseX, mouseY, centerX, centerY, -i);
            }
            drawArt(mouseX, mouseY, centerX, centerY, +i);
        }

        drawLabel(centerX, centerY);
    }

    @Override
    public void onClose() {
        int artIndex = hoveredIndex == -1 ? this.artIndex : hoveredIndex;
        String art = arts.get(artIndex).getName();

        CompoundNBT tag = new CompoundNBT();
        tag.putString(Art.TYPE_TAG, type);
        tag.putString(Art.ART_TAG, art);

        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeCompoundTag(tag);

//        FMLProxyPacket packet = new FMLProxyPacket(buffer, Proxy.SYNC_CHANNEL);
//        PaintingsMod.getProxy().sendPacket(packet);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double scroll) {
        if (scroll > 0) {
            artIndex = artIndex - 1;
            if (artIndex < 0) {
                artIndex = arts.size() - 1;
            }
        }

        if (scroll < 0) {
            artIndex = artIndex + 1;
            if (artIndex >= arts.size()) {
                artIndex = 0;
            }
        }
        return false;
    }

    private void drawArt(int mx, int my, int cx, int cy, int di) {
        int index = artIndex + di;

        if (index < 0) {
            index = (arts.size() - 1) + index;
        }

        if (index >= arts.size()) {
            index = index - (arts.size() - 1);
        }

        float scale0 = 2F - ((Math.abs(di)) / 4F);
        int size = Math.round((this.width / 11F) * scale0);
        int left = cx + 1 + (di * (size + 1)) - (size / 2);
        int top = cy - (size / 2);

        Art art = arts.get(index);
        float w = 1F;
        float h = 1F;
        if (art.width() != art.height()) {
            float scale1 = 1F / Math.max(art.width(), art.height());
            w = art.width() * scale1;
            h = art.height() * scale1;
        }

        int tw = Math.round(size * w);
        int th = Math.round(size * h);
        int tl = left + ((size - tw) / 2);
        int tt = top + ((size - th) / 2);

        handleMouse(mx, my, tl, tt, tw, th, index);

        float alpha = Math.min(1F, 0.2F + Math.max(0, 1F - (Math.abs(di) / 2F)));
        GlStateManager.color4f(alpha, alpha, alpha, 1F);
        AbstractGui.blit(tl, tt, art.u(), art.v(), art.width(), art.height(), tw, th, art.textureWidth(), art.textureHeight());

//        GuiUtils.drawTexturedModalRect(tl, tt, art.u(), art.v(), art.width(), art.height(), 1F);
//        Gui.drawScaledCustomSizeModalRect(tl, tt, art.u(), art.v(), art.width(), art.height(), tw, th, art.textureWidth(), art.textureHeight());
    }

    private void drawLabel(int centerX, int centerY) {
        int index = hoveredIndex != -1 ? hoveredIndex : artIndex;
        Art art = arts.get(index);
        String text = art.getDisplayName(typeUnlocal);
        int width = font.getStringWidth(text);
        int height = (this.width / 11) + 10;
        font.drawStringWithShadow(text, centerX - (width / 2), centerY + height, 0xFFFFFF);
    }

    private void handleMouse(int mx, int my, int l, int t, int w, int h, int index) {
        if (mx >= l && mx <= l + w && my >= t && my <= t + h) {
            this.hoveredIndex = index;
        }
    }
}
