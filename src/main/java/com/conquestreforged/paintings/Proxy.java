package com.conquestreforged.paintings;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * @author dags <dags@dags.me>
 */
public interface Proxy {

    ResourceLocation SYNC_CHANNEL = new ResourceLocation("conquest:painting-sync");

    void handlePaintingUse(ItemStack stack, String name, String artName);
}
