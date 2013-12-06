package ca.jakegreene.painterly.util;

import processing.core.PImage;

//Fast Gaussian Blur v1.3
//by Mario Klingemann <http://incubator.quasimondo.com>

//One of my first steps with Processing. I am a fan
//of blurring. Especially as you can use blurred images
//as a base for other effects. So this is something I
//might get back to in later experiments.
//
//What you see is an attempt to implement a Gaussian Blur algorithm
//which is exact but fast. I think that this one should be
//relatively fast because it uses a special trick by first
//making a horizontal blur on the original image and afterwards
//making a vertical blur on the pre-processed image. This
//is a mathematical correct thing to do and reduces the
//calculation a lot.
//
//In order to avoid the overhead of function calls I unrolled
//the whole convolution routine in one method. This may not
//look nice, but brings a huge performance boost.
//
//
//v1.1: I replaced some multiplications by additions
//    and added aome minor pre-caclulations.
//    Also add correct rounding for float->int conversion
//
//v1.2: I completely got rid of all floating point calculations
//    and speeded up the whole process by using a
//    precalculated multiplication table. Unfortunately
//    a precalculated division table was becoming too
//    huge. But maybe there is some way to even speed
//    up the divisions.
//
//v1.3: Fixed a bug that caused blurs that start at y>0
//	 to go wrong. Thanks to Jeroen Schellekens for 
//    finding it!
public class Convolver {
	int radius;
	int kernelSize;
	int[] kernel;
	int[][] mult;

	public Convolver(int sz) {
		this.setRadius(sz);
	}

	public void setRadius(int sz) {

		int i, j;
		sz = min(max(1, sz), 248);
		if (radius == sz)
			return;
		kernelSize = 1 + sz * 2;
		radius = sz;
		kernel = new int[1 + sz * 2];
		mult = new int[1 + sz * 2][256];

		int sum = 0;
		for (i = 1; i < sz; i++) {
			int szi = sz - i;
			kernel[sz + i] = kernel[szi] = szi * szi;
			sum += kernel[szi] + kernel[szi];
			for (j = 0; j < 256; j++) {
				mult[sz + i][j] = mult[szi][j] = kernel[szi] * j;
			}
		}
		kernel[sz] = sz * sz;
		sum += kernel[sz];
		for (j = 0; j < 256; j++) {
			mult[sz][j] = kernel[sz] * j;
		}
	}

	public void blur(PImage img, int x, int y, int w, int h) {

		int sum, cr, cg, cb;
		int read, i, ri, xl, yl, yi, ym, riw;
		int[] pix = img.pixels;
		int iw = img.width;

		int wh = iw * img.height;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];

		for (i = 0; i < wh; i++) {
			ri = pix[i];
			r[i] = (ri & 0xff0000) >> 16;
			g[i] = (ri & 0x00ff00) >> 8;
			b[i] = (ri & 0x0000ff);
		}

		int r2[] = new int[wh];
		int g2[] = new int[wh];
		int b2[] = new int[wh];

		x = max(0, x);
		y = max(0, y);
		w = x + w - max(0, (x + w) - iw);
		h = y + h - max(0, (y + h) - img.height);
		yi = y * iw;

		for (yl = y; yl < h; yl++) {
			for (xl = x; xl < w; xl++) {
				cb = cg = cr = sum = 0;
				ri = xl - radius;
				for (i = 0; i < kernelSize; i++) {
					read = ri + i;
					if (read >= x && read < w) {
						read += yi;
						cr += mult[i][r[read]];
						cg += mult[i][g[read]];
						cb += mult[i][b[read]];
						sum += kernel[i];
					}
				}
				ri = yi + xl;
				r2[ri] = cr / sum;
				g2[ri] = cg / sum;
				b2[ri] = cb / sum;
			}
			yi += iw;
		}
		yi = y * iw;

		for (yl = y; yl < h; yl++) {
			ym = yl - radius;
			riw = ym * iw;
			for (xl = x; xl < w; xl++) {
				cb = cg = cr = sum = 0;
				ri = ym;
				read = xl + riw;
				for (i = 0; i < kernelSize; i++) {
					if (ri < h && ri >= y) {
						cr += mult[i][r2[read]];
						cg += mult[i][g2[read]];
						cb += mult[i][b2[read]];
						sum += kernel[i];
					}
					ri++;
					read += iw;
				}
				pix[xl + yi] = 0xff000000 | (cr / sum) << 16 | (cg / sum) << 8
						| (cb / sum);
			}
			yi += iw;
		}
	}
	
	private final int max(int a, int b) {
		return (a > b) ? a : b;
	}
	
	private final int min(int a, int b) {
	    return (a < b) ? a : b;
	}
}
