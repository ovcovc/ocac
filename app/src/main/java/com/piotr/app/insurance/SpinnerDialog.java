package com.piotr.app.insurance;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Piotr on 2015-08-19.
 */
public class SpinnerDialog extends DialogFragment {

    public SpinnerDialog() {
        // use empty constructors. If something is needed use onCreate's
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        ProgressDialog _dialog = new ProgressDialog(getActivity());
        this.setStyle(STYLE_NO_TITLE, getTheme()); // You can use styles or inflate a view
        _dialog.setMessage("Trwa komunikacja z serwerem.."); // set your messages if not inflated from XML

        _dialog.setCancelable(false);

        return _dialog;
    }
}