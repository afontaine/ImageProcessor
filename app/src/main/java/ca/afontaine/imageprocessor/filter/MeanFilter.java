package ca.afontaine.imageprocessor.filter;

import android.graphics.Color;

/**
 * @author Andrew Fontaine
 * @version 1.0
 * @since 2015-01-21
 */
public class MeanFilter implements Filter {

    public int filter(int[] pixels) {
        int mean_r = 0;
        int mean_g = 0;
        int mean_b = 0;
        int mean_a = 0;
        for(int i : pixels) {
            mean_a += Color.alpha(i);
            mean_b += Color.blue(i);
            mean_g += Color.green(i);
            mean_r += Color.red(i);
        }
        return Color.argb(mean_a/pixels.length, mean_r/pixels.length, mean_g/pixels.length, mean_b/pixels.length);

    }
}
