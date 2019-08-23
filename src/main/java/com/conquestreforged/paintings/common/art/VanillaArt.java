package com.conquestreforged.paintings.common.art;

import net.minecraft.entity.item.PaintingType;
import net.minecraft.util.ResourceLocation;

import java.util.LinkedList;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public class VanillaArt implements Art {

    public static final ResourceLocation location = new ResourceLocation("textures/painting/paintings_kristoffer_zetterstrand.png");
    public static final List<Art> ALL = new LinkedList<>();

    private final PaintingType art;

    public VanillaArt(PaintingType art) {
        this.art = art;
    }

    @Override
    public int u() {
        return 0;
    }

    @Override
    public int v() {
        return 0;
    }

    @Override
    public int width() {
        return art.getWidth();
    }

    @Override
    public int height() {
        return art.getHeight();
    }

    @Override
    public int textureWidth() {
        return art.getWidth();
    }

    @Override
    public int textureHeight() {
        return art.getHeight();
    }

    @Override
    public String getName() {
        return art.toString();
    }

    @Override
    public String getDisplayName(String parent) {
        return art.toString();
    }

    @Override
    public boolean matches(Object art) {
        return art == this.art;
    }

    static {

    }
}
