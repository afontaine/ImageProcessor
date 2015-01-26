package ca.afontaine.imageprocessor.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
* @author Andrew Fontaine
* @version 1.0
* @since 2015-01-23
*/
public class NumberPickerDialog extends DialogPreference {

    private int value;
    private EditText size;
    static int DEFAULT_VALUE = 1;

    public NumberPickerDialog(Context ctx, AttributeSet attr) {
        super(ctx,attr);
        setDialogLayoutResource(R.layout.dialog_filter_size);
        setTitle(R.string.filter_size);
        setPositiveButtonText("OK");
        setNegativeButtonText("Cancel");

    }
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if(positiveResult) {
            try {
                value = Integer.parseInt(size.getText().toString());
                if (value % 2 != 0) persistInt(value);
            }
            catch(NumberFormatException e) {
            }
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        size = (EditText) view.findViewById(R.id.filter_size);
        size.setText(Integer.toString(value));
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object value) {
        if(restore)
            this.value = getPersistedInt(DEFAULT_VALUE);
        else {
            this.value = (Integer) value;
            persistInt(this.value);
        }
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        size.addTextChangedListener(new OddNumberTextWatcher((AlertDialog) getDialog()));
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, DEFAULT_VALUE);
    }
}
