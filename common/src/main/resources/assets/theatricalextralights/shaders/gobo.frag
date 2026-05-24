#version 150

uniform sampler2D Sampler0;
uniform vec4 GoboColor;

out vec4 fragColor;

void main() {

    vec4 tex = texture(Sampler0, vec2(0.5));

    fragColor = tex * GoboColor;

    fragColor = vec4(1.0,0.0,0.0,0.5);
}