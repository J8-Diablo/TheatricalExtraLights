#version 150

uniform sampler2D Sampler0; // Gobo PNG (Slot 0)
uniform sampler2D Sampler1; // Texture Depth Buffer Copiado (Slot 1)

uniform mat4 u_InvGameVP;
uniform mat4 u_LightVP;
uniform vec2 ScreenSize;
uniform float u_GoboRotation;
uniform vec4 u_ColorIntensity;

out vec4 fragColor;

void main() {
    // Reconstrucción basada en gl_FragCoord para erradicar el jittering de interpolación
    vec2 screenUV = gl_FragCoord.xy / ScreenSize;
    float depth = texture(Sampler1, screenUV).r;

    if (depth >= 1.0) discard;

    // Mapear NDC homogéneo estricto de OpenGL
    vec4 ndcPos = vec4(
        screenUV.x * 2.0 - 1.0,
        screenUV.y * 2.0 - 1.0,
        depth * 2.0 - 1.0,
        1.0
    );

    // Reproyección al espacio de mundo relativo a la cámara
    vec4 worldPos = u_InvGameVP * ndcPos;
    if (worldPos.w == 0.0) discard;
    worldPos /= worldPos.w;

    // Proyección en el espacio intrínseco del haz de luz
    vec4 lightSpacePos = u_LightVP * worldPos;
    if (lightSpacePos.w <= 0.0) discard; // Bloqueo de retroproyección trasera inversiva
    vec3 lightClip = lightSpacePos.xyz / lightSpacePos.w;

    // HARDWARE FRUSTUM CLIPPING PERFECTO ADAPTATIVO
    if (lightClip.x < -1.0 || lightClip.x > 1.0 ||
        lightClip.y < -1.0 || lightClip.y > 1.0 ||
        lightClip.z < -1.0 || lightClip.z > 1.0) {
        discard;
    }

    // Conversión a mapeo UV plano de textura
    vec2 goboUV = lightClip.xy * 0.5 + 0.5;
    goboUV.y = 1.0 - goboUV.y; // Corrección de Handedness / Flip de Y invertido de Mojang

    // Rotación estable en GPU libre de colapso de derivadas
    if (abs(u_GoboRotation) > 0.001) {
        vec2 uvCentered = goboUV - 0.5;
        float cosR = cos(u_GoboRotation);
        float sinR = sin(u_GoboRotation);
        goboUV = vec2(
            uvCentered.x * cosR - uvCentered.y * sinR,
            uvCentered.x * sinR + uvCentered.y * cosR
        ) + 0.5;
    }

    vec4 goboTexel = texture(Sampler0, goboUV);
    vec3 finalRGB = u_ColorIntensity.rgb * goboTexel.rgb * u_ColorIntensity.a * goboTexel.a;

    // Purgado de fragmentos oscuros para optimización de ancho de banda
    if (dot(finalRGB, vec3(0.2126, 0.7152, 0.0722)) < 0.002) discard;

    fragColor = vec4(finalRGB, 1.0);
}