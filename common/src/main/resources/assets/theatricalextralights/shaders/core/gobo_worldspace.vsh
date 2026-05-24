#version 150

// ============================================================
//  gobo_worldspace.vsh  — CORREGIDO
//
//  CAMBIO CLAVE:
//  v_LocalPos se pasa como la posición SIN la ModelViewMat.
//  Es la posición en espacio LOCAL del cono (antes de cualquier
//  transformación de cámara o mundo).
//  El fragment shader usa v_LocalPos para calcular la UV del
//  gobo y el falloff — ambos son cálculos en espacio local puro.
// ============================================================

in vec3 Position;
in vec2 UV0;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

// Pasamos la posición LOCAL (sin transformar) al fragment
out vec3  v_LocalPos;
out float v_Axial;     // UV0.y = t_axial [0=apex, 1=base]

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    // v_LocalPos = posición en espacio LOCAL del cono
    // (es la Position del vértice tal como la emitió Java,
    //  con (0,0,0)=apex y z=profundidad a lo largo del eje)
    v_LocalPos = Position;
    v_Axial    = UV0.y;
}
