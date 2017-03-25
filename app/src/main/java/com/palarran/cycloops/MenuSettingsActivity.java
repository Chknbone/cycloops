package com.palarran.cycloops;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import static com.palarran.cycloops.R.string.settings_cyclone_data_key;

public class MenuSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_settings_activity);
    }

    public static class CyclonePreferenceFragment extends PreferenceFragment
                        implements  Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            //Helper method  to update preference summary when settings activity is launched
            Preference minCategory = findPreference(getString(R.string.settings_min_category_key));
            bindPreferenceSummaryToValue(minCategory);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

            Preference display_type = findPreference(getString(settings_cyclone_data_key));
            bindPreferenceSummaryToValue(display_type);
        }

        //Show preference settings and updates immediately after setting is changed by user
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            final int minCategory = -2;
            final int maxCategory = 5;

            final EditTextPreference editTextPreference=(EditTextPreference) findPreference(getString(R.string.settings_min_category_key));
            editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int val = Integer.parseInt(newValue.toString());
                    if ((val >= minCategory) && (val <= maxCategory)) {
                        preference.setSummary("" + val);
                        return true;
                    }
                    else {
                        // invalid you can show invalid message
                        Toast.makeText(getActivity(), "No such storm category", Toast.LENGTH_LONG).show();
                        Toast.makeText(getActivity(), "Enter a number between -2 and 5", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
            });

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0){
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;


        }

        //Define the bindPreferenceSummaryToValue() helper method to set the current
        //CyclonePreferenceFragment instance as the listener on each preference
        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}