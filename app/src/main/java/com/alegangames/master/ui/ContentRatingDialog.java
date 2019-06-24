package com.alegangames.master.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.alegangames.master.R;
import com.alegangames.master.activity.ActivityMain;
import com.alegangames.master.activity.ActivityWebView;
import com.alegangames.master.util.preference.UtilPreference;
import com.alegangames.master.util.rules.GDPRHelper;

public class ContentRatingDialog extends DialogFragment {

    private static final String TAG = ContentRatingDialog.class.getSimpleName();


    public static ContentRatingDialog getInstance() {
        return new ContentRatingDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_dialog_content_rating, null);
        builder.setView(view);

        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewTitle.setText(R.string.term_of_use);

        TextView textViewMessage = view.findViewById(R.id.textViewMessage);
        String privacyPolisy = "<a href=" + getString(R.string.settings_confidentiality_link) + ">" + getString(R.string.privacy_policy) + "</a>";
//        String userAgreement = "<a href=" + getString(R.string.settings_user_agreement_link) + ">" + getString(R.string.user_agreement) + "</a>";
        String messageString = getString(R.string.term_of_use_message, privacyPolisy);
        Spanned messageHtml;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            messageHtml = Html.fromHtml(messageString, Html.FROM_HTML_MODE_LEGACY);
        } else {
            messageHtml = Html.fromHtml(messageString);
        }
//        textViewMessage.setText(messageHtml);
        textViewMessage.setMovementMethod(LinkMovementMethod.getInstance());

        URLSpan[] urls = messageHtml.getSpans(0, messageHtml.length(), URLSpan.class);
        SpannableStringBuilder style = new SpannableStringBuilder(messageHtml);

        style.clearSpans();//should clear old spans
        for (URLSpan url : urls) {
            CustomerTextClick click = new CustomerTextClick(url.getURL());
            style.setSpan(click, messageHtml.getSpanStart(url), messageHtml.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textViewMessage.setText(style);

        Button button = view.findViewById(R.id.button);
        button.setText(R.string.accept);
        button.setOnClickListener(v -> {
            GDPRHelper.getRequestConsentInfo(getContext());
            UtilPreference.setUnderAgePref(getContext(), true);
            startActivity(new Intent(getContext(), ActivityMain.class));
            dismiss();
        });


        setCancelable(false);

        return builder.create();
    }

    public void onShow(FragmentActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (!activity.isFinishing() && !activity.isDestroyed() && !activity.getSupportFragmentManager().isStateSaved()) {
                show(activity.getSupportFragmentManager(), null);
            }
        } else if (!activity.isFinishing() && !activity.getSupportFragmentManager().isStateSaved()) {
            show(activity.getSupportFragmentManager(), null);
        }

    }

    private class CustomerTextClick extends ClickableSpan {

        private String mUrl;

        CustomerTextClick(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(@NonNull View widget) {
            Intent intent = new Intent(getActivity(), ActivityWebView.class);
            intent.putExtra(ActivityWebView.URL_EXTRA, mUrl);
            intent.putExtra(ActivityWebView.BACK_EXTRA, true);
            startActivity(intent);
        }
    }

}
