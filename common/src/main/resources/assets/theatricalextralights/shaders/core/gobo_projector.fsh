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

    vec2 projectedUV = vec2(
        (uDist / rZ) * 0.5 + 0.5,
        1.0 - ((vDist / rZ) * 0.5 + 0.5)
    );

    // Always fade the outer 35% of the cone radius regardless of size.
    // Pure proportional fade — no absolute clamp — so large gobos get
    // the same soft-edge look as small ones.

    // Soft edge fade — but clamp minimum alpha inside the cone to avoid
    // dark/bright seams at block boundaries showing through as grid lines.

    float edgeFadeRange = rZ * 0.25;
    float edgeFade = 1.0 - smoothstep(rZ - edgeFadeRange, rZ, distFromCenter);

    // Ensure fragments well inside the cone never go below a minimum alpha,
    // which prevents the grid pattern from showing at block junctions.

    edgeFade = max(edgeFade, 0.85 * step(distFromCenter, rZ * 0.7));

    // Keep the gobo fully visible across most of its range and only fade in
    // the last ~15% so it doesn't look weak well before reaching MaxGoboDist.
    float lengthFade = 1.0 - smoothstep(effectiveMax * 0.85, effectiveMax, zDist);

    vec4 texColor = texture(Sampler0, projectedUV);

    float brightness = max(texColor.r, max(texColor.g, texColor.b));
    if (brightness < 0.01) discard;

    // Reduced multiplier (1.2 instead of 2.2) to avoid blown-out pure white
    vec3 finalRGB = texColor.rgb * VertexColor.rgb * 0.2;
    float finalAlpha = texColor.a * VertexColor.a * edgeFade * lengthFade;

    fragColor = vec4(finalRGB, finalAlpha);
}