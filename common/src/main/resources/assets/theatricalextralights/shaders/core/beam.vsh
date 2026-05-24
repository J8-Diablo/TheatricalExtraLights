#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 vertexColor;
out vec2 texCoord0;

void main() {
    vec4 worldPos = ModelViewMat * vec4(Position, 1.0);

    gl_Position = ProjMat * worldPos;

    // usamos Z en espacio de cámara para el fade
    float dist = abs(worldPos.z);

    // ajustá estos valores a gusto
    float fade = clamp(1.0 - dist * 0.05, 0.0, 1.0);

    vertexColor = vec4(Color.rgb, Color.a * fade);

    texCoord0 = UV0;
}