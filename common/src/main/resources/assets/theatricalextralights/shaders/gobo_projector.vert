#version 330 core

// Fullscreen triangle-strip quad en NDC.
// El vertex shader no hace nada especial: los vértices ya llegan en NDC [-1,1].
// Toda la lógica de proyección está en el fragment shader.
layout(location = 0) in vec2 aPos;

out vec2 v_ScreenUV;

void main() {
    v_ScreenUV  = aPos * 0.5 + 0.5;
    gl_Position = vec4(aPos, 0.0, 1.0);
}
