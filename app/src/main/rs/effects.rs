#pragma version(1)
#pragma rs java_package_name(ca.afontaine.imageprocessor.rs)
#include "rs_core_math.rsh"
#include "rs_debug.rsh"

const uchar4* input;
int width, height;

static uchar4 getPixelAt(int, int);
void setPixelAt(int, int, uchar4);

/**
 * Adapted from code available at
 * http://popscan.blogspot.ca/2012/04/fisheye-lens-equation-simple-fisheye.html
 */
uchar4 __attribute__((kernel)) mesheye(uchar4 in, int x, int y) {
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

//a convenience method to clamp getting pixels into the image
static uchar4 getPixelAt(int x, int y) {
	if(y>=height) y = height-1;
	if(y<0) y = 0;
	if(x>=width) x = width-1;
	if(x<0) x = 0;
	return input[y*width + x];
}
