package com.conquestreforged.paintings.common;

import com.conquestreforged.paintings.PaintingsMod;
import com.conquestreforged.paintings.Proxy;
import com.conquestreforged.paintings.common.art.Art;
import com.conquestreforged.paintings.common.command.PaintCommand;
import com.conquestreforged.paintings.common.entity.PaintingEntity;
import com.conquestreforged.paintings.common.entity.PaintingType;
import com.conquestreforged.paintings.common.item.PaintingItem;
import com.conquestreforged.paintings.common.item.VanillaPaintingItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.io.IOException;

/**
 * @author dags <dags@dags.me>
 */
public abstract class CommonProxy implements Proxy {

    protected static final PaintingItem paintingItem = new PaintingItem();
    protected static final PaintingItem vanillaPainting = new VanillaPaintingItem();

    private final FMLEventChannel eventChannel;

    public CommonProxy() {
        MinecraftForge.EVENT_BUS.register(this);
        eventChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(Proxy.SYNC_CHANNEL);
        getEventChannel().register(this);
    }

    protected FMLEventChannel getEventChannel() {
        return eventChannel;
    }

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        for (int i = 0; i < 9; i++) {
            PaintingType.register("painting" + i);
        }
        registerEntity(PaintingEntity.class, "painting", 0, 128);
    }

    @Override
    public void serverStart(FMLServerStartingEvent e) {
        e.registerServerCommand(new PaintCommand());
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(paintingItem);
        event.getRegistry().register(vanillaPainting);
    }

    @SubscribeEvent
    public void syncEvent(FMLNetworkEvent.ServerCustomPacketEvent e) throws IOException {
        NetHandlerPlayServer netHandler = (NetHandlerPlayServer) e.getHandler();
        EntityPlayerMP player = netHandler.player;

        ItemStack stack = player.getHeldItemMainhand();
        if (stack.getItem() != paintingItem && stack.getItem() != vanillaPainting && stack.getItem() != Item.getByNameOrId("minecraft:painting")) {
            return;
        }

        PacketBuffer buffer = new PacketBuffer(e.getPacket().payload());
        NBTTagCompound tag = buffer.readCompoundTag();
        if (tag == null) {
            return;
        }

        String type = tag.getString(Art.TYPE_TAG);
        String art = tag.getString(Art.ART_TAG);
        if (type.isEmpty() || art.isEmpty()) {
            return;
        }

        ItemStack newStack = PaintingItem.createStack(type, art);
        player.setHeldItem(EnumHand.MAIN_HAND, newStack);
        player.updateHeldItem();
    }

    private static void registerEntity(Class<? extends Entity> entityClass, String name, int id, int range) {
        Object plugin = PaintingsMod.getInstance();
        int updateFrequency = 1;
        boolean sendsVelocity = false;
        ResourceLocation registryName = new ResourceLocation("paintings", name);
        EntityRegistry.registerModEntity(registryName, entityClass, name, id, plugin, range, updateFrequency, sendsVelocity);
    }
}
