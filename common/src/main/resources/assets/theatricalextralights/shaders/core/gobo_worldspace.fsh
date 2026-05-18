#version 150

// ============================================================
//  gobo_worldspace.fsh
//  Fragment Shader — World-Space Volumetric Gobo Spotlight
// ============================================================
//
//  PRINCIPIO FUNDAMENTAL:
//  Este shader NUNCA usa:
//    ✗ gl_FragCoord
//    ✗ depth buffer / texture
//    ✗ inverse projection
//    ✗ screen UV
//    ✗ reconstrucción de world-space desde depth
//    ✗ ninguna referencia a la cámara
//
//  TODO el cálculo se hace en ESPACIO LOCAL DEL CONO.
//
//  CONVENCIÓN DE EJES (espacio local del cono):
//    +Z  = eje del cono (desde apex hacia la base)
//    X,Y = plano perpendicular al eje
//    (0,0,0) = apex (punto de emisión de luz)
//
//  CÁLCULO DEL UV DEL GOBO:
//  Para un punto P = (px, py, pz) en espacio local:
//    1. La distancia al eje es r = sqrt(px² + py²)
//    2. El radio máximo a esa profundidad es r_max = pz * tan(halfAngle)
//    3. t_radial = r / r_max   ∈ [0, 1]
//    4. La dirección angular es θ = atan(py, px)
//    5. UV del gobo = 0.5 + t_radial * 0.5 * vec2(cos(θ+rot), sin(θ+rot))
//
//  Esto es proyección cónica central pura → igual que un proyector real.
//
//  FALLOFF VOLUMÉTRICO:
//  Simula la dispersión de Mie en humo/niebla:
//    f(t_radial, t_axial) = borde_suave × falloff_axial × intensidad
//
// ============================================================

// ── Inputs del vertex shader ───────────────────────────────────────────────
in vec3  v_LocalPos;
in float v_Radial;
in float v_Axial;

// ── Textura del gobo ───────────────────────────────────────────────────────
uniform sampler2D Sampler0;   // gobo texture (slot 0)

// ── Parámetros del cono ────────────────────────────────────────────────────
uniform float u_ConeHalfAngle;  // half-angle en radianes
uniform float u_ConeLength;     // longitud total en bloques

// ── Color e intensidad ─────────────────────────────────────────────────────
uniform vec4 u_ColorIntensity;  // rgb = color DMX, a = intensidad/dimmer

// ── Rotación del gobo (DMX) ────────────────────────────────────────────────
uniform float u_GoboRotation;   // en radianes

// ── Output ─────────────────────────────────────────────────────────────────
out vec4 fragColor;

// ── Constantes ─────────────────────────────────────────────────────────────
const float PI       = 3.14159265358979323846;
const float TWO_PI   = 6.28318530717958647692;

// Anchura del suavizado del borde del cono (en fracción del radio)
// 0.05 = borde muy duro, 0.25 = borde muy suave
const float EDGE_SOFTNESS = 0.12;

// Exponente del falloff radial  (más alto = haz más concentrado en el eje)
const float RADIAL_FALLOFF_POWER = 1.2;

// Exponente del falloff axial   (simula absorción volumétrica)
const float AXIAL_FALLOFF_POWER  = 0.6;

// ── Funciones auxiliares ───────────────────────────────────────────────────

/**
 * Suavizado cúbico tipo smoothstep, pero con control de borde.
 * Retorna 1.0 cuando x < (1-edge), baja suavemente a 0 en x=1.
 */
float edgeFade(float x, float edge) {
    float t = clamp((x - (1.0 - edge)) / edge, 0.0, 1.0);
    return 1.0 - t * t * (3.0 - 2.0 * t);
}

