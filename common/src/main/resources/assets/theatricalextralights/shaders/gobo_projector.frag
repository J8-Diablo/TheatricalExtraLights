#version 330 core

// ─────────────────────────────────────────────────────────────────────────────
// GOBO PROJECTOR — Fragment Shader
// ─────────────────────────────────────────────────────────────────────────────
//
// Algoritmo completo en un solo frag shader (deferred):
//
//  1. Leer depth de Minecraft → reconstruir posición world-space
//  2. Proyectar esa posición en el frustum del proyector
//  3. Si está dentro del frustum, calcular UV del gobo
//  4. Aplicar atenuación de distancia + angular + feathering de borde
//  5. Samplear textura gobo PNG y emitir luz aditiva
//
// World-space aquí es CÁMARA-RELATIVO (consistente con la modelview de MC).
// ─────────────────────────────────────────────────────────────────────────────

in  vec2 v_ScreenUV;
out vec4 FragColor;

// ── Minecraft scene ──────────────────────────────────────────────────────────
uniform sampler2D u_MCDepthTex;   // Depth buffer de Minecraft (slot 0)
                                   // REQUIERE GL_TEXTURE_COMPARE_MODE = GL_NONE
uniform vec2      u_ScreenSize;   // Dimensiones en píxeles (no necesaria con v_ScreenUV pero útil para debug)
uniform mat4      u_InvMCVP;      // Inversa de (Proj_MC × ModelView_MC)

// ── Projector ────────────────────────────────────────────────────────────────
uniform mat4      u_ProjectorVP;  // Proj_projector × View_projector (cám-relativo)
uniform vec3      u_ProjectorPos; // Posición del proyector relativa a la cámara
uniform vec3      u_ProjectorDir; // Dirección normalizada del proyector
uniform vec3      u_Color;        // Color RGB del haz de luz
uniform float     u_Intensity;    // Multiplicador de brillo
uniform float     u_Range;        // Distancia máxima de alcance (metros/bloques)
uniform float     u_Angle;        // SEMIÁNGULO del cono en radianes

// ── Gobo textures (slots 1..9) ───────────────────────────────────────────────
// Indexación dinámica de sampler arrays NO garantizada en GLSL 3.30.
// Usamos switch/case con literales de entero (legal y portable).
uniform int       u_GoboIndex;    // 1..9
uniform sampler2D u_GoboTex1;
uniform sampler2D u_GoboTex2;
uniform sampler2D u_GoboTex3;
uniform sampler2D u_GoboTex4;
uniform sampler2D u_GoboTex5;
uniform sampler2D u_GoboTex6;
uniform sampler2D u_GoboTex7;
uniform sampler2D u_GoboTex8;
uniform sampler2D u_GoboTex9;

// ─────────────────────────────────────────────────────────────────────────────
vec4 sampleGobo(int idx, vec2 uv) {
    switch (idx) {
        case 1: return texture(u_GoboTex1, uv);
        case 2: return texture(u_GoboTex2, uv);
        case 3: return texture(u_GoboTex3, uv);
        case 4: return texture(u_GoboTex4, uv);
        case 5: return texture(u_GoboTex5, uv);
        case 6: return texture(u_GoboTex6, uv);
        case 7: return texture(u_GoboTex7, uv);
        case 8: return texture(u_GoboTex8, uv);
        case 9: return texture(u_GoboTex9, uv);
        default: return vec4(0.0);
    }
}

// ─────────────────────────────────────────────────────────────────────────────
void main() {

    // ── 1. Reconstruir posición world-space (cámara-relativa) ─────────────
    float depth = texture(u_MCDepthTex, v_ScreenUV).r;

    // El fondo (depth=1.0) no tiene geometría. Descartarlo evita que el
    // gobo se proyecte "en el cielo" cuando el rayo de proyección no golpea nada.
    if (depth >= 0.9999) discard;

    // Reconstruir NDC completo
    vec2  ndc2    = v_ScreenUV * 2.0 - 1.0;
    float ndcZ    = depth * 2.0 - 1.0;
    vec4  clipPos = vec4(ndc2, ndcZ, 1.0);

    // Deshacer la projection+view de Minecraft para obtener world-space
    vec4 wPos4 = u_InvMCVP * clipPos;
    vec3 wPos  = wPos4.xyz / wPos4.w;

    // ── 2. Proyectar en el frustum del proyector ──────────────────────────
    vec4 pPos4 = u_ProjectorVP * vec4(wPos, 1.0);

    // Descartar puntos detrás del plano near del proyector
    if (pPos4.w <= 0.0) discard;

    vec3 pNDC = pPos4.xyz / pPos4.w;

    // Descartar fuera de los límites del frustum
    if (abs(pNDC.x) > 1.0 || abs(pNDC.y) > 1.0 ||
        pNDC.z < -1.0    || pNDC.z > 1.0) discard;

    // ── 3. UV del gobo en [0,1] ───────────────────────────────────────────
    // pNDC.xy en [-1,1] → UV en [0,1]
    vec2 goboUV = pNDC.xy * 0.5 + 0.5;

    // ── 4. Atenuación por distancia ───────────────────────────────────────
    vec3  toPixel = wPos - u_ProjectorPos;
    float dist    = length(toPixel);
    if (dist > u_Range) discard;

    // Cuadrática: da una caída más física (como luz real)
    float distAtt = 1.0 - (dist / u_Range);
    distAtt = distAtt * distAtt;

    // ── 5. Atenuación angular con feathering ──────────────────────────────
    vec3  dirToP = toPixel / max(dist, 0.0001);
    float cosA   = dot(dirToP, normalize(u_ProjectorDir));

    // clamp evita NaN en acos por errores de floating point en bordes exactos
    float angle  = acos(clamp(cosA, -1.0, 1.0));
    if (angle > u_Angle) discard;

    // Feathering: suavizado en el último 20% del cono (borde de halo)
    float edgeStart = u_Angle * 0.80;
    float angAtt    = smoothstep(u_Angle, edgeStart, angle);

    // ── 6. Sample gobo y output ───────────────────────────────────────────
    vec4  goboSample = sampleGobo(u_GoboIndex, goboUV);

    // El alpha del PNG actúa como máscara de stencil del gobo.
    // Los píxeles transparentes del PNG = ausencia de luz.
    float mask     = goboSample.a;
    float combined = u_Intensity * distAtt * angAtt * mask;

    // RGB aditivo: se acumula en el FBO con blending ONE,ONE.
    // Alpha = 0 para que el compose pass haga blit solo con color.
    FragColor = vec4(u_Color * goboSample.rgb * combined, 0.0);
}
