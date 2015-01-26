package ca.afontaine.imageprocessor.app;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Andrew Fontaine
 * @version 1.0
 * @since 2015-01-21
 */
public class MedianFilter implements Filter {

    public int filter(int[] pixels) {
        List<Integer> red = new ArrayList<>();
        List<Integer> green = new ArrayList<>();
        List<Integer> blue = new ArrayList<>();
        List<Integer> alpha = new ArrayList<>();
        for(int i : pixels) {
            red.add(Color.red(i));
            blue.add(Color.blue(i));
            green.add(Color.green(i));
            alpha.add(Color.alpha(i));
        }
        Collections.sort(red);
        Collections.sort(blue);
        Collections.sort(green);
        Collections.sort(alpha);
        return Color.argb(alpha.get(alpha.size() / 2 + 1), red.get(red.size() / 2 + 1), green.get(green.size() / 2 + 1),
                blue.get(blue.size() / 2 + 1));
    }
}
