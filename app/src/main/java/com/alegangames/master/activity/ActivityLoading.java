package com.alegangames.master.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alegangames.master.R;
import com.alegangames.master.ads.admob.AdMobRequest;
import com.alegangames.master.util.StrictModeUtil;
import com.alegangames.master.util.network.NetworkManager;
import com.alegangames.master.util.preference.UtilPreference;
import com.alegangames.master.util.rules.COPPAHelper;
import com.alegangames.master.util.rules.GDPRHelper;

public class ActivityLoading extends AppCompatActivity {

    private static final String TAG = ActivityLoading.class.getSimpleName();
    private static final int LAYOUT = R.layout.activity_loading;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        StrictModeUtil.init();
        NetworkManager.getInstance(this);

        if (!UtilPreference.getUnderAgePref(this) || UtilPreference.getMaxAdContentRatingPref(this).isEmpty() ||
                UtilPreference.getAge(this) == -1) {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            findViewById(R.id.containerDialog).setVisibility(View.VISIBLE);

            String[] values = new String[]{
                    "3", "4", "5", "6", "7", "8", "9", "10",
                    "11", "12", "13", "14", "15", "16", "17", "---",
                    "18", "19", "20", "21", "22", "23", "24", "25",
                    "26", "27", "28", "29", "30", "31", "32", "33",
                    "34", "35", "36", "37", "38", "39", "40", "41",
                    "42", "43", "44", "45", "46", "47", "48", "49",
                    "50", "51", "52", "53", "54", "55", "56", "57"
            };

            NumberPicker numberPicker = findViewById(R.id.number_picker);
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(55);
            numberPicker.setDisplayedValues(values);

            TextView textViewMessage = findViewById(R.id.textViewMessage);
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
                style.setSpan(new ForegroundColorSpan(Color.parseColor("#976928")), messageHtml.getSpanStart(url), messageHtml.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            textViewMessage.setText(style);

            Button button = findViewById(R.id.button);
            button.setEnabled(false);
            numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
                if (newVal == 15)
                    button.setEnabled(false);
                else if (oldVal == 15)
                    button.setEnabled(true);
            });
            numberPicker.setValue(15);
            button.setText(R.string.accept);
            button.setOnClickListener(v -> {
                GDPRHelper gdprHelper = new GDPRHelper(this);
                int age = Integer.valueOf(values[numberPicker.getValue()]);
                UtilPreference.setAge(this, age);
                if (age >= 18)
                    UtilPreference.setMaxAdContentRatingPref(this, AdMobRequest.EXTRA_ACR_VALUE_MA);
                else if (age>=12)
                    UtilPreference.setMaxAdContentRatingPref(this, AdMobRequest.EXTRA_ACR_VALUE_T);
                else if (age>=7)
                    UtilPreference.setMaxAdContentRatingPref(this, AdMobRequest.EXTRA_ACR_VALUE_PG);
                else if (age>=3)
                    UtilPreference.setMaxAdContentRatingPref(this, AdMobRequest.EXTRA_ACR_VALUE_G);
                UtilPreference.setUnderAgePref(this, true);
                if (age<14) {
                    COPPAHelper.setChildDirectedTreatment(this, true);
                    gdprHelper.setTagForUnderAgeOfConsent(this);
                }
                startActivity(new Intent(this, ActivityMain.class));
            });
        } else {

            //Задержка для появления экрана загрузки
            AsyncTask.execute(()->{
                try {
                    Thread.sleep(1000);
                    runOnUiThread(()->{
                        startActivity(new Intent(ActivityLoading.this, ActivityMain.class));
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class CustomerTextClick extends ClickableSpan {

        private String mUrl;

        CustomerTextClick(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(@NonNull View widget) {
            Intent intent = new Intent(ActivityLoading.this, ActivityWebView.class);
            intent.putExtra(ActivityWebView.URL_EXTRA, mUrl);
            intent.putExtra(ActivityWebView.BACK_EXTRA, true);
            startActivity(intent);
        }
    }

}
