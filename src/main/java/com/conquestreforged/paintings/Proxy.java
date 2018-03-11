package com.conquestreforged.paintings;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

/**
 * @author dags <dags@dags.me>
 */
public interface Proxy {

    String SYNC_CHANNEL = "painting-sync";

    void preInit(FMLPreInitializationEvent e);

    void init(FMLInitializationEvent e);

    void serverStart(FMLServerStartingEvent e);

    void handlePaintingUse(ItemStack stack, String name, String artName);

    void sendPacket(FMLProxyPacket packet);
}
