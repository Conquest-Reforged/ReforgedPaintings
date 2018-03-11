package com.conquestreforged.paintings.common.art;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public class VanillaArt implements Art {

    public static final ResourceLocation location = new ResourceLocation("textures/painting/paintings_kristoffer_zetterstrand.png");
    public static final List<Art> ALL;

    private final EntityPainting.EnumArt art;

    public VanillaArt(EntityPainting.EnumArt art) {
        this.art = art;
    }

    @Override
    public int u() {
        return art.offsetX;
    }

    @Override
    public int v() {
        return art.offsetY;
    }

    @Override
    public int width() {
        return art.sizeX;
    }

    @Override
    public int height() {
        return art.sizeY;
    }

    @Override
    public int textureWidth() {
        return 256;
    }

    @Override
    public int textureHeight() {
        return 256;
    }

    @Override
    public String getName() {
        return art.toString();
    }

    @Override
    public String getDisplayName(String parent) {
        return art.title;
    }

    @Override
    public boolean matches(Enum art) {
        return art == this.art;
    }

    static {
        EntityPainting.EnumArt[] arts = EntityPainting.EnumArt.values();
        ImmutableList.Builder<Art> builder = ImmutableList.builder();
        for (EntityPainting.EnumArt art : arts) {
            builder.add(new VanillaArt(art));
        }
        ALL = builder.build();
    }
}
