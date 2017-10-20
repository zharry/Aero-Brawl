#version 110

varying vec2 tCoord;

void main() {
	tCoord = (gl_Vertex.xy + vec2(1.0, 1.0)) / 2.0;
	gl_Position = gl_Vertex;
}
