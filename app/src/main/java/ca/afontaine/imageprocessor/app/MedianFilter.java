package ca.afontaine.imageprocessor.app;

import java.util.Arrays;

/**
 * @author Andrew Fontaine
 * @version 1.0
 * @since 2015-01-21
 */
public class MedianFilter implements Filter {

    public int filter(int[] pixels) {
        Arrays.sort(pixels);
        return pixels[pixels.length/2 + 1];
    }
}
