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
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

/**
 * @author Andrew Fontaine
 * @version 1.0
 * @since 2015-02-08
 */
public class MaxNumberTextWatcher implements TextWatcher {
	private final AlertDialog dialog;

	public MaxNumberTextWatcher(AlertDialog dialog) {
		this.dialog = dialog;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		try {
			int size = Integer.parseInt(s.toString());
			if(size > 3) {
				Toast.makeText(dialog.getContext(), "Size must be between 1 and 3.", Toast.LENGTH_SHORT).show();
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
				return;
			}
			if(size < 1) {
				Toast.makeText(dialog.getContext(), "Size must be between 1 and 3.", Toast.LENGTH_SHORT).show();
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
				return;
			}
		}
		catch(NumberFormatException e) {
			dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
			if(s.toString().isEmpty()) {
				return;
			}
			Toast.makeText(dialog.getContext(), "Size must be a number.", Toast.LENGTH_SHORT).show();
			return;
		}
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
	}
}
