package com.github.dumann089.theatricalextralights.client.gobo;

import java.util.List;

/**
 * Patrones de "fake volumetric beams": múltiples beams en posiciones/rotaciones
 * relativas, hardcodeados por patrón. Se asocian uno-a-uno con los slots del GoboLibrary.
 *
 * BeamTransform:
 *   offsetX, offsetZ  — desplazamiento lateral relativo al centro del fixture (en bloques)
 *   tiltDeg           — inclinación adicional del beam en grados (0 = recto)
 *   panDeg            — rotación lateral adicional en grados
 *   thicknessMult     — multiplicador del grosor del beam (1.0 = normal)
 *   alphaMult         — multiplicador de alpha/opacidad (1.0 = normal)
 */
public enum FakeVolumetricBeamPattern {

    SINGLE(
            new BeamTransform(0f,    0f,    0f,  0f,  1.0f, 1.0f)
    ),
    RING_8(
            new BeamTransform(0f, 0f,  5f,   0f,  0.55f, 0.75f),
            new BeamTransform(0f, 0f,  5f,  45f,  0.55f, 0.75f),
            new BeamTransform(0f, 0f,  5f,  90f,  0.55f, 0.75f),
            new BeamTransform(0f, 0f,  5f, 135f,  0.55f, 0.75f),
            new BeamTransform(0f, 0f,  5f, 180f,  0.55f, 0.75f),
            new BeamTransform(0f, 0f,  5f, 225f,  0.55f, 0.75f),
            new BeamTransform(0f, 0f,  5f, 270f,  0.55f, 0.75f),
            new BeamTransform(0f, 0f,  5f, 315f,  0.55f, 0.75f)
    ),

    /**
     * SPIRAL_6 — 6 beams en espiral tipo "caracol", cada uno más abierto y rotado
     * progresivamente. Efecto de haz en giro.
     */
    SPIRAL_6(
            new BeamTransform( 0.00f,  0.00f,  0f,   0f,   0.9f,  1.0f),
            new BeamTransform( 0.0f,  0.0f, 2f,  30f,   0.75f, 0.85f),
            new BeamTransform( 0.0f,  0.0f,  4f,  70f,   0.65f, 0.7f),
            new BeamTransform( 0.0f, 0.0f, 6f, 120f,   0.55f, 0.6f),
            new BeamTransform( 0.00f,  0.0f, 8f, 170f,   0.45f, 0.5f),
            new BeamTransform(0.0f,  0.0f, 10f, 220f,   0.35f, 0.4f)
    ),

    /**
     * DOUBLE_RING — dos anillos concéntricos: uno interno ajustado y uno externo más abierto.
     * Apto para gobos de líneas radiales o círculos.
     */
    DOUBLE_RING(
            // Anillo interno (4 beams, radio pequeño, 2° apertura)
            new BeamTransform( 0.03f,  0.00f,  2f,   0f,   0.6f,  0.8f),
            new BeamTransform( 0.00f,  0.0f,  2f,  90f,   0.6f,  0.8f),
            new BeamTransform(0.0f,  0.00f,  2f, 180f,   0.6f,  0.8f),
            new BeamTransform( 0.00f, 0.0f,  2f, 270f,   0.6f,  0.8f),
            // Anillo externo (6 beams, radio mayor, 5° apertura)
            new BeamTransform( 0.0f,  0.00f,  5f,   0f,   0.4f,  0.55f),
            new BeamTransform( 0.0f, 0.06f,  5f,  60f,   0.4f,  0.55f),
            new BeamTransform(0.0f, 0.0f,  5f, 120f,   0.4f,  0.55f),
            new BeamTransform(0.0f,  0.00f,  5f, 180f,   0.4f,  0.55f),
            new BeamTransform(0.0f,0.0f,  5f, 240f,   0.4f,  0.55f),
            new BeamTransform( 0.0f,0.0f,  5f, 300f,   0.4f,  0.55f)
    ),

