package com.conquestreforged.paintings.server;

import com.conquestreforged.paintings.common.CommonProxy;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

/**
 * @author dags <dags@dags.me>
 */
public class ServerProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent e) {

    }

    @Override
    public void handlePaintingUse(ItemStack stack, String name, String artName) {

    }

    @Override
    public void sendPacket(FMLProxyPacket packet) {

    }
}
