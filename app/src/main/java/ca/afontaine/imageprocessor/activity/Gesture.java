/*
 * Copyright (c) 2015 Andrew Fontaine
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ca.afontaine.imageprocessor.activity;

import android.content.Context;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Fontaine
 * @version 1.0
 * @since 2015-02-08
 */
public class Gesture implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{

	private static final String TAG = "Gesture";
	private static final int VIBE = 200;

	private List<OnGestureListener> listeners;
	private Vibrator vibe;

	public Gesture(Context ctx) {
		listeners = new ArrayList<OnGestureListener>();
		vibe = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
	}

	public void addListener(OnGestureListener listner) {
		listeners.add(listner);
	}

	public void removeListener(OnGestureListener listener) {
		listeners.remove(listener);
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		vibe.vibrate(VIBE);
		for(OnGestureListener listener : listeners) {
			listener.onDoublePress();
		}
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		vibe.vibrate(VIBE);
		for(OnGestureListener listener : listeners) {
			listener.onLongPress();
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		vibe.vibrate(VIBE);
		for(OnGestureListener listener : listeners) {
			listener.onFling();
		}
		return true;
	}
}
