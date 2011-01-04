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

import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.os.Bundle;
import android.util.Log;

public class Extras extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "GEMSettings[Extras]";

    private static final String NOTIF_ADB = "display_adb_usb_debugging_notif";
    private static final String NOTIF_LED = "display_notification_led_screen_on";

    private static final String AUTO_BRIGHT_MIN_LEVEL = "auto_brightness_minimum_backlight_level";

    private static final String KILL_APP = "kill_app_longpress_back";

    private CheckBoxPreference mNotifADBPref;
    private CheckBoxPreference mNotifLEDPref;

    private SeekBarStepPreference mAutoBrightMinLevelPref;

    private CheckBoxPreference mKillAppPref;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.extras);

        final PreferenceScreen prefSet = getPreferenceScreen();

        mNotifADBPref = (CheckBoxPreference)prefSet.findPreference(NOTIF_ADB);
        mNotifLEDPref = (CheckBoxPreference)prefSet.findPreference(NOTIF_LED);

        mAutoBrightMinLevelPref = (SeekBarStepPreference)prefSet.findPreference(AUTO_BRIGHT_MIN_LEVEL);
        mAutoBrightMinLevelPref.setOnPreferenceChangeListener(this);

        mKillAppPref = (CheckBoxPreference)prefSet.findPreference(KILL_APP);

        prefSet.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        mNotifLEDPref.setChecked(Settings.Secure.getInt(
                getContentResolver(),
                Settings.Secure.DISPLAY_NOTIFICATION_LED_SCREEN_ON, 0) != 0);
        mNotifADBPref.setChecked(Settings.Secure.getInt(
                getContentResolver(),
                Settings.Secure.DISPLAY_ADB_USB_DEBUGGING_NOTIFICATION, 1) != 0);
        mAutoBrightMinLevelPref.setValue(Settings.Secure.getInt(
                getContentResolver(),
                Settings.Secure.AUTO_BRIGHTNESS_MINIMUM_BACKLIGHT_LEVEL, 16));
        mKillAppPref.setChecked(Settings.Secure.getInt(
                getContentResolver(),
                Settings.Secure.KILL_APP_LONGPRESS_BACK, 0) != 0);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        return true;
    }

    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (NOTIF_ADB.equals(key)) {
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.DISPLAY_ADB_USB_DEBUGGING_NOTIFICATION,
                    mNotifADBPref.isChecked() ? 1 : 0);
        } else if (NOTIF_LED.equals(key)) {
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.DISPLAY_NOTIFICATION_LED_SCREEN_ON,
                    mNotifLEDPref.isChecked() ? 1 : 0);
        } else if (AUTO_BRIGHT_MIN_LEVEL.equals(key)) {
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.AUTO_BRIGHTNESS_MINIMUM_BACKLIGHT_LEVEL,
                    mAutoBrightMinLevelPref.getValue());
        } else if (KILL_APP.equals(key)) {
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.KILL_APP_LONGPRESS_BACK,
                    mKillAppPref.isChecked() ? 1 : 0);
        }
    }
}
