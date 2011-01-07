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

package org.cvpcs.android.gem_settings.activity;

import org.cvpcs.android.gem_settings.R;

import android.app.ColorPickerDialog;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.os.Bundle;
import android.util.Log;

public class Audio extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "GEMSettings[Audio]";

    private static final String VOLUME_BUTTON_MUSIC_CONTROLS = "volume_button_music_controls";

    private static final String LOCKSCREEN_MUSIC_CONTROLS = "display_lockscreen_music_controls";

    private CheckBoxPreference mVolumeButtonMusicControlsPref;

    private ListPreference mGeneralLockscreenMusicControlsPref;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.audio);

        final PreferenceScreen prefSet = getPreferenceScreen();

        mVolumeButtonMusicControlsPref = (CheckBoxPreference)prefSet.findPreference(VOLUME_BUTTON_MUSIC_CONTROLS);
        mVolumeButtonMusicControlsPref.setChecked(Settings.System.getInt(
                getContentResolver(),
                Settings.System.ENABLE_VOLBTN_MUSIC_CONTROLS, 1) != 0);

        mGeneralLockscreenMusicControlsPref = (ListPreference)prefSet.findPreference(LOCKSCREEN_MUSIC_CONTROLS);
        mGeneralLockscreenMusicControlsPref.setValue(Integer.toString(Settings.System.getInt(
                getContentResolver(),
                Settings.System.DISPLAY_LOCKSCREEN_MUSIC_CONTROLS, 1)));
        mGeneralLockscreenMusicControlsPref.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if(preference == mVolumeButtonMusicControlsPref) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.ENABLE_VOLBTN_MUSIC_CONTROLS,
                    mVolumeButtonMusicControlsPref.isChecked() ? 1 : 0);
        }

        return true;
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if(preference == mGeneralLockscreenMusicControlsPref) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.DISPLAY_LOCKSCREEN_MUSIC_CONTROLS,
                    Integer.parseInt(mGeneralLockscreenMusicControlsPref.getValue()));
            return true;
        }

        return false;
    }
}
