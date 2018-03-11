package com.conquestreforged.paintings.client.gui;

import com.conquestreforged.paintings.PaintingsMod;
import com.conquestreforged.paintings.Proxy;
import com.conquestreforged.paintings.common.art.Art;
import com.conquestreforged.paintings.common.art.ModArt;
import com.conquestreforged.paintings.common.art.VanillaArt;
import com.conquestreforged.paintings.common.entity.PaintingArt;
import com.conquestreforged.paintings.common.entity.PaintingType;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
@SideOnly(Side.CLIENT)
public class GuiPainting extends GuiScreen {

    private final List<Art> arts;
    private final ItemStack stack;
    private final ResourceLocation texture;
    private final String type;
    private final String typeUnlocal;

    private int artIndex;
    private int hoveredIndex = -1;

    public GuiPainting(ItemStack stack, PaintingType type, PaintingArt art) {
        this.stack = stack;
        this.type = type.getName();
        this.typeUnlocal = type.getUnlocalizedName();
        this.texture = type.getResourceLocation();
        this.arts = ModArt.ALL;
        this.artIndex = Art.indexOf(art, ModArt.ALL);
        this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
    }

    public GuiPainting(ItemStack stack, EntityPainting.EnumArt art) {
        this.stack = stack;
        this.type = "Vanilla";
        this.typeUnlocal = "";
        this.arts = VanillaArt.ALL;
        this.texture = VanillaArt.location;
        this.artIndex = Art.indexOf(art, VanillaArt.ALL);
        this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        Minecraft.getMinecraft().displayGuiScreen(null);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        Minecraft.getMinecraft().displayGuiScreen(null);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int centerX = width / 2;
        int centerY = height / 2;
        hoveredIndex = -1;

        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

        for (int i = 5; i >= 0; i--) {
            if (i > 0) {
                drawArt(mouseX, mouseY, centerX, centerY, -i);
            }
            drawArt(mouseX, mouseY, centerX, centerY, +i);
        }

        drawLabel(centerX, centerY);
    }

    @Override
    public void onGuiClosed() {
        int artIndex = hoveredIndex == -1 ? this.artIndex : hoveredIndex;
        String art = arts.get(artIndex).getName();

        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(Art.TYPE_TAG, type);
        tag.setString(Art.ART_TAG, art);

        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeCompoundTag(tag);

        FMLProxyPacket packet = new FMLProxyPacket(buffer, Proxy.SYNC_CHANNEL);
        PaintingsMod.getProxy().sendPacket(packet);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int i = Mouse.getDWheel();
        if (i > 0) {
            artIndex = artIndex - 1;
            if (artIndex < 0) {
                artIndex = arts.size() - 1;
            }
        }

        if (i < 0) {
            artIndex = artIndex + 1;
            if (artIndex >= arts.size()) {
                artIndex = 0;
            }
        }
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
        int size = Math.round((this.width / 11) * scale0);
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
        GlStateManager.color(alpha, alpha, alpha, 1F);
        Gui.drawScaledCustomSizeModalRect(tl, tt, art.u(), art.v(), art.width(), art.height(), tw, th, art.textureWidth(), art.textureHeight());
    }

    private void drawLabel(int centerX, int centerY) {
        int index = hoveredIndex != -1 ? hoveredIndex : artIndex;
        Art art = arts.get(index);
        String text = art.getDisplayName(typeUnlocal);
        int width = fontRenderer.getStringWidth(text);
        int height = (this.width / 11) + 10;
        fontRenderer.drawStringWithShadow(text, centerX - (width / 2), centerY + height, 0xFFFFFF);
    }

    private void handleMouse(int mx, int my, int l, int t, int w, int h, int index) {
        if (mx >= l && mx <= l + w && my >= t && my <= t + h) {
            this.hoveredIndex = index;
        }
    }
}
