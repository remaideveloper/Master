package com.alegangames.master.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.alegangames.master.R;
import com.alegangames.master.activity.ActivityAppParent;
import com.alegangames.master.activity.ActivityMain;
import com.alegangames.master.activity.ActivityWebView;
import com.alegangames.master.util.AppUtil;
import com.alegangames.master.util.FragmentUtil;
import com.alegangames.master.util.rules.GDPRHelper;

public class FragmentSettings extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);
    }

    /**
     * Hides TabLayout in activity
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ActivityAppParent) {
            ActivityAppParent activityAppParrent = (ActivityAppParent) context;
            TabLayout tabLayout;
            if ((tabLayout = activityAppParrent.findViewById(R.id.tab_layout)) != null) {
                tabLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        new Handler().postDelayed(() -> {
            try {
                ((ActivityMain) getActivity()).mNavigationViewUtil.onUpdateDrawerToggle();
                ((ActivityMain) getActivity()).getSupportActionBar().setTitle(R.string.settings);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 100);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //About
        Preference preferenceabout = findPreference("about");
        preferenceabout.setOnPreferenceClickListener(preference -> {
            String title = getString(R.string.app_name);
            String alert1 = getString(R.string.settings_aboutus_version) + " " + AppUtil.getAppVersionName(getActivity());

            if (!getActivity().isFinishing()) {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(getResources().getString(R.string.settings_aboutus_header))
                        .setMessage(title + "\n\n" + alert1)
                        .setPositiveButton(getResources().getString(android.R.string.ok), null)
                        .create()
                        .show();
            }
            return true;
        });

        //Open source licenses
        Preference preferenceLicenses = findPreference(getString(R.string.settings_key_open_licenses));
        preferenceLicenses.setOnPreferenceClickListener(preference -> {
            FragmentUtil.onTransactionFragment(getActivity(), R.id.main_container, new FragmentLicenses());
            return true;
        });

        //Confidentiality
        Preference preferenceConfidentiality = findPreference(getString(R.string.settings_confidentiality_key));
        preferenceConfidentiality.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getContext(), ActivityWebView.class);
            intent.putExtra(ActivityWebView.URL_EXTRA, getString(R.string.settings_confidentiality_link));
            startActivity(intent);
            return true;
        });

        //Consent Policy
        Preference preferenceConsentPolicy = findPreference(getString(R.string.settings_consent_policy_key));
        preferenceConsentPolicy.setVisible(GDPRHelper.isRequestLocationInEeaOrUnknown(getContext()));
        preferenceConsentPolicy.setOnPreferenceClickListener(preference -> {
            GDPRHelper.getConsentForm(getContext());
            return true;
        });

    }
}
