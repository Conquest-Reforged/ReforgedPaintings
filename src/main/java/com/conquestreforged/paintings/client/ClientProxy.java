package com.conquestreforged.paintings.client;

import com.conquestreforged.paintings.client.gui.GuiPainting;
import com.conquestreforged.paintings.client.render.PaintingRenderer;
import com.conquestreforged.paintings.common.CommonProxy;
import com.conquestreforged.paintings.common.art.Art;
import com.conquestreforged.paintings.common.entity.PaintingArt;
import com.conquestreforged.paintings.common.entity.PaintingEntity;
import com.conquestreforged.paintings.common.entity.PaintingType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author dags <dags@dags.me>
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        RenderingRegistry.registerEntityRenderingHandler(PaintingEntity.class, PaintingRenderer::new);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        registerModel(paintingItem, "conquest:painting");
        registerModel(vanillaPainting, "minecraft:painting");
    }

    @Override
    public void sendPacket(FMLProxyPacket packet) {
        getEventChannel().sendToServer(packet);
    }

    @Override
    public void handlePaintingUse(ItemStack stack, String name, String artName) {
        if (stack.getItem() == Item.getByNameOrId("conquest:vanilla_painting")) {
            if (artName.isEmpty()) {
                artName = EntityPainting.EnumArt.ALBAN.toString();
            }
            GuiPainting gui = new GuiPainting(stack, EntityPainting.EnumArt.valueOf(artName));
            Minecraft.getMinecraft().displayGuiScreen(gui);
        } else {
            PaintingType type = PaintingType.fromId(name);
            PaintingArt art = PaintingArt.fromName(artName);
            if (!type.isPresent() || art == null) {
                return;
            }

            GuiPainting gui = new GuiPainting(stack, type, art);
            Minecraft.getMinecraft().displayGuiScreen(gui);
        }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem e) {
        if (e.getSide() != Side.CLIENT) {
            return;
        }

        ItemStack stack = e.getItemStack();
        String artName = "";

        NBTTagCompound data = stack.getTagCompound();
        if (data != null) {
            NBTTagCompound painting = data.getCompoundTag(Art.DATA_TAG);
            artName = painting.getString(Art.ART_TAG);
        }

        if (stack.getItem() == Item.getByNameOrId("minecraft:painting")) {
            if (artName.isEmpty()) {
                artName = EntityPainting.EnumArt.ALBAN.toString();
            }
            GuiPainting gui = new GuiPainting(stack, EntityPainting.EnumArt.valueOf(artName));
            Minecraft.getMinecraft().displayGuiScreen(gui);
        }
    }

    private static void registerModel(Item item, String id) {
        ModelResourceLocation modelLocation = new ModelResourceLocation(id, "inventory");
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, modelLocation);
    }
}
