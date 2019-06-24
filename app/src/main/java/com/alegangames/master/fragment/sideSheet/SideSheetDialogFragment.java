package com.alegangames.master.fragment.sideSheet;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * @author Sony Raj on 07-07-2017.
 */

public class SideSheetDialogFragment extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new SideSheetDialog(getContext(), getTheme());
    }
}