    /**
     * CROSS_5 — beam central más 4 satélites en cruz, típico de gobo de "breakups" o "stains".
     */
    CROSS_5(
            new BeamTransform( 0.00f,  0.00f,  0f,   0f,   1.0f,  1.0f),
            new BeamTransform( 0.07f,  0.00f,  4f,   0f,   0.5f,  0.65f),
            new BeamTransform(-0.07f,  0.00f,  4f, 180f,   0.5f,  0.65f),
            new BeamTransform( 0.00f,  0.07f,  5f,  90f,   0.5f,  0.65f),
            new BeamTransform( 0.00f, -0.07f,  5f, 270f,   0.5f,  0.65f)
    ),

    /**
     * FAN_7 — 7 beams en abanico horizontal, apertura de -18° a +18°.
     * Efecto de líneas verticales proyectadas en abanico.
     */
    FAN_7(
            // Anillo interno (4 beams, radio pequeño, 2° apertura)
            new BeamTransform( 0.03f,  0.00f,  2f,   0f,   0.2f,  0.8f),
            new BeamTransform( 0.00f,  0.0f,  2f,  90f,   0.4f,  0.8f),
            new BeamTransform(0.0f,  0.00f,  4f, 180f,   0.7f,  0.8f),
            new BeamTransform( 0.00f, 0.0f,  3f, 270f,   0.6f,  0.8f),
            // Anillo externo (6 beams, radio mayor, 5° apertura)
            new BeamTransform( 0.0f,  0.00f,  6f,   0f,   0.2f,  0.55f),
            new BeamTransform( 0.0f, 0.06f,  7f,  60f,   0.4f,  0.55f),
            new BeamTransform(0.0f, 0.0f,  5f, 120f,   0.2f,  0.55f),
            new BeamTransform(0.0f,  0.00f,  7f, 180f,   0.3f,  0.55f),
            new BeamTransform(0.0f,0.0f,  6f, 240f,   0.9f,  0.55f),
            new BeamTransform( 0.0f,0.0f,  7f, 300f,   0.2f,  0.55f)
    ),

    /**
     * SCATTER_NATURE — dispersión orgánica irregular. Para gobos de "nature", "bubbles", etc.
     */
    SCATTER_NATURE(
            new BeamTransform( 0.00f,  0.00f,  0f,   0f,   0.85f, 0.9f),
            new BeamTransform( 0.0f,  0.00f,  2f,  25f,   0.5f,  0.65f),
            new BeamTransform(-0.0f,  0.00f,  3f,  110f,  0.45f, 0.6f),
            new BeamTransform(-0.0f, -0.00f,  4f,  190f,  0.5f,  0.55f),
            new BeamTransform( 0.0f, -0.00f,  5f,  310f,  0.4f,  0.5f),
            new BeamTransform( 0.0f,  0.00f,  6f,  55f,   0.35f, 0.45f),
            new BeamTransform(-0.0f, -0.00f,  7f,  250f,  0.3f,  0.4f)
    );

    // -----------------------------------------------------------------------

    /** Un transform relativo aplicado a un beam individual dentro del patrón. */
    public record BeamTransform(
            float offsetX,       // Offset lateral X (bloques), relativo al centro del fixture
            float offsetZ,       // Offset lateral Z (bloques), relativo al centro del fixture
            float tiltDeg,       // Tilt adicional en grados (apertura hacia afuera)
            float panDeg,        // Pan adicional en grados (rotación lateral)
            float thicknessMult, // Multiplicador de grosor del beam
            float alphaMult      // Multiplicador de opacidad
    ) {}

    private final List<BeamTransform> transforms;

    FakeVolumetricBeamPattern(BeamTransform... transforms) {
        this.transforms = List.of(transforms);
    }

    public List<BeamTransform> getTransforms() {
        return transforms;
    }

    public int getBeamCount() {
        return transforms.size();
    }
}