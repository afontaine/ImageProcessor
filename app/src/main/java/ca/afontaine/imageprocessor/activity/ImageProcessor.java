package ca.afontaine.imageprocessor.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import ca.afontaine.imageprocessor.app.*;
import ca.afontaine.imageprocessor.effect.EffectTask;
import ca.afontaine.imageprocessor.effect.WaveEffect;
import ca.afontaine.imageprocessor.filter.Filter;
import ca.afontaine.imageprocessor.filter.FilterTask;
import ca.afontaine.imageprocessor.filter.MeanFilter;
import ca.afontaine.imageprocessor.filter.MedianFilter;
import ca.afontaine.imageprocessor.ui.OddNumberTextWatcher;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ImageProcessor extends Activity {

    private static final String TAG = "ImageProcessor";
    private static final int GET_IMAGE = 1;
	private static final int GET_CAMERA = 2;

	private Uri newImage;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processor);
        image = (ImageView) findViewById(R.id.imageView);
	    if(getIntent().getData() != null) {
		    image.setImageURI(getIntent().getData());
	    }

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
	        case R.id.camera:
		        setCamera();
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

	private void setCamera() {
		Log.d(TAG, "User is taking new image.");
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File picDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"ImageProcessor");
		if(!picDir.exists()) picDir.mkdirs();
		File pic = new File(picDir.getPath(),File.separator +
				"Image_" + new SimpleDateFormat("yyyy-MM-ddHH_mm_ss").format(Calendar.getInstance().getTime()) +
				".jpeg");
		newImage = Uri.fromFile(pic);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, newImage);
		if(intent.resolveActivity(getPackageManager()) != null)
			startActivityForResult(intent, GET_CAMERA);
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
        }
	    if(requestCode == GET_CAMERA) {
		    Log.d(TAG, "User took new photo");
		    if(newImage != null)
			    image.setImageURI(newImage);
	    }
    }

    private void chooseFilter() {
        Log.d(TAG, "Ask for filter type.");
        if(image.getDrawable() == null) {
            Toast.makeText(this, "You must choose an image before applying a filter", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.filter_dialog)
                .setItems(R.array.pref_list_types, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] values = getResources().getStringArray(R.array.pref_list_values);
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
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
        builder.setTitle(R.string.filter_size)
                .setView(inflater.inflate(R.layout.dialog_filter_size, null))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Dialog dialog = (Dialog) dialogInterface;
                        EditText text = (EditText) dialog.findViewById(R.id.filter_size);
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplication());
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
        size.setText(Integer.toString(PreferenceManager.getDefaultSharedPreferences(this).getInt("filter_size", 1)));
        size.addTextChangedListener(new OddNumberTextWatcher(dialog));
    }

    public void applyFilter() {
        Log.d(TAG, "Apply filter");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Filter filter = pref.getString("filter_list", "0").equals("0") ? new MeanFilter() : new MedianFilter();
        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
        (new FilterTask(image, filter, pref.getInt("filter_size", 1), this)).execute(drawable.getBitmap());
    }

}
