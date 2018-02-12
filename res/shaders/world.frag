#version 110

varying vec4 color;
varying vec3 normal;
varying vec3 vertex;
varying vec2 tCoord;

uniform bool hasDiffuseMap;
uniform sampler2D diffuseMap;

uniform samplerCube shadowMap;
uniform float shadowMapSize;

vec3 fix_cube_lookup(vec3 v) {
	float M = max(max(abs(v.x), abs(v.y)), abs(v.z));
	float scale = (shadowMapSize - 0.5) / shadowMapSize;
	if (abs(v.x) != M) v.x *= scale;
	if (abs(v.y) != M) v.y *= scale;
	if (abs(v.z) != M) v.z *= scale;
	return v;
}

vec3 shift_cubemap(vec3 v, vec2 amt) {
	float M = max(max(abs(v.x), abs(v.y)), abs(v.z));
	float scale = (shadowMapSize - 0.5) / shadowMapSize;
	if (abs(v.x) == M) return vec3(v.x, v.yz + amt);
	if (abs(v.y) == M) return vec3(v.x + amt.x, v.y, v.z + amt.y);
	if (abs(v.z) == M) return vec3(v.xy + amt, v.z);
	return v;
}

void main(void) {
	vec3 diff = gl_LightSource[0].position.xyz - vertex;
	float dist = length(diff);
	vec3 dir = normalize(diff);

	vec3 clrMult = vec3(1.0, 1.0, 1.0);
//	if(hasDiffuseMap)
//		clrMult *= texture2D(diffuseMap, tCoord).rgb;
	float brt = clamp(dot(dir, normal), 0.0, 1.0);
	float sun = clamp(abs(dot(normalize(vec3(1.0, 0.6, 0.3)), normal)), 0.0, 1.0);
	clrMult *= (gl_LightSource[0].diffuse.rgb * brt / sqrt(dist) * 5.0);

	float tot = 0.0;
	float sum = 0.0;
	for(float i = -1.0; i <= 1.0; i += 0.5) {
		for(float j = -1.0; j <= 1.0; j += 0.5) {
			vec3 vd = -normalize(diff);
			vd = shift_cubemap(vd, vec2(i, j) * 0.002);
			if(dist / 1000.0 - (textureCube(shadowMap, fix_cube_lookup(vd)).r) < 0.0001) {
				sum += 1.0;
			}
			tot += 1.0;
		}
	}

	clrMult *= sum / tot;

	gl_FragColor = vec4(color.rgb * (clrMult * sum / tot / 2.0 + 0.4 + 0.1 * sun), 1);
}