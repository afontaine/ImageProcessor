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

	// if(relX != 0) {
	// 	angle = atan2(relY, relX);
	// 	rsDebug("atan2 angle is: ", angle);
	// 	if(relX > 0 && relY < 0) { angle = 2.0f * M_PI - angle; }
	// 	else if(relX <= 0 && relY >= 0) { angle = M_PI - angle; }
	// 	else if(relX <= 0 && relY < 0) { angle += M_PI; }
	// }
	// else {
	// 	if(relY >= 0) angle = 0.5f * M_PI;
	// 	else angle = 1.5f * M_PI;
	// }


	float radius = sqrt(relX * relX + relY * relY);
	float new_angle = angle + radius / (pow(0.5, radius) + 1) * 0.03;
	int srcX = (int) (floor(radius * cos(new_angle) + 0.5f));
	int srcY = (int) (floor(radius * sin(new_angle) + 0.5f));
	srcX += cX;
	srcY += cY;
	srcY = height - srcY;
	return getPixelAt(srcX, srcY);
}

//a convenience method to clamp getting pixels into the image
static uchar4 getPixelAt(int x, int y) {
	if(y>=height) y = height-1;
	if(y<0) y = 0;
	if(x>=width) x = width-1;
	if(x<0) x = 0;
	return input[y*width + x];
}
