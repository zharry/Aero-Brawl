#version 110

varying vec4 color;
varying vec3 normal;
varying vec3 vertex;
varying vec2 tCoord;

attribute vec2 texCoord;

void main(void) {
	tCoord = texCoord;
	color = gl_Color;
	normal = normalize(gl_NormalMatrix * gl_Normal);
	vertex = (gl_ModelViewMatrix * gl_Vertex).xyz;
    gl_Position = gl_ProjectionMatrix * gl_ModelViewMatrix * gl_Vertex;
}