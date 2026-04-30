package com.github.dumann089.theatricalextralights.laser;

/**
 * One beam emitted by a laser fixture, expressed in fixture-local space (the
 * local Z+ axis points where pan/tilt aim the fixture). Yaw rotates around Y,
 * pitch around X. Length is in blocks (capped by ray trace later if needed).
 */
public final class LaserBeam {
    public final float yawDeg;
    public final float pitchDeg;
    public final float length;
    public final int color;

    public LaserBeam(float yawDeg, float pitchDeg, float length, int color) {
        this.yawDeg = yawDeg;
        this.pitchDeg = pitchDeg;
        this.length = length;
        this.color = color;
    }
}
