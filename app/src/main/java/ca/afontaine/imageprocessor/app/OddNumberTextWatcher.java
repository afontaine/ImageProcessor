package ca.afontaine.imageprocessor.app;

import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

/**
* @author Andrew Fontaine
* @version 1.0
* @since 2015-01-23
*/
class OddNumberTextWatcher implements TextWatcher {
    private final AlertDialog dialog;

    public OddNumberTextWatcher(AlertDialog dialog) {
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
            if(size % 2 == 0) {
                Toast.makeText(dialog.getContext(), "Size must be odd.", Toast.LENGTH_SHORT).show();
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
