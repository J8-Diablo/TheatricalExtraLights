#version 150

uniform sampler2D Sampler0;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    // 🔥 REPETICIÓN PURA Y DIRECTA: Solo lee la textura y procesa el canal alfa original
    vec4 goboTex = texture(Sampler0, texCoord0);

    float goboAlpha = pow(goboTex.a, 0.7);

    float finalAlpha = goboAlpha * vertexColor.a;
    vec3 finalRGB = goboTex.rgb * vertexColor.rgb;

    fragColor = vec4(finalRGB, finalAlpha);
}