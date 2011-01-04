/* //device/apps/Settings/src/com/android/settings/Keyguard.java
**
** Copyright 2006, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License"); 
** you may not use this file except in compliance with the License. 
** You may obtain a copy of the License at 
**
**     http://www.apache.org/licenses/LICENSE-2.0 
**
** Unless required by applicable law or agreed to in writing, software 
** distributed under the License is distributed on an "AS IS" BASIS, 
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and 
** limitations under the License.
*/

package org.cvpcs.android.gem_settings.activities;

import org.cvpcs.android.gem_settings.utils.SeekBarStepPreference;
import org.cvpcs.android.gem_settings.R;

import android.app.ColorPickerDialog;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.os.Bundle;
import android.util.Log;

public class Tools extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "GEMSettings[Tools]";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.tools);

        final PreferenceScreen prefSet = getPreferenceScreen();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        return true;
    }

    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
          
    }
}

