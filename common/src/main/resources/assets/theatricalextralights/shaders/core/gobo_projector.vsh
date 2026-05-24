#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;

out vec3 ViewPos;
out vec4 VertexColor;

void main() {
    // Proyección estándar de Minecraft para dibujar en pantalla
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    // Extraemos la posición exacta para la matemática de la luz
    ViewPos = (ModelViewMat * vec4(Position, 1.0)).xyz;
    VertexColor = Color;
}