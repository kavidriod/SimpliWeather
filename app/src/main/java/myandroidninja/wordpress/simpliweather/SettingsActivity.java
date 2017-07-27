package myandroidninja.wordpress.simpliweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.Set;


public class SettingsActivity extends PreferenceActivity  {

    private  String TAG = SettingsActivity.class.getSimpleName();
    int zipcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



       addPreferencesFromResource(R.xml.pref);

        Preference preference =  findPreference(getResources().getString(R.string.pincodekey));
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                String s = preference.getKey();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
               String zipcodes = sharedPreferences.getString(s,null);

                if (zipcodes != null)
                {
                    zipcode = Integer.parseInt(zipcodes);
                }
                else
                {
                    zipcode = 0;
                }

                Log.i(TAG,"status -> "+s);
                Log.i(TAG,"status -> "+zipcode);

                return true;
            }
        });


        Preference btnPreference = findPreference(getResources().getString(R.string.locationkey));
        btnPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                Intent in = new Intent(getApplicationContext(),ZipCodeActivity.class);
                in.putExtra("ZIPCODE",zipcode);
                startActivity(in);


                return true;
            }
        });


    }


}