// ── Main ───────────────────────────────────────────────────────────────────
void main() {

    // ── 1.  Posición local del fragmento ────────────────────────────────────
    vec3 P = v_LocalPos;

    // Profundidad a lo largo del eje del cono
    float depth = P.z;

    // Descartar fragmentos detrás del apex o más allá de la longitud del cono
    if (depth <= 0.0 || depth > u_ConeLength) {
        discard;
    }

    // ── 2.  Coordenadas polares en el plano perpendicular al eje ────────────
    float r     = length(P.xy);                      // distancia al eje
    float r_max = depth * tan(u_ConeHalfAngle);      // radio máximo a esta z

    // t_radial: 0 en el eje, 1 en el borde exacto del cono
    float t_radial = r / max(r_max, 0.0001);

    // Descartar fragmentos que están fuera del cono
    // (con pequeño epsilon para evitar artefactos en el borde)
    if (t_radial > 1.02) discard;

    // ── 3.  UV del gobo (proyección cónica central) ─────────────────────────
    //
    //  La proyección cónica mapea cada punto del plano de la base al UV:
    //    centro del gobo → eje del cono
    //    borde del gobo  → borde del cono a cualquier profundidad
    //
    //  Esto es matemáticamente equivalente a un proyector de diapositivaso
    //  o un spotlight teatral con gobo: la imagen se mantiene estable en
    //  world-space independientemente del ángulo de cámara.

    float theta_raw = atan(P.y, P.x);
    float theta = theta_raw + u_GoboRotation;

    // UV en [0,1]: centro = 0.5, borde = 0.0 o 1.0
    float uv_r = t_radial * 0.5;
    vec2 goboUV = vec2(0.5) + uv_r * vec2(cos(theta), sin(theta));

    // Clamp para evitar artefactos de wrapping fuera del disco
    goboUV = clamp(goboUV, 0.0, 1.0);

    // ── 4.  Muestreo del gobo ────────────────────────────────────────────────
    vec4 goboSample = texture(Sampler0, goboUV);

    // El canal alpha del gobo actúa como máscara de opacidad:
    // 0 = opaco (bloquea la luz), 1 = transparente (deja pasar la luz).
    // Para gobos con bordes negros/opacos, esto invierte correctamente.
    float goboMask = goboSample.a;

    // Si la textura del gobo es RGB sin alpha significativo,
    // usar la luminancia como máscara:
    if (goboMask < 0.01) {
        goboMask = dot(goboSample.rgb, vec3(0.299, 0.587, 0.114));
    }

    // ── 5.  Falloff volumétrico ──────────────────────────────────────────────

    // a) Falloff radial: luz concentrada en el eje, se desvanece hacia el borde
    float radialFalloff = pow(1.0 - clamp(t_radial, 0.0, 1.0), RADIAL_FALLOFF_POWER);

    // b) Suavizado del borde del cono
    float edgeWeight = edgeFade(t_radial, EDGE_SOFTNESS);

    // c) Falloff axial: simula absorción de partículas (ley inversa al cuadrado
    //    aproximada como potencia para mantener estabilidad numérica)
    float t_axial = clamp(depth / u_ConeLength, 0.0, 1.0);
    float axialFalloff = pow(1.0 - t_axial, AXIAL_FALLOFF_POWER);

    // d) Falloff combinado
    float volumetricWeight = radialFalloff * edgeWeight * axialFalloff;

    // ── 6.  Color final ──────────────────────────────────────────────────────
    vec3  lightColor = u_ColorIntensity.rgb;
    float intensity  = u_ColorIntensity.a;

    // Combinar el gobo (patron de imagen) con el color DMX y el falloff
    vec3 goboColor = goboSample.rgb * lightColor;

    // El resultado final:
    //   goboColor × goboMask × falloff × intensidad
    vec3 finalColor = goboColor * goboMask * volumetricWeight * intensity;

    // Blending aditivo: alpha = 0 significa que OpenGL suma directamente
    // el color sin usar el alpha para mezcla (ONE, ONE en RenderType)
    fragColor = vec4(finalColor, 0.0);

    // Guard: descartar fragmentos casi negros (optimización fill-rate)
    if (dot(fragColor.rgb, vec3(1.0)) < 0.001) discard;
}
