package dev.zprestige.fire.module.visual.holeesp;

import dev.zprestige.fire.Main;
import dev.zprestige.fire.event.bus.EventListener;
import dev.zprestige.fire.manager.holemanager.HoleManager;
import dev.zprestige.fire.module.Descriptor;
import dev.zprestige.fire.module.Module;
import dev.zprestige.fire.settings.impl.ColorBox;
import dev.zprestige.fire.settings.impl.ComboBox;
import dev.zprestige.fire.settings.impl.Slider;
import dev.zprestige.fire.settings.impl.Switch;
import dev.zprestige.fire.util.impl.AnimationUtil;
import dev.zprestige.fire.util.impl.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;

@Descriptor(description = "Draws holes you can walk into and not take crystal damage in")
public class HoleESP extends Module {
    public final ComboBox animation = Menu.ComboBox("Animation", "None", new String[]{
            "None",
            "Fade",
            "Grow",
            "Pop"
    });
    protected final Slider fadeAmount = Menu.Slider("Fade Amount", 30.0f, 0.1f, 50.0f).visibility(z -> animation.GetCombo().equals("Fade"));
    public final Slider animationSpeed = Menu.Slider("Animation Speed", 1.0f, 1.0f, 10.0f).visibility(z -> animation.GetCombo().equals("Grow") || animation.GetCombo().equals("Pop"));
    public final Switch fadePop = Menu.Switch("Fade Pop", false).visibility(z -> animation.GetCombo().equals("Pop"));
    public final Slider startY = Menu.Slider("Start Y", 1.0f, 0.1f, 10.0f).visibility(z -> animation.GetCombo().equals("Pop"));
    public final Slider height = Menu.Slider("Height", 1.0f, 0.0f, 2.0f);
    public final Switch bedrockBox = Menu.Switch("Bedrock Box", false);
    public final ColorBox bedrockBoxColor = Menu.Color("Bedrock Box Color", Color.GREEN).visibility(z -> bedrockBox.GetSwitch());
    public final Switch bedrockOutline = Menu.Switch("Bedrock Outline", false);
    public final ColorBox bedrockOutlineColor = Menu.Color("Bedrock Outline Color", Color.GREEN).visibility(z -> bedrockOutline.GetSwitch());
    public final Switch obsidianBox = Menu.Switch("Obsidian Box", false);
    public final ColorBox obsidianBoxColor = Menu.Color("Obsidian Box Color", Color.RED).visibility(z -> obsidianBox.GetSwitch());
    public final Switch obsidianOutline = Menu.Switch("Obsidian Outline", false);
    public final ColorBox obsidianOutlineColor = Menu.Color("Obsidian Outline Color", Color.RED).visibility(z -> obsidianOutline.GetSwitch());
    public final Slider lineWidth = Menu.Slider("Line Width", 1.0f, 0.1f, 5.0f).visibility(z -> obsidianOutline.GetSwitch());
    protected final ICamera camera = new Frustum();
    protected final ArrayList<AnimatingHole> animatingHoles = new ArrayList<>();

    public HoleESP() {
        eventListeners = new EventListener[]{
                new Frame3DListener(this)
        };
    }

    protected boolean holeManagerContains(final BlockPos pos) {
        return Main.holeManager.getHoles().stream().anyMatch(holePos -> holePos.getPos().equals(pos));
    }

    protected boolean contains(final BlockPos pos) {
        return animatingHoles.stream().anyMatch(animatingHole -> animatingHole.pos.getPos().equals(pos));
    }

    protected void renderHole(final HoleManager.HolePos holePos, final AxisAlignedBB bb, boolean box, boolean outline, final Color boxColor, final Color outlineColor) {
        if (camera.isBoundingBoxInFrustum(bb.grow(2.0))) {
            if (holePos.isDouble()) {
                if (holePos.isWestDouble()) {
                    if (box) {
                        RenderUtil.drawCustomBB(boxColor, bb.minX - 1, bb.minY, bb.minZ, bb.maxX, bb.maxY - 1 + height.GetSlider(), bb.maxZ);
                    }
                    if (outline) {
                        RenderUtil.drawBlockOutlineBBWithHeight(new AxisAlignedBB(bb.minX - 1, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ), outlineColor, lineWidth.GetSlider(), height.GetSlider());
                    }
                } else {
                    if (box) {
                        RenderUtil.drawCustomBB(boxColor, bb.minX, bb.minY, bb.minZ - 1, bb.maxX, bb.maxY - 1 + height.GetSlider(), bb.maxZ);
                    }
                    if (outline) {
                        RenderUtil.drawBlockOutlineBBWithHeight(new AxisAlignedBB(bb.minX, bb.minY, bb.minZ - 1, bb.maxX, bb.maxY, bb.maxZ), outlineColor, lineWidth.GetSlider(), height.GetSlider());
                    }
                }
            } else {
                if (box) {
                    RenderUtil.drawBoxWithHeight(bb, boxColor, height.GetSlider());
                }
                if (outline) {
                    RenderUtil.drawBlockOutlineBBWithHeight(bb, outlineColor, lineWidth.GetSlider(), height.GetSlider());
                }
            }
        }
    }

