#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

#define MAX_PORTAL_POINTS 3


varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

uniform mat3 u_transToWorldCoordsMat;
uniform mat3 u_transToWorldCoordsMatInv;
uniform vec2 u_worldSize;

uniform float u_portalRadius;
uniform float u_portalPointsWorldCoordinatesX[MAX_PORTAL_POINTS];
uniform float u_portalPointsWorldCoordinatesY[MAX_PORTAL_POINTS];
uniform float u_portalPointsIntensities[MAX_PORTAL_POINTS];

void main() {


    vec3 worldCoords = u_transToWorldCoordsMat * vec3(v_texCoords, 1.0);
    vec2 currentPoint = vec2(worldCoords.xy);


    for (int i = 0; i < MAX_PORTAL_POINTS; i++) {
        vec2 portalPoint = vec2(u_portalPointsWorldCoordinatesX[i], u_portalPointsWorldCoordinatesY[i]);

        if (portalPoint.x < 0.0)
            continue;

        if (currentPoint.x > portalPoint.x + u_portalRadius)
            continue;

        if (currentPoint.x < portalPoint.x - u_portalRadius)
            continue;

        if (currentPoint.y > portalPoint.y + u_portalRadius)
            continue;

        if (currentPoint.y < portalPoint.y - u_portalRadius)
            continue;

        float dist = distance(portalPoint, currentPoint);
        if (dist > u_portalRadius)
            continue;

        vec2 portalPointToCurrentPoint = currentPoint - portalPoint;
        //float c = pow(u_portalRadius - dist, 2.0) / 30.0 * u_portalPointsIntensities[i];
        float c = (u_portalRadius - sqrt(u_portalRadius*u_portalRadius - (dist-u_portalRadius)*(dist-u_portalRadius))) * u_portalPointsIntensities[i];

        float cr = c / 3.0;
        vec2 warpedPointR = currentPoint + cr*portalPointToCurrentPoint;
        vec3 warpedPointTexCoordsR = u_transToWorldCoordsMatInv * vec3(warpedPointR.xy, 1);
        float r = texture2D(u_texture, warpedPointTexCoordsR.xy).r;

        float cg = c / 2.7;
        vec2 warpedPointG = currentPoint + cg*portalPointToCurrentPoint;
        vec3 warpedPointTexCoordsG = u_transToWorldCoordsMatInv * vec3(warpedPointG.xy, 1);
        float g = texture2D(u_texture, warpedPointTexCoordsG.xy).g;

        float cb = c / 2.4;
        vec2 warpedPointB = currentPoint + cb*portalPointToCurrentPoint;
        vec3 warpedPointTexCoordsB = u_transToWorldCoordsMatInv * vec3(warpedPointB.xy, 1);
        float b = texture2D(u_texture, warpedPointTexCoordsB.xy).b;


        gl_FragColor = v_color * vec4(r, g, b, 1.0);


        /*if (i == 0)
            gl_FragColor = v_color * texture2D(u_texture, v_texCoords) + vec4(0.0, 1.0, 0.0, 1.0);
        else if  (i == 1)
            gl_FragColor = v_color * texture2D(u_texture, v_texCoords) + vec4(1.0, 0.0, 0.0, 1.0);
        else
            gl_FragColor = v_color * texture2D(u_texture, v_texCoords) + vec4(0.0, 0.0, 1.0, 1.0);*/


        return;
    }


    /*if (texCoordsWorldCoords.x < u_worldSize.x/2.0 && texCoordsWorldCoords.y < u_worldSize.y/2.0) {
        gl_FragColor = vec4(1.0, 0, 0, 1.0) * texture2D(u_texture, v_texCoords);
        return;
    }*/


    gl_FragColor = v_color * texture2D(u_texture, v_texCoords);

}
