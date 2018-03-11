package com.conquestreforged.paintings.common.entity;

import com.conquestreforged.paintings.common.art.Art;
import com.conquestreforged.paintings.common.item.PaintingItem;
import com.conquestreforged.paintings.common.network.BufHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * @author dags <dags@dags.me>
 */
public class PaintingEntity extends EntityHanging implements IEntityAdditionalSpawnData {

    private PaintingType type;
    private PaintingArt art = PaintingArt.A1x1_0;

    public PaintingEntity(World worldIn) {
        super(worldIn);
    }

    public PaintingType getType() {
        return type;
    }

    public PaintingArt getArt() {
        return art;
    }

    public void place(BlockPos pos, EnumFacing side) {
        hangingPosition = pos;
        updateFacingWithBoundingBox(side);
    }

    public void setType(PaintingType type) {
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
        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0F, 1.0F);

            if (brokenEntity instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) brokenEntity;

                if (entityplayer.capabilities.isCreativeMode) {
                    return;
                }
            }

            ItemStack drop = PaintingItem.createStack(getType().getName(), getArt().getName());
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
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setString(Art.TYPE_TAG, this.type.getName());
        tagCompound.setInteger(Art.ART_TAG, this.art.index());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        String type = tagCompound.getString(Art.TYPE_TAG);
        int id = tagCompound.getInteger(Art.ART_TAG);
        this.type = PaintingType.fromId(type);
        this.art = PaintingArt.fromId(id);
        super.readEntityFromNBT(tagCompound);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(hangingPosition.getX());
        buffer.writeInt(hangingPosition.getY());
        buffer.writeInt(hangingPosition.getZ());
        buffer.writeInt(getHorizontalFacing().getIndex());
        BufHelper.writeUTF8(getType().getName(), buffer);
        BufHelper.writeUTF8(getArt().shapeId, buffer);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        int x = additionalData.readInt();
        int y = additionalData.readInt();
        int z = additionalData.readInt();
        int facing = additionalData.readInt();
        String type = BufHelper.readUTF8(additionalData);
        String art = BufHelper.readUTF8(additionalData);
        this.type = PaintingType.fromId(type);
        this.art = PaintingArt.fromName(art);
        this.setPosition(x, y, z);
        this.updateFacingWithBoundingBox(EnumFacing.getFront(facing));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float a, float b, int c, boolean d) {
        BlockPos pos = this.hangingPosition.add(x - this.posX, y - this.posY, z - this.posZ);
        this.setPosition((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
    }
}
