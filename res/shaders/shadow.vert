#version 110

uniform mat4 view;

varying vec3 position;

void main() {
	position = (gl_ModelViewMatrix * gl_Vertex).xyz;
    gl_Position = gl_ProjectionMatrix * view * gl_ModelViewMatrix * gl_Vertex;
}
