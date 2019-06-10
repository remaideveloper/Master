package com.alegangames.master.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.alegangames.master.R;
import com.alegangames.master.activity.ActivityWebView;

public class FragmentLicenses extends PreferenceFragmentCompat {

    private String[] openSourceLibraries;
    private String[] authors;
    private String[] urls;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_licenses);
        // initialize arrays
        openSourceLibraries = getResources().getStringArray(R.array.library_names);
        authors = getResources().getStringArray(R.array.library_authors);
        urls = getResources().getStringArray(R.array.library_links);

        for (int i = 0; i < openSourceLibraries.length; i++) {
            Preference library = new Preference(getActivity());
            library.setLayoutResource(R.layout.preference_license);
            library.setTitle(openSourceLibraries[i]);
            library.setSummary(authors[i]);
            Intent intent = new Intent(getActivity(), ActivityWebView.class);
            intent.putExtra(ActivityWebView.URL_EXTRA, urls[i]);
            library.setIntent(intent);

            getPreferenceScreen().addPreference(library);
        }
    }
}
