#define PI 3.1415926
#define scaleFactor 0.25
#define bloomBightness 3.0

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 worldSizeInPixelCoordinates;

uniform sampler2D u_textureBlurred;

uniform float warpStretchFactor;

vec2 stretch(float);

void main() {

    vec2 pixelSize = 1.0/worldSizeInPixelCoordinates;

    vec2 newPoint = stretch(warpStretchFactor);

    vec4 blurFinal = /*vBlur(hBlurredTexturePixelSize)*/texture2D(u_textureBlurred, newPoint);



    gl_FragColor = v_color * texture2D(u_texture, newPoint) + blurFinal * bloomBightness;
}

vec2 stretch(float c) {
    vec2 pointRelativeToCentre = (v_texCoords-0.5)*2.0;
    float radius = sqrt(pow(pointRelativeToCentre.x, 2.0) + pow(pointRelativeToCentre.y, 2.0));
    float s = radius/abs(radius);
    //float power = 1 - c * (1-p);
    //float scale = 1 - c * (1-scaleFactor);
    //float newRadius = s*pow(s*radius*scale, power);
    float scale = c*scaleFactor;
    float newRadius = radius - scale * radius*radius;
    vec2 newPointRelativeToCentre = newRadius * vec2(pointRelativeToCentre.x/radius, pointRelativeToCentre.y/radius);//newRadius * (cos(theta), sin(theta));
    vec2 newPointUV = newPointRelativeToCentre/2.0 + 0.5;

    /*vec2 pointRelativeToCentre = (v_texCoords-0.5)*2;
    vec2 s = pointRelativeToCentre/abs(pointRelativeToCentre); // sign
    float c = (warpVelocityMultiplier-multInitialVal)/(multFinalVal-multInitialVal);
    float scale = c*scaleFactor;
    vec2 newPointRelativeToCentre = pointRelativeToCentre - scale * pointRelativeToCentre*pointRelativeToCentre*s;
    vec2 newPointUV = newPointRelativeToCentre/2.0 + 0.5;*/


    return newPointUV;
}
