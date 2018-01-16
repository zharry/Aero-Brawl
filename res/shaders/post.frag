#version 110

#extension GL_ARB_texture_multisample : enable

varying vec2 tCoord;

uniform int samples;

uniform sampler2DMS texture;

//uniform sampler2DMS diffuse;
//uniform sampler2DMS normal;
//uniform sampler2DMS depth;
//uniform sampler2DMS position;
//
//uniform int lights;
//
//uniform vec3 shadowPosition;
//uniform samplerCube shadowTexture;

void main() {
	vec2 size = vec2(textureSize(texture));

	vec3 clrInput = vec3(0.0, 0.0, 0.0);

	for(int i = 0; i < samples; ++i) {
		clrInput += texelFetch(texture, ivec2(tCoord.x * size.x, tCoord.y * size.y), i).rgb;
	}

	clrInput /= float(samples);

	gl_FragColor = vec4(clrInput, 1.0);
}
