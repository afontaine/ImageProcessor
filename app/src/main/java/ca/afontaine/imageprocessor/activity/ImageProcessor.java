package ca.afontaine.imageprocessor.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import ca.afontaine.imageprocessor.app.R;
import ca.afontaine.imageprocessor.effect.EffectTask;
import ca.afontaine.imageprocessor.effect.FisheyeEffect;
import ca.afontaine.imageprocessor.effect.SwirlEffect;
import ca.afontaine.imageprocessor.effect.WaveEffect;
import ca.afontaine.imageprocessor.filter.Filter;
import ca.afontaine.imageprocessor.filter.FilterTask;
import ca.afontaine.imageprocessor.filter.MeanFilter;
import ca.afontaine.imageprocessor.filter.MedianFilter;
import ca.afontaine.imageprocessor.ui.OddNumberTextWatcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.Deque;


public class ImageProcessor extends Activity implements OnGestureListener {

    private static final String TAG = "ImageProcessor";
    private static final int GET_IMAGE = 1;
	private static final int GET_CAMERA = 2;

	private Uri newImage;
    private ImageView image;
	private Gesture gesture;
	private GestureDetector gestureDetector;
	private Deque<Bitmap> undoStack;
	private Button undo;
	private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processor);
	    gesture = new Gesture(this);
	    gesture.addListener(this);
	    gestureDetector = new GestureDetector(this, gesture);
        image = (ImageView) findViewById(R.id.imageView);
	    image.setOnTouchListener(new View.OnTouchListener() {
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
			    return gestureDetector.onTouchEvent(event);
		    }
	    });
	    undoStack = new ArrayDeque<Bitmap>();
	    if(getIntent().getData() != null) {
		    image.setImageURI(getIntent().getData());
	    }
	    save = (Button) findViewById(R.id.save);
	    save.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			    saveImage();
		    }
	    });
	    save.setEnabled(false);

	    undo = (Button) findViewById(R.id.undo);
	    undo.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			    if(undoStack.peek() != null)
				    image.setImageBitmap(undoStack.removeLast());
			    if(undoStack.peek() == null)
				    undo.setEnabled(false);
		    }
	    });
	    undo.setEnabled(false);

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
	    newImage = null;
        startActivityForResult(intent, GET_IMAGE);
    }

	private void setCamera() {
		Log.d(TAG, "User is taking new image.");
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		getImageFile();
		intent.putExtra(MediaStore.EXTRA_OUTPUT, newImage);
		if(intent.resolveActivity(getPackageManager()) != null)
			startActivityForResult(intent, GET_CAMERA);
	}

	private void getImageFile() {
		File picDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"ImageProcessor");
		if(!picDir.exists()) picDir.mkdirs();
		File pic = new File(picDir.getPath(),File.separator +
				"Image_" + new SimpleDateFormat("yyyy-MM-ddHH_mm_ss").format(Calendar.getInstance().getTime()) +
				".jpeg");
		newImage = Uri.fromFile(pic);
	}

	private void goToSettings() {
        Log.d(TAG, "User is going to settings.");
        Intent intent = new Intent();
        intent.setClass(this, SettingsActivity.class);
        startActivity(intent);
    }

	@Override
	public void onBackPressed() {
		if(save.isEnabled()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Hold on!")
					.setMessage("Would you like to save before leaving?")
					.setPositiveButton("Save", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							saveImage();
							backPressed();
						}
					})
					.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							backPressed();
						}
					}).show();
		}
	}

	public void backPressed() {
		super.onBackPressed();
	}

	private void saveImage() {
		if(newImage == null)
			getImageFile();
		final Context ctx = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				Bitmap image = getBitmap();
				OutputStream out;
				try {
					out = getContentResolver().openOutputStream(newImage);
					image.compress(Bitmap.CompressFormat.JPEG, 80, out);
					out.flush();
					out.close();
					MediaScannerConnection.scanFile(ctx, new String[]{newImage.getPath()}, null, null);
				}
				catch(FileNotFoundException e) {
					Log.e(TAG, "File not found for saving", e);
				}
				catch(IOException e) {
					Log.e(TAG, "IOException when saving", e);
				}
				save.setEnabled(false);
			}
		}).run();
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
	    undoStack = new ArrayDeque<Bitmap>();
	    undo.setEnabled(false);
	    save.setEnabled(false);
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
	    (new FilterTask(image, filter, pref.getInt("filter_size", 1), this)).execute(getBitmap());
    }

	protected Bitmap getBitmap() {
		return ((BitmapDrawable) image.getDrawable()).getBitmap();
	}

	@Override
	public void onFling() {
		addUndo();
		new EffectTask(image, new WaveEffect(this), this).execute(getBitmap());
	}

	@Override
	public void onLongPress() {
		addUndo();
		new EffectTask(image, new FisheyeEffect(this), this).execute(getBitmap());
	}

	@Override
	public void onDoublePress() {
		addUndo();
		new EffectTask(image, new SwirlEffect(this), this).execute(getBitmap());
	}

	protected void addUndo() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplication());
		if(undoStack.size() >= pref.getInt("undo_size", 3))
			undoStack.removeFirst();
		undoStack.addLast(getBitmap());
		undo.setEnabled(true);
		save.setEnabled(true);
	}

}
