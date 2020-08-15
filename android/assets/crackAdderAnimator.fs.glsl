#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

#define MAX_REFRACTION 1.

#define LPOINTS_LENGTH 6
#define ABC_LENGTH LPOINTS_LENGTH-1
#define CPOINTS_LENGTH 4
#define CURVE_PARAMETERS_LENGTH CPOINTS_LENGTH/2

#define T_MIN_L 0.0005
#define T_MAX_L 0.001-T_MIN_L
#define T_MIN_C 0.001
#define T_MAX_C 0.004-T_MIN_C
//#define SIN_MULTIPLIER_RANDOM 100000.0
#define RCL_M 150.
#define RCC_M 1044.
#define EXTENSION 0.03

#define TOTAL_FRAMES 6.




varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture; //The "refractionCrackMap"

uniform int u_currentFrame;

uniform bool u_flip;


uniform vec2 u_lPoints[LPOINTS_LENGTH];

uniform float u_smallestAreaSide;

uniform float u_mainA;
uniform float u_mainB;
uniform float u_mainC;

uniform float u_c, u_s; //cos & sin

uniform float u_A[ABC_LENGTH];
uniform float u_B[ABC_LENGTH];
uniform float u_C[ABC_LENGTH];




uniform vec2 u_cPoints[CPOINTS_LENGTH];

uniform float u_a2[CURVE_PARAMETERS_LENGTH];
uniform float u_a1[CURVE_PARAMETERS_LENGTH];
uniform float u_a0[CURVE_PARAMETERS_LENGTH];

uniform float u_C0[CURVE_PARAMETERS_LENGTH];
uniform float u_C1[CURVE_PARAMETERS_LENGTH];
uniform float u_C2[CURVE_PARAMETERS_LENGTH];
uniform float u_C3[CURVE_PARAMETERS_LENGTH];
uniform float u_C4[CURVE_PARAMETERS_LENGTH];


/**
Traditional lerp does the following: You enter the starting value "s", the ending value "e" and the progress "p". Then the function returns the value "v".
This inverseLerp does the oposite. You give it the value "v" as wll as the starting value "s", the ending value "e". Then it returns the progress "p". 0 <= p <= 1.
*/
float inverseLerp(float s, float e, float v) {
    return (v-s)/(e-s);
}

float sech(float x) {
    return 2./(exp(x) + exp(-x));
}

float rand0(float x) {
    return fract(sin(30.0*x)*30.0);
}

/*float rand(float x) {
    return fract(sin(x)*SIN_MULTIPLIER_RANDOM);
    //return sin(x);
}*/

float rand(float x) {
    return rand0(rand0(x));
}

float noise(float x, float m, float initial) {
    float _x = m*x + initial;
    float i = floor(_x);// integer
    float f = fract(_x);// fraction
    return mix(rand(i), rand(i + 1.), smoothstep(0., 1., f));
}

float noiseRC(float x, float m, float initial) {
    float _x = m*x + initial;
    float i = floor(_x);// integer
    float f = fract(_x);// fraction

    return mix(pow(rand(i), .4), pow(rand(i + 2.), .4), smoothstep(0., 1., f));
}

/*float linePerpendicularDistance(float _A, float _B, float _C, float _x, float _y) {
    return _A*_x + _B*_y + _C;
}*/

/**
returns a the smallest distance in absolute value with its sign.
*/
/*float parabolaDistanceAlongPerpendicularLine(float _C0, float _C1, float _C2, float _C3, float _C4, float _x, float _y) {

}*/


bool insideBounds(vec2 p0, vec2 p1, float x, float y, float t_max) {
    float xLeft, xRight;
    if (p0.x < p1.x) {
        xLeft = p0.x;
        xRight = p1.x;
    } else {
        xLeft = p1.x;
        xRight = p0.x;
    }

    if (x <= xLeft - t_max) return false;
    else if (x >= xRight + t_max) return false;

    float yDown, yUp;
    if (p0.y < p1.y) {
        yDown = p0.y;
        yUp = p1.y;
    } else {
        yDown = p1.y;
        yUp = p0.y;
    }

    if (y <= yDown - t_max) return false;
    else if (y >= yUp + t_max) return false;

    return true;
}

bool insideHorizontalBounds(vec2 p0, vec2 p1, float y, float t_max) {
    float yDown, yUp;
    if (p0.y < p1.y) {
        yDown = p0.y;
        yUp = p1.y;
    } else {
        yDown = p1.y;
        yUp = p0.y;
    }

    if (y <= yDown /*- t_max*/) return false;
    else if (y >= yUp/* + t_max*/) return false;

    return true;
}

bool insideVerticalBounds(vec2 p0, vec2 p1, float x, float t_max) {
    float xLeft, xRight;
    if (p0.x < p1.x) {
        xLeft = p0.x;
        xRight = p1.x;
    } else {
        xLeft = p1.x;
        xRight = p0.x;
    }

    if (x <= xLeft - t_max) return false;
    else if (x >= xRight + t_max) return false;

    return true;
}




