package com.conquestreforged.paintings.common.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

/**
 * @author dags <dags@dags.me>
 */
public class VanillaPaintingItem extends PaintingItem {

    public VanillaPaintingItem() {
        super("vanilla_painting");
    }

    @Override
    protected HangingEntity createEntity(World world, BlockPos pos, Direction side, String paintType, String paintArt) {
        return new PaintingEntity(EntityType.PAINTING, world) {{
            hangingPosition = pos;
            facingDirection = side;
            art = Registry.MOTIVE.getOrDefault(new ResourceLocation(paintArt));
            updateFacingWithBoundingBox(side);
        }};
    }
}
