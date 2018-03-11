package com.conquestreforged.paintings.client.render;

import com.conquestreforged.paintings.common.entity.PaintingArt;
import com.conquestreforged.paintings.common.entity.PaintingEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author dags <dags@dags.me>
 */
@SideOnly(Side.CLIENT)
public class PaintingRenderer extends Render<PaintingEntity> {

    public PaintingRenderer(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(PaintingEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.enableRescaleNormal();
        this.bindEntityTexture(entity);
        PaintingArt art = entity.getArt();
        float f = 0.0625F;
        GlStateManager.scale(f, f, f);
        this.renderPainting(entity, art.sizeX, art.sizeY, art.offsetX, art.offsetY);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(PaintingEntity entity) {
        return entity.getType().getResourceLocation();
    }

    private void renderPainting(PaintingEntity painting, int width, int height, int textureU, int textureV) {
        float xCenter = (float) (-width) / 2.0F;
        float yCenter = (float) (-height) / 2.0F;

        // 0.5F == surface height handler. lower values move paintings away from handler. higher values move the paintings inside the handler
        // Textures start to clip when set too close to the handler, 0.3 seems to be a good compromise between distance and glitch-iness
        float f2 = 0.3F;

        for (int i = 0; i < width / 16; ++i) {
            for (int j = 0; j < height / 16; ++j) {
                float xMax = xCenter + (float) ((i + 1) * 16);
                float xMin = xCenter + (float) (i * 16);
                float yMax = yCenter + (float) ((j + 1) * 16);
                float yMin = yCenter + (float) (j * 16);

                this.setLightmap(painting, (xMax + xMin) / 2.0F, (yMax + yMin) / 2.0F);

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.getBuffer();
                buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

                float txMin = (float) (textureU + width - i * 16) / 256.0F;
                float txMax = (float) (textureU + width - (i + 1) * 16) / 256.0F;
                float tyMin = (float) (textureV + height - j * 16) / 256.0F;
                float tyMax = (float) (textureV + height - (j + 1) * 16) / 256.0F;
                buffer.pos(xMax, yMin, f2).tex(txMax, tyMin).normal(0.0F, 0.0F, -1.0F).endVertex();
                buffer.pos(xMin, yMin, f2).tex(txMin, tyMin).normal(0.0F, 0.0F, -1.0F).endVertex();
                buffer.pos(xMin, yMax, f2).tex(txMin, tyMax).normal(0.0F, 0.0F, -1.0F).endVertex();
                buffer.pos(xMax, yMax, f2).tex(txMax, tyMax).normal(0.0F, 0.0F, -1.0F).endVertex();

                // reverse the texture so it appears flipped on the back side height the paintings
                float txMinReverse = txMin;
                float txMaxReverse = txMax;
                float tyMinReverse = tyMax;
                float tyMaxReverse = tyMin;
                buffer.pos(xMax, yMax, f2).tex(txMaxReverse, tyMinReverse).normal(0.0F, 0.0F, -1.0F).endVertex();
                buffer.pos(xMin, yMax, f2).tex(txMinReverse, tyMinReverse).normal(0.0F, 0.0F, -1.0F).endVertex();
                buffer.pos(xMin, yMin, f2).tex(txMinReverse, tyMaxReverse).normal(0.0F, 0.0F, -1.0F).endVertex();
                buffer.pos(xMax, yMin, f2).tex(txMaxReverse, tyMaxReverse).normal(0.0F, 0.0F, -1.0F).endVertex();

                tessellator.draw();
            }
        }
    }

    private void setLightmap(PaintingEntity painting, float p_77008_2_, float p_77008_3_) {
        int i = MathHelper.floor(painting.posX);
        int j = MathHelper.floor(painting.posY + (p_77008_3_ / 16.0F));
        int k = MathHelper.floor(painting.posZ);
        EnumFacing enumfacing = painting.facingDirection;

        if (enumfacing == EnumFacing.NORTH) {
            i = MathHelper.floor(painting.posX + (p_77008_2_ / 16.0F));
        }

        if (enumfacing == EnumFacing.WEST) {
            k = MathHelper.floor(painting.posZ - (p_77008_2_ / 16.0F));
        }

        if (enumfacing == EnumFacing.SOUTH) {
            i = MathHelper.floor(painting.posX - (p_77008_2_ / 16.0F));
        }

        if (enumfacing == EnumFacing.EAST) {
            k = MathHelper.floor(painting.posZ + (p_77008_2_ / 16.0F));
        }

        int l = this.renderManager.world.getCombinedLight(new BlockPos(i, j, k), 0);
        int i1 = l % 65536;
        int j1 = l / 65536;

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) i1, (float) j1);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
    }
}
