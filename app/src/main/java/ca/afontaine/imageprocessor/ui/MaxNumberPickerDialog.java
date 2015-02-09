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

package ca.afontaine.imageprocessor.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import ca.afontaine.imageprocessor.app.R;

/**
 * @author Andrew Fontaine
 * @version 1.0
 * @since 2015-02-08
 */
public class MaxNumberPickerDialog extends NumberPickerDialog {

	public MaxNumberPickerDialog(Context ctx, AttributeSet attr) {
		super(ctx,attr);
		setTitle(R.string.pref_title_undo_size);
	}

	@Override
	protected void showDialog(Bundle state) {
		super.showDialog(state);
		size.addTextChangedListener(new MaxNumberTextWatcher((AlertDialog) getDialog()));
	}
}
