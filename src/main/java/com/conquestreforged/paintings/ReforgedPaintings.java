package com.conquestreforged.paintings;

import com.conquestreforged.paintings.client.ClientProxy;
import com.conquestreforged.paintings.server.ServerProxy;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class ReforgedPaintings {

    private static final Proxy proxy = createProxy();

    public static Proxy getProxy() {
        return proxy;
    }

    private static Proxy createProxy() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            return new ClientProxy();
        }
        return new ServerProxy();
    }
}
