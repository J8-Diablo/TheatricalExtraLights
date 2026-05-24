#version 150

uniform sampler2D Sampler0;

uniform vec3 LightPos;
uniform vec3 LightDir;
uniform vec3 AxisU;
uniform vec3 AxisV;
uniform float TanHalfAngle;
uniform float MaxLen;
uniform float MaxGoboDist;

in vec3 ViewPos;
in vec4 VertexColor;

out vec4 fragColor;

void main() {
    vec3 vecToFrag = ViewPos - LightPos;
    float zDist = dot(vecToFrag, LightDir);

    float effectiveMax = (MaxGoboDist > 0.0) ? min(MaxGoboDist, MaxLen) : MaxLen;

    if (zDist <= 0.0 || zDist > effectiveMax) discard;

    float rZ = max(zDist * TanHalfAngle, 0.0001);
    float uDist = dot(vecToFrag, AxisU);
    float vDist = dot(vecToFrag, AxisV);
    float distFromCenter = sqrt(uDist * uDist + vDist * vDist);

    if (distFromCenter > rZ) discard;

    vec2 projectedUV = vec2(
        (uDist / rZ) * 0.5 + 0.5,
        1.0 - ((vDist / rZ) * 0.5 + 0.5)
    );

    float edgeFade = 1.0 - smoothstep(rZ * 0.92, rZ, distFromCenter);
    float lengthFade = 1.0 - smoothstep(effectiveMax * 0.40, effectiveMax, zDist);

    vec4 texColor = texture(Sampler0, projectedUV);

    float brightness = max(texColor.r, max(texColor.g, texColor.b));
    if (brightness < 0.01) discard;

    vec3 finalRGB = texColor.rgb * VertexColor.rgb * 2.2;
    float finalAlpha = texColor.a * VertexColor.a * edgeFade * lengthFade;

    fragColor = vec4(finalRGB, finalAlpha);
}