package com.conquestreforged.paintings.common.entity;

import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * @author dags <dags@dags.me>
 */
public class PaintingType implements Translateable {

    private static final PaintingType UNKNOWN = new PaintingType("unknown");
    private static final Map<String, PaintingType> types = new ConcurrentHashMap<>();

    private final String name;
    private final ResourceLocation location;

    private PaintingType(String name) {
        this.name = name;
        this.location = new ResourceLocation("conquest", "textures/paintings/" + name + ".png");
    }

    public boolean isPresent() {
        return this != UNKNOWN;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUnlocalizedName() {
        return getUnlocalizedName("conquest");
    }

    @Override
    public String getUnlocalizedName(String parent) {
        return parent + "." + getName();
    }

    public ResourceLocation getResourceLocation() {
        return location;
    }

    public static PaintingType fromId(String id) {
        if (id == null) {
            return UNKNOWN;
        }
        return types.getOrDefault(id, UNKNOWN);
    }

    public static PaintingType fromName(String name) {
        PaintingType type = fromId(name);
        if (type != UNKNOWN) { return type;
        }

        for (PaintingType t : types.values()) {
            if (name.equalsIgnoreCase(t.getName())) {
                return t;
            }
            if (name.equalsIgnoreCase(t.getDisplayName())) {
                return t;
            }
        }

        return UNKNOWN;
    }

    public static void register(String id) {
        types.put(id, new PaintingType(id));
    }

    public static Stream<String> getIds() {
        return types.values().stream().map(PaintingType::getName);
    }
}
