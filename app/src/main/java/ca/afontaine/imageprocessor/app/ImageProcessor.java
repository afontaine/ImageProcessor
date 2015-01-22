package ca.afontaine.imageprocessor.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class ImageProcessor extends Activity {

    private static final String TAG = "ImageProcessor";
    private static final int GET_IMAGE = 1;

    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processor);

        image = (ImageView) findViewById(R.id.imageView);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_processor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.image:
                setImage();
                break;
            case R.id.action_settings:
                goToSettings();
                break;
            case R.id.action_apply_filter:
                chooseFilter();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setImage() {
        Log.d(TAG, "User is choosing image.");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GET_IMAGE);
    }

    private void goToSettings() {
        Log.d(TAG, "User is going to settings.");
        Intent intent = new Intent();
        intent.setClass(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;

        if(requestCode == GET_IMAGE) {
            Log.d(TAG, "User chose image " + data.getData());
            image.setImageURI(data.getData());
            chooseFilter();
        }
    }

    private void chooseFilter() {
        Log.d(TAG, "Ask for filter type.");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.filter_dialog)
                .setItems(R.array.pref_list_types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] values = getResources().getStringArray(R.array.pref_list_values);
                        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("filter_list", values[which]);
                        editor.apply();
                        chooseFilterSize();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        Log.d(TAG, "Dialog shown");
    }

    private void chooseFilterSize() {
        Log.d(TAG, "Ask for filter size");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle("Filter Size")
                .setView(inflater.inflate(R.layout.dialog_filter_size, null))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Dialog dialog = (Dialog) dialogInterface;
                        EditText text = (EditText) dialog.findViewById(R.id.filter_size);
                        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = pref.edit();
                        edit.putInt("filter_size", Integer.parseInt(text.getText().toString()));
                        edit.apply();
                        applyFilter();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        final AlertDialog dialog = builder.show();
        EditText size = (EditText) dialog.findViewById(R.id.filter_size);
        size.setText(Integer.toString(getPreferences(MODE_PRIVATE).getInt("filter_size", 1)));
        size.addTextChangedListener(new TextWatcher() {
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
                    if(size % 2 == 0) {
                        Toast.makeText(getApplicationContext(), "Size must be odd.", Toast.LENGTH_SHORT).show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        return;
                    }
                }
                catch(NumberFormatException e) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    if(s.toString().isEmpty()) {
                        return;
                    }
                    Toast.makeText(getApplicationContext(), "Size must be a number.", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            }
        });
    }

    public void applyFilter() {
        Log.d(TAG, "Apply filter");
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        Filter filter = pref.getString("filter_list", "1").equals("1") ? new MeanFilter() : new MedianFilter();
        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
        (new FilterTask(filter, pref.getInt("filter_size", 1), this)).execute(drawable.getBitmap());
    }

    /**
     * @author Andrew Fontaine
     * @version 1.0
     * @since 2015-01-21
     */
    public class FilterTask extends AsyncTask<Bitmap, Integer, Bitmap> {

        private Filter filter;
        private int filterSize;
        private Context ctx;
        private ProgressDialog pd;

        public FilterTask(Filter filter, int size, Context context) {
            super();
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
}
