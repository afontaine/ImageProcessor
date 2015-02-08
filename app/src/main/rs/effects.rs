#pragma version(1)
#pragma rs java_package_name(ca.afontaine.imageprocessor.rs)
#include "rs_core_math.rsh"
#include "rs_debug.rsh"

const uchar4* input;
uchar4* output;
int width, height;

static uchar4 getPixelAt(int, int);
void setPixelAt(int, int, uchar4);
void mesheye();

/**
 * Adapted from code available at
 * http://popscan.blogspot.ca/2012/04/fisheye-lens-equation-simple-fisheye.html
 */
void mesheye() {
	float nx, ny, r, nr, theta, nnx, nny;
	int x, y, i, j, i2, j2;
	rsDebug("Height of image is: ", height);
	rsDebug("Width of image is: ", width);

	for(j = 0; j < height; j++) {
		ny = (((2.0 * j) / height) - 1.0);
		for(i = 0; i < width; i++) {
			nx = (((2.0 * i) / width) - 1.0);
			rsDebug("nx and ny are: ", nx, ny);
			r = sqrt(ny * ny + nx * nx);
			rsDebug("Distance to pixel is: ", r);
			if(0.0 <= r && r <= 1.0) {
				nr = sqrt((float) (1.0 - r * r));
				nr = (r + (1.0 - nr) / 2.0);
				if(nr <= 1.0) {
					theta = atan2(ny, nx);
					nnx = nr * cos(theta);
					nny = nr * sin(theta);
					i2 = (int) (((nnx + 1) * width) / 2.0);
					j2 = (int) (((nny + 1) * height) / 2.0);
					setPixelAt(i, j, getPixelAt(i2, j2));
				}
			}
		}
	}
	rsDebug("DONE", 0);
}

//a convenience method to clamp getting pixels into the image
static uchar4 getPixelAt(int x, int y) {
	if(y>=height) y = height-1;
	if(y<0) y = 0;
	if(x>=width) x = width-1;
	if(x<0) x = 0;
	return input[y*width + x];
}

//take care of setting x,y on the 1d-array representing the bitmap
void setPixelAt(int x, int y, uchar4 pixel) {
	output[y*width + x] = pixel;
}