    protected static class AnimatingHole {
        protected final HoleManager.HolePos pos;
        protected AxisAlignedBB bb;
        protected float scale, y;
        protected boolean remove;
        protected final HoleESP holeESP;

        public AnimatingHole(final HoleManager.HolePos pos, final HoleESP holeESP) {
            this.holeESP = holeESP;
            this.pos = pos;
            this.bb = new AxisAlignedBB(pos.getPos());
            this.scale = holeESP.animation.GetCombo().equals("Grow") ? 0.5f : 1.0f;
            this.y = holeESP.animation.GetCombo().equals("Pop") ? holeESP.startY.GetSlider() / 10.0f : 0.0f;
            remove = false;
        }

        protected void update() {
            AxisAlignedBB bb = this.bb;
            boolean box = pos.isBedrock() ? holeESP.bedrockBox.GetSwitch() : holeESP.obsidianBox.GetSwitch();
            boolean outline = pos.isBedrock() ? holeESP.bedrockOutline.GetSwitch() : holeESP.obsidianOutline.GetSwitch();
            Color color = pos.isBedrock() ? holeESP.bedrockBoxColor.GetColor() : holeESP.obsidianBoxColor.GetColor();
            Color outlineColor = pos.isBedrock() ? holeESP.bedrockOutlineColor.GetColor() : holeESP.obsidianOutlineColor.GetColor();
            switch (holeESP.animation.GetCombo()) {
                case "Fade":
                    final int alpha = (int) Math.min(color.getAlpha() / (Math.max(1.0, holeESP.mc.player.getDistanceSq(pos.getPos()) / holeESP.fadeAmount.GetSlider())), color.getAlpha());
                    color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
                    outlineColor = new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), alpha);
                    break;
                case "Grow":
                    if (remove) {
                        scale = AnimationUtil.decreaseNumber(scale, 0.5f, ((scale - 0.5f) / Minecraft.getDebugFPS()) * holeESP.animationSpeed.GetSlider());
                    } else {
                        scale = AnimationUtil.increaseNumber(scale, 1.0f, ((1.0f - scale) / Minecraft.getDebugFPS()) * holeESP.animationSpeed.GetSlider());
                    }
                    bb = bb.shrink(scale);
                    break;
                case "Pop":
                    if (remove) {
                        if (holeESP.fadePop.GetSwitch()) {
                            scale = AnimationUtil.decreaseNumber(scale, 0.0f, (scale / Minecraft.getDebugFPS()) * holeESP.animationSpeed.GetSlider());
                        }
                        y = AnimationUtil.increaseNumber(y, holeESP.startY.GetSlider() / 10.0f, ((holeESP.startY.GetSlider() / 10.0f - y) / Minecraft.getDebugFPS()) * holeESP.animationSpeed.GetSlider());
                    } else {
                        if (holeESP.fadePop.GetSwitch()) {
                            scale = AnimationUtil.increaseNumber(scale, color.getAlpha() / 255.0f, (((color.getAlpha() / 255.0f) - scale) / Minecraft.getDebugFPS()) * holeESP.animationSpeed.GetSlider());
                        }
                        y = AnimationUtil.decreaseNumber(y, 0.0f, (y / Minecraft.getDebugFPS()) * holeESP.animationSpeed.GetSlider());
                    }
                    if (holeESP.fadePop.GetSwitch()) {
                        color = new Color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, Math.min(1.0f, Math.max(0.0f, scale)));
                        outlineColor = new Color(outlineColor.getRed() / 255.0f, outlineColor.getGreen() / 255.0f, outlineColor.getBlue() / 255.0f, Math.min(1.0f, Math.max(0.0f, scale)));
                    }
                    bb = new AxisAlignedBB(bb.minX, bb.minY - y, bb.minZ, bb.maxX, bb.maxY - y, bb.maxZ);
                    break;
            }
            holeESP.renderHole(pos, bb, box, outline, color, outlineColor);
        }

        protected boolean canRemove() {
            if (remove) {
                switch (holeESP.animation.GetCombo()) {
                    case "None":
                    case "Fade":
                        return true;
                    case "Grow":
                        return scale <= 0.52f;
                    case "Pop":
                        return y + 0.05f >= (holeESP.startY.GetSlider() / 10.0f);
                }
            }
            return false;
        }
    }
}
