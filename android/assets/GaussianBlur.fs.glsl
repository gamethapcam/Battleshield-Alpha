#define PI 3.1415926

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 sizeInPixelUnits;

uniform bool horizontalPass;
uniform int kernelSize;

float weight(float x, float c);

void main() {

    vec2 pixelSize = 1.0/sizeInPixelUnits;

    float sigma = float(kernelSize) * 0.137915254237; //This ensures that the blurred image won't be darker than the original.
    float c = 2.0*sigma*sigma;
    float currentWeight = 1.0/sqrt(PI*c);

    float i_max = float(kernelSize)/2.0;

    vec4 sum = texture2D(u_texture, v_texCoords) * currentWeight;

    for (float i = 1.0; i < i_max; i++) {
        
        currentWeight = weight(i, c);

        vec2 uv_right;
        vec2 uv_left;
        if (horizontalPass) {
            uv_right = vec2(v_texCoords.x + i*pixelSize.x, v_texCoords.y);
            uv_left  = vec2(v_texCoords.x - i*pixelSize.x, v_texCoords.y);
        } else {
            uv_right = vec2(v_texCoords.x, v_texCoords.y + i*pixelSize.y);
            uv_left  = vec2(v_texCoords.x, v_texCoords.y - i*pixelSize.y);
        }

        sum += texture2D(u_texture, uv_right) * currentWeight;
        sum += texture2D(u_texture, uv_left) * currentWeight;
    }

    gl_FragColor = sum/*texture2D(u_texture, v_texCoords)*/;
}

float weight(float x, float c) {
    return exp((-x*x)/c)/sqrt(PI*c);
}
