package dev.zprestige.fire.util.impl;

import dev.zprestige.fire.Main;
import dev.zprestige.fire.util.Utils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil implements Utils {
    protected static final Tessellator tessellator = Tessellator.getInstance();
    protected static final BufferBuilder bufferbuilder = tessellator.getBuffer();

    public static void renderEntity(final Entity entity, final float partialTicks) {
        if (mc.player != null && mc.world != null && entity != null) {
            if (entity.ticksExisted == 0) {
                entity.lastTickPosX = entity.posX;
                entity.lastTickPosY = entity.posY;
                entity.lastTickPosZ = entity.posZ;
            }
            final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
            final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
            final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
            final float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
            final float l = 65536.0f;
            final int i = entity.getBrightnessForRender();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i % l, i / l);
            try {
                mc.getRenderManager().renderEntity(entity, x - mc.getRenderManager().viewerPosX, y - mc.getRenderManager().viewerPosY, z - mc.getRenderManager().viewerPosZ, yaw, partialTicks, false);
            } catch (Exception ignored) {
            }
        }
    }

    public static void drawPickerBase(int pickerX, int pickerY, int pickerWidth, int pickerHeight, float red, float green, float blue, float alpha) {
        GL11.glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glShadeModel(GL_SMOOTH);
        glBegin(GL_POLYGON);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glVertex2f(pickerX, pickerY);
        glVertex2f(pickerX, pickerY + pickerHeight);
        glColor4f(red, green, blue, alpha);
        glVertex2f(pickerX + pickerWidth, pickerY + pickerHeight);
        glVertex2f(pickerX + pickerWidth, pickerY);
        glEnd();
        glDisable(GL_ALPHA_TEST);
        glBegin(GL_POLYGON);
        glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        glVertex2f(pickerX, pickerY);
        glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        glVertex2f(pickerX, pickerY + pickerHeight);
        glVertex2f(pickerX + pickerWidth, pickerY + pickerHeight);
        glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        glVertex2f(pickerX + pickerWidth, pickerY);
        glEnd();
        glEnable(GL_ALPHA_TEST);
        glShadeModel(GL_FLAT);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        GL11.glPopMatrix();
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, 0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(left, top, 0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(left, bottom, 0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos(right, bottom, 0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawRoundedRect(double x, double y, double width, double height, final double radius, final Color color) {
        glPushAttrib(GL_POINTS);
        glScaled(0.5, 0.5, 0.5);
        {
            x *= 2;
            y *= 2;
            width *= 2;
            height *= 2;
            glEnable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
            glEnable(GL_LINE_SMOOTH);
            glBegin(GL_POLYGON);
            final double pi = Math.PI;
            int i;
            for (i = 0; i <= 90; i++) {
                glVertex2d(x + radius + Math.sin(i * pi / 180.0D) * radius * -1.0D, y + radius + Math.cos(i * pi / 180.0D) * radius * -1.0D);
            }
            for (i = 90; i <= 180; i++) {
                glVertex2d(x + radius + Math.sin(i * pi / 180.0D) * radius * -1.0D, height - radius + Math.cos(i * pi / 180.0D) * radius * -1.0D);
            }
            for (i = 0; i <= 90; i++) {
                glVertex2d(width - radius + Math.sin(i * pi / 180.0D) * radius, height - radius + Math.cos(i * pi / 180.0D) * radius);
            }
            for (i = 90; i <= 180; i++) {
                glVertex2d(width - radius + Math.sin(i * pi / 180.0D) * radius, y + radius + Math.cos(i * pi / 180.0D) * radius);
            }
            glEnd();
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            glDisable(GL_LINE_SMOOTH);
            glDisable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
        }

        glScaled(2, 2, 2);
        glPopAttrib();
    }

    public static void drawCustomBB(Color color, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        final AxisAlignedBB bb1 = new AxisAlignedBB(minX - mc.getRenderManager().viewerPosX, minY - mc.getRenderManager().viewerPosY, minZ - mc.getRenderManager().viewerPosZ, maxX - mc.getRenderManager().viewerPosX, maxY - mc.getRenderManager().viewerPosY, maxZ - mc.getRenderManager().viewerPosZ);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        RenderGlobal.renderFilledBox(bb1, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }


    public static void drawBoxWithHeight(AxisAlignedBB bb, Color color, float height) {
        final AxisAlignedBB bb1 = new AxisAlignedBB(bb.minX - mc.getRenderManager().viewerPosX, bb.minY - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ, bb.maxX - mc.getRenderManager().viewerPosX, bb.maxY - 1 + height - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        RenderGlobal.renderFilledBox(bb1, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static double interpolateLastTickPos(double pos, double lastPos) {
        return lastPos + (pos - lastPos) * mc.timer.renderPartialTicks;
    }

    public static Vec3d interpolateEntity(Entity entity) {
        double x;
        double y;
        double z;
        x = interpolateLastTickPos(entity.posX, entity.lastTickPosX) - mc.getRenderManager().renderPosX;
        y = interpolateLastTickPos(entity.posY, entity.lastTickPosY) - mc.getRenderManager().renderPosY;
        z = interpolateLastTickPos(entity.posZ, entity.lastTickPosZ) - mc.getRenderManager().renderPosZ;
        return new Vec3d(x, y, z);
    }

    public static void draw3DText(final String text, double x, double y, double z, double scale, float red, float green, float blue, float alpha) {
        RenderUtil.drawNametag(text, x, y, z, scale, new Color(red / 255.0f, green / 255.0f, blue / 255.0f, alpha / 255.0f).getRGB());
    }

    public static void prepare3D(final double x, final double y, final double z, final double scale) {
        double dist = ((mc.getRenderViewEntity() == null) ? mc.player : mc.getRenderViewEntity()).getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
        double scaling = 0.0018 + scale * dist;
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate(x, y + 0.4000000059604645, z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, (mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scaling, -scaling, scaling);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
    }

    public static void release3D() {
        glColor4f(1f, 1f, 1f, 1f);
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
        GlStateManager.popMatrix();
    }

    public static void drawNametag(final String text, final double x, final double y, final double z, final double scale, final int color) {
        int textWidth = (int) (Main.fontManager.getStringWidth(text) / 2);
        prepare3D(x, y, z, scale);
        Main.fontManager.drawStringWithShadow(text, -textWidth, -(mc.fontRenderer.FONT_HEIGHT - 1), color);
        release3D();
    }

    public static void drawBBBoxWithHeight(final AxisAlignedBB BB, final Color color, final float height) {
        final AxisAlignedBB bb = new AxisAlignedBB(BB.minX - mc.getRenderManager().viewerPosX, BB.minY - mc.getRenderManager().viewerPosY, BB.minZ - mc.getRenderManager().viewerPosZ, BB.maxX - mc.getRenderManager().viewerPosX, BB.maxY - mc.getRenderManager().viewerPosY - 1 + height, BB.maxZ - mc.getRenderManager().viewerPosZ);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        RenderGlobal.renderFilledBox(bb, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBlockOutlineBBWithHeight(AxisAlignedBB bb, Color color, float lineWidth, float height) {
        RenderUtil.drawBlockOutlineWithHeight(bb.grow(0.002f).offset(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ), color, lineWidth, height);
    }

    public static void drawBlockOutlineWithHeight(final AxisAlignedBB bb, final Color color, final float lineWidth, final float height) {
        float red = (float) color.getRed() / 255f;
        float green = (float) color.getGreen() / 255f;
        float blue = (float) color.getBlue() / 255f;
        float alpha = (float) color.getAlpha() / 255f;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(lineWidth);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY - 1 + height, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY - 1 + height, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY - 1 + height, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY - 1 + height, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY - 1 + height, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY - 1 + height, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY - 1 + height, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY - 1 + height, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }


    public static void drawCheckMark(float x, float y, int width, int color) {
        float f = (color >> 24 & 255) / 255.0f;
        float f1 = (color >> 16 & 255) / 255.0f;
        float f2 = (color >> 8 & 255) / 255.0f;
        float f3 = (color & 255) / 255.0f;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(3f);
        GL11.glBegin(3);
        GL11.glColor4f(0, 0, 0, 1.f);
        GL11.glVertex2d(x + width - 6.25, y + 2.75f);
        GL11.glVertex2d(x + width - 11.5, y + 10.25f);
        GL11.glVertex2d(x + width - 13.75f, y + 7.75f);
        GL11.glEnd();
        GL11.glLineWidth(1.5f);
        GL11.glBegin(3);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(x + width - 6.5, y + 3);
        GL11.glVertex2d(x + width - 11.5, y + 10);
        GL11.glVertex2d(x + width - 13.5, y + 8);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void prepareScale(double scale) {
        GL11.glPushMatrix();
        GL11.glScaled(scale, scale, scale);
    }

    public static void releaseScale() {
        GL11.glPopMatrix();
    }

    public static void drawArrow(float x, float y, boolean left) {
        GL11.glPushMatrix();
        GL11.glScaled(1.3, 1.3, 1.3);
        y -= 1.5f;
        x += 2;
        x /= 1.3;
        y /= 1.3;
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(1);
        GL11.glColor4f(255, 255, 255, 255);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x + (left ? -4 : 4), y + 3);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(x + (left ? -4 : 4), y + 3);
        GL11.glVertex2d(x, y + 6);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glPopMatrix();

    }

    public static void drawOutline(float x, float y, float width, float height, Color color, float lineWidth) {
        if (x < width) {
            double i = x;
            x = width;
            width = (float) i;
        }
        if (y < height) {
            double j = y;
            y = height;
            height = (float) j;
        }
        float f3 = (color.getRGB() >> 24 & 255) / 255.0f;
        float f = (color.getRGB() >> 16 & 255) / 255.0f;
        float f1 = (color.getRGB() >> 8 & 255) / 255.0f;
        float f2 = (color.getRGB() & 255) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        GL11.glLineWidth(lineWidth);
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(x, height, 0.0D).endVertex();
        bufferbuilder.pos(width, height, 0.0D).endVertex();
        bufferbuilder.pos(width, y, 0.0D).endVertex();
        bufferbuilder.pos(x, y, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    }

    public static void prepareScissor(int x, int y, int width, int height) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
        scissor(x, y, x + width, y + height);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    public static void releaseScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    public static void scissor(int x, int y, int x2, int y2) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        GL11.glScissor(x * scaledResolution.getScaleFactor(), (scaledResolution.getScaledHeight() - y2) * scaledResolution.getScaleFactor(), (x2 - x) * scaledResolution.getScaleFactor(), (y2 - y) * scaledResolution.getScaleFactor());
    }

    public static void drawRect(float x, float y, float width, float height, int color) {
        float alpha = (float) (color >> 24 & 0xFF) / 255.0f;
        float red = (float) (color >> 16 & 0xFF) / 255.0f;
        float green = (float) (color >> 8 & 0xFF) / 255.0f;
        float blue = (float) (color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, height, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(width, height, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(width, y, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void image(ResourceLocation resourceLocation, int x, int y, int width, int height, Color color) {
        GlStateManager.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        mc.getTextureManager().bindTexture(resourceLocation);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
    }

    public static void image(ResourceLocation resourceLocation, int x, int y, int width, int height) {
        if (mc == null) {
            return;
        }
        mc.getTextureManager().bindTexture(resourceLocation);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
    }

    public static AxisAlignedBB getViewerPos(final BlockPos pos) {
        return new AxisAlignedBB(pos.getX() - mc.getRenderManager().viewerPosX, pos.getY() - mc.getRenderManager().viewerPosY, pos.getZ() - mc.getRenderManager().viewerPosZ, pos.getX() + 1 - mc.getRenderManager().viewerPosX, pos.getY() + 1 - mc.getRenderManager().viewerPosY, pos.getZ() + 1 - mc.getRenderManager().viewerPosZ);
    }

    public static Vec3d interpolateEntity(final Entity entity, final float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time);
    }

    public static AxisAlignedBB getSelectedBoundingBox(final BlockPos pos) {
        return mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos);
    }

    public static AxisAlignedBB getInterpolatedAxisAlignedBB(final BlockPos pos) {
        final Vec3d interpolatedEntity = interpolateEntity(Objects.requireNonNull(mc.renderViewEntity), mc.getRenderPartialTicks());
        return getSelectedBoundingBox(pos).offset(-interpolatedEntity.x, -interpolatedEntity.y, -interpolatedEntity.z);
    }

    public static void drawOutline(final BlockPos pos, final Color color, final float lineWidth) {
        final AxisAlignedBB bb = getInterpolatedAxisAlignedBB(pos);
        final float red = color.getRed() > 1.0 ? color.getRed() / 255.0f : color.getRed();
        final float green = color.getGreen() > 1.0 ? color.getGreen() / 255.0f : color.getGreen();
        final float blue = color.getBlue() > 1.0 ? color.getBlue() / 255.0f : color.getBlue();
        final float alpha = color.getAlpha() > 1.0 ? color.getAlpha() / 255.0f : color.getAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(lineWidth);
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawBox(final BlockPos pos, final Color color) {
        final float red = color.getRed() > 1.0 ? color.getRed() / 255.0f : color.getRed();
        final float green = color.getGreen() > 1.0 ? color.getGreen() / 255.0f : color.getGreen();
        final float blue = color.getBlue() > 1.0 ? color.getBlue() / 255.0f : color.getBlue();
        final float alpha = color.getAlpha() > 1.0 ? color.getAlpha() / 255.0f : color.getAlpha();
        final AxisAlignedBB bb = getViewerPos(pos);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        RenderGlobal.renderFilledBox(bb, red, green, blue, alpha);
        GL11.glDisable(2848);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
