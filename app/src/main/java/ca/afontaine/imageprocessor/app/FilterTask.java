package ca.afontaine.imageprocessor.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * @author Andrew Fontaine
 * @version 1.0
 * @since 2015-01-21
 */
public class FilterTask extends AsyncTask<Bitmap, Integer, Bitmap> {

    private ImageView image;
    private Filter filter;
    private int filterSize;
    private Context ctx;
    private ProgressDialog pd;

    public FilterTask(ImageView image, Filter filter, int size, Context context) {
        super();
        this.image = image;
        this.filter = filter;
        this.filterSize = size;
        ctx = context;
    }

    @Override
    protected Bitmap doInBackground(Bitmap... params) {
        Bitmap image = params[0];
        Bitmap newMap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        int[] pixels = new int[filterSize * filterSize];

        for(int i = filterSize / 2 + 1 ; i < image.getWidth() - (filterSize / 2); i++) {
            for(int j = filterSize / 2 + 1; j < image.getHeight() - (filterSize / 2); j++) {
                image.getPixels(pixels, 0, filterSize, i - (filterSize / 2 + 1), j - (filterSize / 2 + 1), filterSize, filterSize);
                newMap.setPixel(i, j, filter.filter(pixels));
            }
            publishProgress(i, image.getWidth());
            if(isCancelled())
                return null;
        }
        return newMap;
    }

    protected void onPreExecute() {
        final FilterTask task = this;
        pd = new ProgressDialog(ctx);
        pd.setMessage("Filtering...");
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                task.cancel(false);
            }
        });
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCancelable(true);
        pd.show();
    }


    protected void onPostExecute(Bitmap images) {
        image.setImageBitmap(images);
        pd.dismiss();
    }

    protected void onProgressUpdate(Integer... progress) {
        pd.setProgress(progress[0]);
        pd.setMax(progress[1]);
    }
}
