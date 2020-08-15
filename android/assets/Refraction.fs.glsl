#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

#define MAX_REFRACTION 1.

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform sampler2D u_refractionCrackMap;

uniform bool u_flip;

/*uniform float u_c, u_s;
uniform float u_A, u_B, u_C;*/

vec3 screenBlendMode(vec3 a, vec3 b) {
    return 1. - (1. - a) * (1. - b);
}

void main() {

    float x = v_texCoords.x;
    float y;
    if (u_flip)
        y = 1.-v_texCoords.y;
    else
        y = v_texCoords.y;

    vec4 refractionCrackColor = texture2D(u_refractionCrackMap, /*v_texCoords*/vec2(x, y));


    //vec2 displacement = mix(vec2(-MAX_REFRACTION), vec2(MAX_REFRACTION), refractionCrackColor.rg);
    //float displacementY = mix(-MAX_REFRACTION, MAX_REFRACTION, refractionCrackColor.g);
    //float displacementY = -refractionCrackColor.g;
    /*if (refractionCrackColor.r > .9) {
        float distanceFromLine = abs(u_A*x + u_B*y + u_C);
        vec2 displacement = vec2(distanceFromLine*(1.-u_c), -distanceFromLine * u_s);
        //vec2 displacement = vec2(0., -distanceFromLine * u_s);
        //vec2 displacement = vec2(distanceFromLine*(1.-u_c), -distanceFromLine * u_s);

        gl_FragColor = v_color * texture2D(u_texture, v_texCoords+displacement);
        return;
    } else {
        gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
    }*/

    float crack = refractionCrackColor.b;
    float crackBlur = refractionCrackColor.a;

    //vec4 outColor = (texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y+displacementY)) + vec4(crackBlur));
    vec4 outColor = vec4(/*crackBlur*/0.);
    if (refractionCrackColor.g < 0.25)
        outColor += (texture2D(u_texture, vec2(x, v_texCoords.y)));
    else if (refractionCrackColor.g < 0.5)
        outColor += (texture2D(u_texture, vec2(x, 1.-v_texCoords.y)));
    else if (refractionCrackColor.g < 0.75)
        outColor += (texture2D(u_texture, vec2(1.-x, v_texCoords.y)));
    else
        outColor += (texture2D(u_texture, vec2(1.-x, 1.-v_texCoords.y)));



    if (crack > 0.)
        outColor += vec4(crack, crack, crack, crack);
        //gl_FragColor = vec4(screenBlendMode(gl_FragColor.rgb, vec3(refractionCrackColor.b, refractionCrackColor.b, refractionCrackColor.b)), 1.);

    gl_FragColor = v_color * outColor;
}