void main() {

    //////////////////////////////////////// ////////////////////////////////////////


    vec4 presentColor = texture2D(u_texture, v_texCoords);

    vec4 outColor = vec4(0., presentColor.g, 0., 0.);
    //vec4 outColor = vec4(0., presentColor.g, 0., 1.);

    float x = v_texCoords.x;
    float y;
    if (u_flip)
        y = 1.-v_texCoords.y;
    else
        y = v_texCoords.y;


    //////////////////////////////////////// lines & thier refraction ////////////////////////////////////////



    float linePerpendicularDistance = u_A[u_currentFrame]*x + u_B[u_currentFrame]*y + u_C[u_currentFrame];

    float thickness = T_MIN_L + noise(u_c*x+u_s*y, 10., 7.)*T_MAX_L;

    if (insideHorizontalBounds(u_lPoints[u_currentFrame], u_lPoints[u_currentFrame+1], y, T_MAX_L)) {

        if (sign(-linePerpendicularDistance) * u_smallestAreaSide >= 0.) {
            //outColor.g = inverseLerp(-MAX_REFRACTION, MAX_REFRACTION, abs(mainLinePerpendicularDistance) * u_s * -sign(u_c)) - 0.5;
            //outColor.g = abs(mainLinePerpendicularDistance) /** u_s*/* .2 * sign(u_c) * .5 /*.05*/;
            //outColor.g = -(1. - 2.*y);

            if (presentColor.g < .25) {
                outColor.g = .3;
            } else if (presentColor.g < .5) {
                outColor.g = 0.6;
            } else if (presentColor.g < .75)
                outColor.g = 0.9;
            else
                outColor.g = 0.1;
        }

        if (insideVerticalBounds(u_lPoints[u_currentFrame], u_lPoints[u_currentFrame+1], x, T_MAX_L)) {
            if (abs(linePerpendicularDistance) <= thickness) {
                float rcl = noiseRC(u_c*x+u_s*y, RCL_M, 70.)/*1.*/;
                outColor.b = rcl/*0.*/;
            }


        }

    }




    //////////////////////////////////////// Parabolas ////////////////////////////////////////

    int index = -1;
    if (u_currentFrame == 4) {
        index = 1;
    } else if (u_currentFrame == 0) {
        index = 0;
    }

    if (index >= 0 && insideBounds(u_cPoints[index], u_cPoints[index+2], x, y, T_MAX_C)) {

        float mainLinePerpendicularDistance = u_mainA*x + u_mainB*y + u_mainC;

        float _a2, _a1, _a0, _C4, _C3, _C2, _C1, _C0;

        _a2 = u_a2[index];
        _a1 = u_a1[index];
        _a0 = u_a0[index];
        _C4 = u_C4[index];
        _C3 = u_C3[index];
        _C2 = u_C2[index];
        _C1 = u_C1[index];
        _C0 = u_C0[index];


        //float curveSide = sign(-_a2)*(_a2*x*x + _a1*x + _a0 - y); //if (+ve), the point is inside the parabola. And of course outside it otherwise.
        float sqRoot = sqrt(_C3*_C3 - 2.*_C0*(_C4-(u_c*x+u_s*y)));
        float d0 = abs(_C1*(_C3 + _C0*x + sqRoot));
        float d1 = abs(_C1*(_C3 + _C0*x - sqRoot));
        float curveDistanceAlongPerpendicularLine = min(d0, d1);

        thickness = T_MIN_C + noise(u_c*x+u_s*y, RCC_M, 7.)*T_MAX_C;


        //if (curveSide * linePerpendicularDistance <= 0.) { // between the curve and the line
        //if (curveSide * u_smallestAreaSide >= 0.) { // between the curve and the line
        float totalDistanceBetweenCurveAndLine = abs(curveDistanceAlongPerpendicularLine) + abs(linePerpendicularDistance);


        //float ratio;
        //if (curveDistanceAlongPerpendicularLine > linePerpendicularDistance)
        //ratio = curveDistanceAlongPerpendicularLine/linePerpendicularDistance;
        //else
        //ratio = linePerpendicularDistance/curveDistanceAlongPerpendicularLine;


        float c_t = thickness / (totalDistanceBetweenCurveAndLine*150.);
        float rcc = noise(u_c*x+u_s*y, RCC_M/(200.*c_t), 70.);
        if (curveDistanceAlongPerpendicularLine <= c_t) {

            // Uncomment to make the curve work.
            // Uncomment to make the curve work.
            // Uncomment to make the curve work.
            // Uncomment to make the curve work.
            // Uncomment to make the curve work.
            // Uncomment to make the curve work.
            // Uncomment to make the curve work.
            // Uncomment to make the curve work.

            //outColor.b = rcc; // Uncomment to make the curve work.

        }

    }











    //////////////////////////////////////////////////////////////////////////////////////////////////



    /*float bloomFactor = float(u_currentFrame)/(TOTAL_FRAMES-2.);
    outColor.a = bloomFactor * sech((bloomFactor)*40.*linePerpendicularDistance);*/




    /*if (drawLine(u_currentFrame))
        //Line is drawn
        return;

    if (drawCurve(u_currentFrame))
        //Curve is drawn
        return;*/

    //vec4 dummy = texture2D(u_texture, v_texCoords);

    //gl_FragColor = v_color * (texture2D(u_texture, v_texCoords) + outColor);
    gl_FragColor = v_color * vec4(outColor.r, outColor.g, presentColor.b+outColor.b, presentColor.a+outColor.a);
    //gl_FragColor = v_color * vec4(.5, outColor.g, outColor.b, 1.);
}
