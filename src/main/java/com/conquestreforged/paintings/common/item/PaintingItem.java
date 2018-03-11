package com.conquestreforged.paintings.common.item;

import com.conquestreforged.paintings.PaintingsMod;
import com.conquestreforged.paintings.common.art.Art;
import com.conquestreforged.paintings.common.entity.PaintingArt;
import com.conquestreforged.paintings.common.entity.PaintingEntity;
import com.conquestreforged.paintings.common.entity.PaintingType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author dags <dags@dags.me>
 */
public class PaintingItem extends Item {

    public PaintingItem() {
        setUnlocalizedName("conquest_painting");
        setRegistryName("conquest", "painting");
    }

    public PaintingItem(String name) {
        setUnlocalizedName(name);
        setRegistryName("conquest", name);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (getClass() == PaintingItem.class) {
            if (getCreativeTab() == tab || tab == CreativeTabs.SEARCH) {
                PaintingType.getIds().distinct().sorted().forEach(name -> {
                    String type = PaintingType.fromName(name).getName();
                    ItemStack stack = createStack(type, PaintingArt.A1x1_0.shapeId);
                    items.add(stack);
                });
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        NBTTagCompound painting = stack.getTagCompound();
        if (painting == null) {
            return super.getUnlocalizedName(stack);
        }

        NBTTagCompound data = painting.getCompoundTag(Art.DATA_TAG);
        String typeName = data.getString(Art.TYPE_TAG);
        String artName = data.getString(Art.ART_TAG);

        PaintingType type = PaintingType.fromId(typeName);
        String displayName = getUnlocalizedName();

        if (type.isPresent()) {
            // mod
            displayName = type.getDisplayName();
            PaintingArt art = PaintingArt.fromName(artName);
            if (art != null) {
                displayName = displayName + " " + art.getDisplayName(type.getUnlocalizedName());
            }
        } else if (!artName.isEmpty()) {
            // vanilla
            displayName = artName;
        }

        return displayName;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote) {
            ItemStack stack = player.getHeldItemMainhand();
            String name = "";
            String artName = "";
            NBTTagCompound data = stack.getTagCompound();
            if (data != null) {
                NBTTagCompound painting = data.getCompoundTag(Art.DATA_TAG);
                name = painting.getString(Art.TYPE_TAG);
                artName = painting.getString(Art.ART_TAG);
            }
            PaintingsMod.getProxy().handlePaintingUse(stack, name, artName);
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            onItemRightClick(world, player, hand);
            return EnumActionResult.FAIL;
        }

        ItemStack stack = player.getHeldItem(hand);
        NBTTagCompound data = stack.getTagCompound();
        if (data == null) {
            return EnumActionResult.FAIL;
        }

        NBTTagCompound paint = data.getCompoundTag(Art.DATA_TAG);
        String paintType = paint.getString(Art.TYPE_TAG);
        String paintArt = paint.getString(Art.ART_TAG);
        if (paintType.isEmpty() || paintArt.isEmpty()) {
            return EnumActionResult.FAIL;
        }

        if (side != EnumFacing.DOWN && side != EnumFacing.UP) {
            pos = pos.offset(side);

            EntityHanging painting = createEntity(world, pos, side, paintType, paintArt);
            if (painting == null) {
                return EnumActionResult.FAIL;
            }

            if (!world.isRemote) {
                world.spawnEntity(painting);
                painting.playPlaceSound();
            }

            stack.shrink(1);

            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }

    protected EntityHanging createEntity(World world, BlockPos pos, EnumFacing side, String paintType, String paintArt) {
        PaintingType type = PaintingType.fromName(paintType);
        PaintingArt art = PaintingArt.fromName(paintArt);
        if (!type.isPresent() || art == null) {
            return null;
        }
        PaintingEntity painting = new PaintingEntity(world);
        painting.setType(type);
        painting.setArt(art);
        painting.place(pos, side);
        return painting;
    }

    public static ItemStack createStack(String type, String art) {
        Item item;

        if (type.equalsIgnoreCase("Vanilla")) {
            item = Item.getByNameOrId("conquest:vanilla_painting");
        } else {
            item = Item.getByNameOrId("conquest:painting");
        }

        if (item == null) {
            return ItemStack.EMPTY;
        }

        NBTTagCompound painting = new NBTTagCompound();
        painting.setString(Art.TYPE_TAG, type);
        painting.setString(Art.ART_TAG, art);

        NBTTagCompound data = new NBTTagCompound();
        data.setTag(Art.DATA_TAG, painting);

        ItemStack stack = new ItemStack(item, 1);
        stack.setTagCompound(data);

        return stack;
    }
}
