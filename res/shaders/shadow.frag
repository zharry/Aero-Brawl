#version 110

varying vec3 position;

void main() {
	gl_FragDepth = length(position - gl_LightSource[0].position.xyz) / 1000.0;
}
