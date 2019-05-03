package com.remaistudio.master.util;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.google.android.material.snackbar.Snackbar;
import com.remaistudio.master.R;

public class SnackbarToast {


    public SnackbarToast(Context context, @StringRes int resIdText, @StringRes int resIdButton, View.OnClickListener onClickListener) {
        Snackbar snack = Snackbar.make(((Activity) context).findViewById(android.R.id.content), resIdText, Snackbar.LENGTH_INDEFINITE);
        View view = snack.getView();
        TextView tv = view.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snack.setAction(resIdButton, onClickListener);
        snack.setActionTextColor(context.getResources().getColor(R.color.search_color));
        snack.show();
    }

    public SnackbarToast(Context context, @StringRes int resIdText, @StringRes int resIdButton) {
        this(context, resIdText, resIdButton, null);
    }

    public SnackbarToast(Context context, @StringRes int resIdText) {
        this(context, resIdText, R.string.close, null);
    }
}
