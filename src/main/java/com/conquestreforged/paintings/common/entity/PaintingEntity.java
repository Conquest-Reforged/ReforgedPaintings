package com.conquestreforged.paintings.common.entity;

import com.conquestreforged.paintings.common.art.Art;
import com.conquestreforged.paintings.common.item.PaintingItem;
import com.conquestreforged.paintings.common.network.BufHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

/**
 * @author dags <dags@dags.me>
 */
public class PaintingEntity extends HangingEntity implements IEntityAdditionalSpawnData {

    public static final EntityType<PaintingEntity> TYPE = EntityType.Builder.<PaintingEntity>create(PaintingEntity::new, EntityClassification.MISC)
            .size(0.5F, 0.5F)
            .build("conquest:painting");

    private PaintingVariant type;
    private PaintingArt art = PaintingArt.A1x1_0;

    public PaintingEntity(EntityType<PaintingEntity> type, World world) {
        super(type, world);
    }

    public PaintingEntity(EntityType<PaintingEntity> type, World world, BlockPos pos) {
        super(type, world, pos);
    }

    public PaintingVariant getPaintingType() {
        return type;
    }

    public PaintingArt getArt() {
        return art;
    }

    public void place(BlockPos pos, Direction side) {
        hangingPosition = pos;
        updateFacingWithBoundingBox(side);
    }

    public void setType(PaintingVariant type) {
        this.type = type;
        if (facingDirection != null) {
            updateFacingWithBoundingBox(facingDirection);
        }
    }

    public void setArt(PaintingArt art) {
        this.art = art;
        if (facingDirection != null) {
            updateFacingWithBoundingBox(facingDirection);
        }
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return PaintingItem.createStack(type.getName(), art.getName());
    }

    @Override
    public void onBroken(@Nullable Entity brokenEntity) {
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0F, 1.0F);

            if (brokenEntity instanceof PlayerEntity) {
                PlayerEntity entityplayer = (PlayerEntity) brokenEntity;

                if (entityplayer.abilities.isCreativeMode) {
                    return;
                }
            }

            ItemStack drop = PaintingItem.createStack(getPaintingType().getName(), getArt().getName());
            if (drop != ItemStack.EMPTY) {
                this.entityDropItem(drop, 0.0F);
            }
        }
    }

    @Override
    public boolean onValidSurface() {
        return true;
    }

    @Override
    public int getWidthPixels() {
        return this.art.sizeX;
    }

    @Override
    public int getHeightPixels() {
        return this.art.sizeY;
    }

    @Override
    public void playPlaceSound() {
        this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0F, 1.0F);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString(Art.TYPE_TAG, this.type.getName());
        compound.putInt(Art.ART_TAG, this.art.index());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        String type = compound.getString(Art.TYPE_TAG);
        int id = compound.getInt(Art.ART_TAG);
        this.type = PaintingVariant.fromId(type);
        this.art = PaintingArt.fromId(id);
        super.readAdditional(compound);
    }

    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float a, float b, int c, boolean d) {
        BlockPos pos = this.hangingPosition.add(x - this.posX, y - this.posY, z - this.posZ);
        this.setPosition((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeInt(hangingPosition.getX());
        buffer.writeInt(hangingPosition.getY());
        buffer.writeInt(hangingPosition.getZ());
        buffer.writeInt(getHorizontalFacing().getIndex());
        BufHelper.writeUTF8(getPaintingType().getName(), buffer);
        BufHelper.writeUTF8(getArt().shapeId, buffer);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        int x = additionalData.readInt();
        int y = additionalData.readInt();
        int z = additionalData.readInt();
        int facing = additionalData.readInt();
        String type = BufHelper.readUTF8(additionalData);
        String art = BufHelper.readUTF8(additionalData);
        this.type = PaintingVariant.fromId(type);
        this.art = PaintingArt.fromName(art);
        this.setPosition(x, y, z);
        this.updateFacingWithBoundingBox(Direction.byHorizontalIndex(facing));
    }
}
