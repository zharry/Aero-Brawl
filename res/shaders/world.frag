#version 110

varying vec4 color;
varying vec3 normal;
varying vec3 vertex;
varying vec2 tCoord;

uniform bool hasDiffuseMap;
uniform sampler2D diffuseMap;

uniform sampler2D shadowMap;

uniform mat4 projection;
uniform mat4 view;

void main(void) {
	vec3 diff = gl_LightSource[0].position.xyz - vertex;
	float dist = length(diff);
	vec3 dir = normalize(diff);

	vec3 clrMult = vec3(1.0, 1.0, 1.0);
//	if(hasDiffuseMap)
//		clrMult *= texture2D(diffuseMap, tCoord).rgb;
	clrMult *= gl_LightSource[0].diffuse.a * gl_LightSource[0].diffuse.rgb * dot(dir, normal) / dist;

	float tot = 0.0;
	float sum = 0.0;
	for(float i = -0.0005; i <= 0.0005; i += 0.0005) {
		for(float j = -0.0005; j <= 0.0005; j += 0.0005) {
			vec4 pos = projection * view * vec4(vertex, 1);
			pos.xyz /= pos.w;
			pos.xy += vec2(1, 1);
			pos.xy /= 2.0;
			if(pos.z - texture2D(shadowMap, pos.xy + vec2(i, j)).x < 0.000001) {
				sum += 1.0;
			}
			tot += 1.0;
		}
	}

	sum /= tot;

	gl_FragColor = vec4(color.rgb * clrMult * (sum / 2.0 + 0.5), 1);
}