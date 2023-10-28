package es.upm.miw.bantumi;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            findPreference(getString(R.string.nombreJugadorKey)).setOnPreferenceChangeListener(
                    (preference, newValue) -> {
                        Log.i(
                                MainActivity.LOG_TAG,
                                "onCreatePreferences(): " + preference + " = " + newValue
                        );
                        return true;
                    }
            );
        }
    }
}