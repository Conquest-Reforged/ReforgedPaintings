package com.conquestreforged.paintings.client;

import com.conquestreforged.core.util.RegUtil;
import com.conquestreforged.paintings.client.gui.GuiPainting;
import com.conquestreforged.paintings.client.render.PaintingRenderer;
import com.conquestreforged.paintings.common.CommonProxy;
import com.conquestreforged.paintings.common.art.Art;
import com.conquestreforged.paintings.common.entity.PaintingArt;
import com.conquestreforged.paintings.common.entity.PaintingEntity;
import com.conquestreforged.paintings.common.entity.PaintingVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author dags <dags@dags.me>
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientProxy extends CommonProxy {

    @Override
    public void handlePaintingUse(ItemStack stack, String name, String artName) {
        if (stack.getItem() == ForgeRegistries.ITEMS.getValue(new ResourceLocation("conquest:vanilla_painting"))) {
            if (artName.isEmpty()) {
                artName = PaintingType.ALBAN.toString();
            }
            GuiPainting gui = new GuiPainting(stack, RegUtil.art(artName));
            Minecraft.getInstance().displayGuiScreen(gui);
        } else {
            PaintingVariant type = PaintingVariant.fromId(name);
            PaintingArt art = PaintingArt.fromName(artName);
            if (!type.isPresent() || art == null) {
                return;
            }

            GuiPainting gui = new GuiPainting(stack, type, art);
            Minecraft.getInstance().displayGuiScreen(gui);
        }
    }

    @SubscribeEvent
    public static void registerRenders(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(PaintingEntity.class, PaintingRenderer::new);
        registerModel(paintingItem, "conquest:painting");
        registerModel(vanillaPainting, "minecraft:painting");
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem e) {
        if (e.getSide() != LogicalSide.CLIENT) {
            return;
        }

        ItemStack stack = e.getItemStack();
        String artName = "";

        CompoundNBT data = stack.getTag();
        if (data != null) {
            CompoundNBT painting = data.getCompound(Art.DATA_TAG);
            artName = painting.getString(Art.ART_TAG);
        }

        if (stack.getItem() == Items.PAINTING) {
            if (artName.isEmpty()) {
                artName = PaintingType.ALBAN.toString();
            }
            GuiPainting gui = new GuiPainting(stack, Registry.MOTIVE.getOrDefault(new ResourceLocation(artName)));
            Minecraft.getInstance().displayGuiScreen(gui);
        }
    }

    private static void registerModel(Item item, String id) {
        ModelResourceLocation modelLocation = new ModelResourceLocation(id, "inventory");
        Minecraft.getInstance().getItemRenderer().getItemModelMesher().register(item, modelLocation);
    }
}
