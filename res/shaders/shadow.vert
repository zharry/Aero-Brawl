#version 110

uniform mat4 view;
varying mat4 projection;

varying vec3 position;

void main() {
	projection = gl_ProjectionMatrix;
	position = (gl_ModelViewMatrix * gl_Vertex).xyz;
    gl_Position = gl_ProjectionMatrix * view * gl_ModelViewMatrix * gl_Vertex;
}
