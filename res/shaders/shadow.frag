#version 110

uniform mat4 view;

varying mat4 projection;
varying vec3 position;

void main() {
	vec4 pos = projection * view * vec4(position, 1);
	gl_FragDepth = pos.z / pos.w;
}
