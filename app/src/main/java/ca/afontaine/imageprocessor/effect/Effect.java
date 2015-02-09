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

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import ca.afontaine.imageprocessor.rs.ScriptC_effects;

/**
 * @author Andrew Fontaine
 * @version 1.0
 * @since 2015-02-08
 */
public abstract class Effect {

	Context cxt;
	RenderScript rs;
	ScriptC_effects scr;


	public Effect(Context ctx) {
		this.cxt = ctx;
		rs = RenderScript.create(ctx);
		scr = new ScriptC_effects(rs);
	}


	public Bitmap effect(Bitmap inMap, Bitmap outMap) {
		Allocation in = Allocation.createFromBitmap(rs, inMap);
		Allocation out = Allocation.createTyped(rs, in.getType());
		scr.set_width(inMap.getWidth());
		scr.set_height(inMap.getHeight());
		scr.bind_input(in);
		apply(in, out);
		out.copyTo(outMap);
		scr.destroy();
		rs.destroy();
		return outMap;
	}

	protected abstract void apply(Allocation in, Allocation out);
}
