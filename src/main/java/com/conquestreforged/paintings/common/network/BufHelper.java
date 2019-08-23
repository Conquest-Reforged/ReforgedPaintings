package com.conquestreforged.paintings.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author dags <dags@dags.me>
 */
public class BufHelper {

    public static void writeUTF8(String text, ByteBuf buf) {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(data.length);
        buf.writeBytes(data);
    }

    public static String readUTF8(ByteBuf buf) {
        int length = buf.readInt();
        byte[] data = new byte[length];
        buf.readBytes(data);
        return new String(data, StandardCharsets.UTF_8);
    }

    public static void writeItem(ItemStack stack, PacketBuffer buffer) {
        DataOutputStream out = new DataOutputStream(new ByteArrayOutputStream());
        CompoundNBT compound = new CompoundNBT();
        stack.write(compound);
    }
}
