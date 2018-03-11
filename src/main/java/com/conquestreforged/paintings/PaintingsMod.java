package com.conquestreforged.paintings;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * @author dags <dags@dags.me>
 */
@Mod(modid = "paintings")
public class PaintingsMod {

    @Mod.Instance
    private static PaintingsMod instance;

    @SidedProxy(clientSide = "com.conquestreforged.paintings.client.ClientProxy", serverSide = "com.conquestreforged.paintings.server.ServerProxy")
    private static Proxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        getProxy().preInit(e);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        getProxy().init(e);
    }

    @Mod.EventHandler
    public void server(FMLServerStartingEvent e) {
        getProxy().serverStart(e);
    }

    public static PaintingsMod getInstance() {
        return instance;
    }

    public static Proxy getProxy() {
        return proxy;
    }
}
