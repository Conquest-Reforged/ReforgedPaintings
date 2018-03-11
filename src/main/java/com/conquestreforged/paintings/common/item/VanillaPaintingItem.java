package com.conquestreforged.paintings.common.item;

import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author dags <dags@dags.me>
 */
public class VanillaPaintingItem extends PaintingItem {

    public VanillaPaintingItem() {
        super("vanilla_painting");
    }

    @Override
    protected EntityHanging createEntity(World world, BlockPos pos, EnumFacing side, String paintType, String paintArt) {
        return new EntityPainting(world) {{
            hangingPosition = pos;
            facingDirection = side;
            art = EnumArt.valueOf(paintArt);
            updateFacingWithBoundingBox(side);
        }};
    }
}
