#version 110

varying vec4 color;
varying vec3 normal;
varying vec3 vertex;
varying vec2 tCoord;

uniform bool hasDiffuseMap;
uniform sampler2D diffuseMap;

void main(void) {
	vec3 diff = gl_LightSource[0].position.xyz - vertex;
	float dist = length(diff);
	vec3 dir = normalize(diff);

	vec3 clrMult = vec3(1.0, 1.0, 1.0);
	if(hasDiffuseMap)
		clrMult *= texture2D(diffuseMap, tCoord).rgb;
	clrMult *= gl_LightSource[0].diffuse.a * gl_LightSource[0].diffuse.rgb * dot(dir, normal) / dist;

//	clrMult = vec3(1.0, 1.0, 1.0);

	gl_FragColor = vec4(color.rgb * clrMult, 1);
}