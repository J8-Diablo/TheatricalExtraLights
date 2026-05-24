// blockentities/interfaces/HasGobo.java
package com.github.dumann089.theatricalextralights.blockentities.interfaces;

public interface HasGobo {
    /** Slot del gobo (0 = abierto/blanco). */
    int getGobo();

    /** Rotación acumulada del gobo en grados (animada en lightTick). */
    float getGoboRotation();

    /** Zoom DMX 0-255 → apertura del cono. */
    int getZoom();

    /** Zoom interpolado para partialTicks. */
    float getPartialZoom(float partialTicks);

    /** Intensidad interpolada 0-255. */
    float getPartialIntensity(float partialTicks);

    /** Color RGB empaquetado. */
    int getColour();

    /** Focus DMX 0-255 → dureza del borde. */
    int getFocus();

    /** Pan interpolado en grados. */
    float getPartialPanDeg(float partialTicks);

    /** Tilt interpolado en grados. */
    float getPartialTiltDeg(float partialTicks);
}