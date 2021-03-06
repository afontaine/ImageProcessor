#pragma version(1)
#pragma rs java_package_name(ca.afontaine.imageprocessor.rs)
#include "rs_types.rsh"
#include "rs_core_math.rsh"
#include "rs_debug.rsh"

const uchar4* input;
int width, height;
float factor;

static uchar4 getPixelAt(int, int);
void setPixelAt(int, int, uchar4);

/**
 * Adapted from code available at
 * http://popscan.blogspot.ca/2012/04/fisheye-lens-equation-simple-fisheye.html
 */
uchar4 __attribute__((kernel)) fisheye(uchar4 in, int x, int y) {
	float ny = (((2.0 * y) / height) - 1.0);
	float nx = (((2.0 * x) / width) - 1.0);
	float r = sqrt(ny * ny + nx * nx);
	float nr = sqrt((float) (1.0 - r * r));
	nr = (r + (1.0 - nr) / 2.0);

	float theta = atan2(ny, nx);
	float nnx = nr * cos(theta);
	float nny = nr * sin(theta);
	int x2 = (int) (((nnx + 1) * width) / 2.0);
	int y2 = (int) (((nny + 1) * height) / 2.0);
	return getPixelAt(x2, y2);
}

uchar4 __attribute__((kernel)) swirl(uchar4 in, int x, int y) {
	float cY = height / 2.0;
	float cX = width / 2.0;
	float relX = cX - x;
	float relY = cY - y;
	float angle = atan2(relY, relX);
	float radius = sqrt(relX * relX + relY * relY);
	float new_angle = angle + radius / (pow(0.5, radius) + 1) * 0.005;
	int srcX = (int) (radius * cos(new_angle) + 0.5f);
	int srcY = (int) (radius * sin(new_angle) + 0.5f);
	srcX += cX;
	srcY += cY;
	srcY = height - srcY;
	return getPixelAt(srcX, srcY);
}

uchar4 __attribute((kernel)) wave(uchar4 in, int x, int y) {
	float x_sine = 2 * M_PI * 3 / height * y;
	float y_sine = 2 * M_PI * 3 / width * x;
	int nX = (width / 10) * sin(x_sine) + x;
	int nY = (height / 10) * sin(y_sine) + y;
	return getPixelAt(nX, nY);
}

//a convenience method to clamp getting pixels into the image
static uchar4 getPixelAt(int x, int y) {
	if(y>=height) y = height-1;
	if(y<0) y = 0;
	if(x>=width) x = width-1;
	if(x<0) x = 0;
	return input[y*width + x];
}
