#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {

    vec2 offset = vec2(sin(v_texCoords.y*1000.0)/300.0, 0.0);

    gl_FragColor = v_color * texture2D(u_texture, v_texCoords + offset);
}