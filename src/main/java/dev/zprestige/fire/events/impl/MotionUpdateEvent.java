package dev.zprestige.fire.events.impl;

import dev.zprestige.fire.events.eventbus.event.Event;

public class MotionUpdateEvent extends Event {
    protected double x, y, z, prevX, prevY, prevZ;
    protected float rotationYaw, rotationPitch, prevYaw, prevPitch;
    protected boolean onGround, prevOnGround;
    protected Stage stage;

    public MotionUpdateEvent(Stage stage, MotionUpdateEvent event) {
        this(stage, event.x, event.y, event.z, event.rotationYaw, event.rotationPitch, event.onGround);
    }

    public MotionUpdateEvent(Stage stage, double x, double y, double z, float rotationYaw, float rotationPitch, boolean onGround) {
        this.stage = stage;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        this.onGround = onGround;
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.prevYaw = rotationYaw;
        this.prevPitch = rotationPitch;
        this.prevOnGround = onGround;
    }

    public Stage getStage() {
        return stage;
    }

    public double getPrevX() {
        return prevX;
    }

    public double getPrevY() {
        return prevY;
    }

    public double getPrevZ() {
        return prevZ;
    }

    public float getPrevYaw() {
        return prevYaw;
    }

    public float getPrevPitch() {
        return prevPitch;
    }

    public boolean isPrevOnGround() {
        return prevOnGround;
    }

    public float getRotationYaw() {
        return rotationYaw;
    }

    public float getRotationPitch() {
        return rotationPitch;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return rotationYaw;
    }

    public void setYaw(float rotationYaw) {
        this.rotationYaw = rotationYaw;
    }

    public float getPitch() {
        return rotationPitch;
    }

    public void setPitch(float rotationPitch) {
        this.rotationPitch = rotationPitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public enum Stage {
        Pre,
        Post
    }
}