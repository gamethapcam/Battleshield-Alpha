#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

#define PI 3.1415926535897932384626433832795

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform int u_angleIncreaseDirection;
uniform float u_angle;
uniform float u_radius;
uniform float u_innerRadiusRatio;

uniform float u_regionCenterX;
uniform float u_regionCenterY;

void main() {
    vec4 color;

    float currentAngle;
    if (u_angleIncreaseDirection == 0)
        currentAngle = atan(v_texCoords.y - u_regionCenterY, u_regionCenterX - v_texCoords.x);
    else
        currentAngle = atan(u_regionCenterX - v_texCoords.x, v_texCoords.y - u_regionCenterY);

    float r;
    if (u_innerRadiusRatio > 0.0) {
        vec2 texCoordPolar = vec2(v_texCoords.x - u_regionCenterX, u_regionCenterY - v_texCoords.y);
        r = sqrt(texCoordPolar.x*texCoordPolar.x + texCoordPolar.y*texCoordPolar.y);
    }

    if (currentAngle + PI > u_angle || r < u_innerRadiusRatio*u_radius) color = vec4(1.0, 1.0, 1.0, 0.0);
    else color = texture2D(u_texture, v_texCoords);

    /*vec2 POS = vec2(0.5, 0.3);
    vec2 pos = vec2(POS.x+0.5, 1.0-(POS.y+0.5));
    vec2 pointR = vec2(0.025);

    if (v_texCoords.x >= pos.x - pointR.x &&
        v_texCoords.x <= pos.x + pointR.x &&
        v_texCoords.y >= pos.y - pointR.y &&
        v_texCoords.y <= pos.y + pointR.y)

        color = vec4(1, 0, 0, 1);
    else
        color = texture2D(u_texture, v_texCoords);*/

    gl_FragColor = v_color * color;
}