//#version 100
#define kernel_size 0.0025
#define p 1.1
//#define scaleFactor 1/p
#define scaleFactor 0.3

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform int widthInPixelCoordinates;
uniform int heightInPixelCoordinates;
uniform float warpStretchFactor;
/*uniform float multFinalVal;
uniform float multInitialVal;*/

vec4 blueGlow(vec2 pointUV, float c) {
    /*vec4 currentColor = texture2D(u_texture, pointUV);
    if (currentColor != vec4(1, 1, 1, 1))
        return currentColor;*/

    //vec4 glow_color = vec4(0, 135.0/255.0, 189.0/255.0, 1);
    vec4 glow_color = vec4(1, 1, 1, 1);

    float currentPixelX = pointUV.x * float(widthInPixelCoordinates);
    float currentPixelY = pointUV.y * float(heightInPixelCoordinates);
    float kernelSizePixelCoordinates = kernel_size*2.0*float(heightInPixelCoordinates)*c;

    vec4 sum = vec4(0.0);
    /*for (int x = -kernelSizePixelCoordinates; x < kernelSizePixelCoordinates; x++) {
        for (int y = -kernelSizePixelCoordinates; y < kernelSizePixelCoordinates; y++) {

            vec2 currentPixelUV = vec2(float(currentPixelX+x) / widthInPixelCoordinates, float(currentPixelY+y) / heightInPixelCoordinates);
            sum += texture2D(u_texture, currentPixelUV);

        }
    }
    vec4 avg = sum / (kernelSizePixelCoordinates*2*kernelSizePixelCoordinates*2);*/

    for (float i = -kernelSizePixelCoordinates; i < kernelSizePixelCoordinates; i++) {
        vec2 currentPixelUV_Horizontal = vec2((currentPixelX+i) / float(widthInPixelCoordinates), currentPixelY / float(heightInPixelCoordinates));
        vec2 currentPixelUV_Vertical = vec2(currentPixelX / float(widthInPixelCoordinates), (currentPixelY+i) / float(heightInPixelCoordinates));
        sum += texture2D(u_texture, currentPixelUV_Horizontal);
        sum += texture2D(u_texture, currentPixelUV_Vertical);
    }

    vec4 avg = sum / (kernelSizePixelCoordinates*2.0/**2.0*/);

    return avg/* * glow_color*/;
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

void main() {

    float c = warpStretchFactor/*(warpVelocityMultiplier-multInitialVal)/(multFinalVal-multInitialVal)*/;

    vec2 newPointUV = stretch(c);
    vec4 color = texture2D(u_texture, /*v_texCoords*/newPointUV)/*vec4(1, 0, 0, 1)*/;

    vec4 glow = /*blueGlow(newPointUV, 1.0)*/vec4(0, 0, 0, 0);

    //float dumbdumb = warpVelocityMultiplier+widthInPixelCoordinates+heightInPixelCoordinates;

    gl_FragColor = v_color * color + glow;
}


