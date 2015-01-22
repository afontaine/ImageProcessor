package ca.afontaine.imageprocessor.app;

import android.graphics.Bitmap;

/**
 * @author Andrew Fontaine
 * @version 1.0
 * @since 2015-01-21
 */
public class MeanFilter implements Filter {

    public int filter(int[] pixels) {
        int mean = 0;
        for(int i : pixels)
            mean += i;
        return mean/pixels.length;
    }
}
