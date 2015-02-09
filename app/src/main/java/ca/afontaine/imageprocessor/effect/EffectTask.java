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

package ca.afontaine.imageprocessor.effect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * @author Andrew Fontaine
 * @version 1.0
 * @since 2015-02-08
 */
public class EffectTask extends AsyncTask<Bitmap, Integer, Bitmap> {
	private ImageView image;
	private Effect effect;
	private Context ctx;
	private ProgressDialog pd;

	public EffectTask(ImageView image, Effect effect, Context ctx) {
		super();
		this.image = image;
		this.effect = effect;
		this.ctx = ctx;
	}

	@Override
	protected Bitmap doInBackground(Bitmap... params) {
		Bitmap image = params[0];
		Bitmap newMap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
		return effect.effect(image, newMap);
	}

	@Override
	protected void onPreExecute() {
		final EffectTask task = this;
		pd = new ProgressDialog(ctx);
		pd.setMessage("Effecting...");
		pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				task.cancel(false);
			}
		});
		pd.setCancelable(true);
		pd.show();
	}

	@Override
	protected void onPostExecute(Bitmap images) {
		image.setImageBitmap(images);
		pd.dismiss();
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		pd.setProgress(progress[0]);
		pd.setMax(progress[1]);
	}
}
