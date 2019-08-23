package com.conquestreforged.paintings.common;

import com.conquestreforged.paintings.Protocols;
import com.conquestreforged.paintings.Proxy;
import com.conquestreforged.paintings.common.art.Art;
import com.conquestreforged.paintings.common.entity.PaintingEntity;
import com.conquestreforged.paintings.common.entity.PaintingVariant;
import com.conquestreforged.paintings.common.item.PaintingItem;
import com.conquestreforged.paintings.common.item.VanillaPaintingItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;

/**
 * @author dags <dags@dags.me>
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public abstract class CommonProxy implements Proxy {

    protected static final PaintingItem paintingItem = new PaintingItem();
    protected static final PaintingItem vanillaPainting = new VanillaPaintingItem();
    protected final EventNetworkChannel eventChannel;

    public CommonProxy() {
        eventChannel = NetworkRegistry.newEventChannel(Proxy.SYNC_CHANNEL, Protocols.VERSION, Protocols.CLIENT, Protocols.SERVER);
        eventChannel.registerObject(this);
    }

    @SubscribeEvent
    public static void registerEntities(FMLCommonSetupEvent event) {
        for (int i = 0; i < 9; i++) {
            PaintingVariant.register("painting" + i);
        }
        ForgeRegistries.ENTITIES.register(PaintingEntity.TYPE);
    }

    @SubscribeEvent
    public static void registerCommands(FMLServerStartingEvent event) {

    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(paintingItem);
        event.getRegistry().register(vanillaPainting);
    }

    @SubscribeEvent
    public static void syncEvent(NetworkEvent.ServerCustomPayloadEvent e) throws IOException {
        ServerPlayerEntity player = e.getSource().get().getSender();
        if (player == null) {
            return;
        }

        ItemStack stack = player.getHeldItemMainhand();
        if (stack.getItem() != paintingItem && stack.getItem() != vanillaPainting && stack.getItem() != Items.PAINTING) {
            return;
        }

        PacketBuffer buffer = e.getPayload();
        CompoundNBT tag = buffer.readCompoundTag();
        if (tag == null) {
            return;
        }

        String type = tag.getString(Art.TYPE_TAG);
        String art = tag.getString(Art.ART_TAG);
        if (type.isEmpty() || art.isEmpty()) {
            return;
        }

        ItemStack newStack = PaintingItem.createStack(type, art);
        player.setHeldItem(Hand.MAIN_HAND, newStack);
        player.updateHeldItem();
    }
}
