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

public class GEMSettings extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "GEMSettings";

    private static final String GENERAL_NOTIF_ADB = "display_adb_usb_debugging_notif";
    private static final String GENERAL_NOTIF_LED = "display_notification_led_screen_on";
    private static final String GENERAL_ROTARY_LOCK = "use_rotary_lockscreen";

    private static final String GENERAL_AUTO_BRIGHT_MIN_LEVEL = "auto_brightness_minimum_backlight_level";

    private CheckBoxPreference mGeneralNotifADBPref;
    private CheckBoxPreference mGeneralNotifLEDPref;
    private CheckBoxPreference mGeneralUseRotaryLockPref;

    private SeekBarStepPreference mGeneralAutoBrightMinLevelPref;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.gem_settings);

        final PreferenceScreen prefSet = getPreferenceScreen();

        mGeneralNotifADBPref = (CheckBoxPreference)prefSet.findPreference(GENERAL_NOTIF_ADB);
        mGeneralNotifLEDPref = (CheckBoxPreference)prefSet.findPreference(GENERAL_NOTIF_LED);
        mGeneralUseRotaryLockPref = (CheckBoxPreference)prefSet.findPreference(GENERAL_ROTARY_LOCK);

        mGeneralAutoBrightMinLevelPref = (SeekBarStepPreference)prefSet.findPreference(GENERAL_AUTO_BRIGHT_MIN_LEVEL);
        mGeneralAutoBrightMinLevelPref.setOnPreferenceChangeListener(this);

        prefSet.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        mGeneralNotifLEDPref.setChecked(Settings.Secure.getInt(
                getContentResolver(),
                Settings.Secure.DISPLAY_NOTIFICATION_LED_SCREEN_ON, 0) != 0);
        mGeneralNotifADBPref.setChecked(Settings.Secure.getInt(
                getContentResolver(),
                Settings.Secure.DISPLAY_ADB_USB_DEBUGGING_NOTIFICATION, 1) != 0);
        mGeneralUseRotaryLockPref.setChecked(Settings.System.getInt(
                getContentResolver(),
                Settings.System.USE_ROTARY_LOCKSCREEN, 0) != 0);
        mGeneralAutoBrightMinLevelPref.setValue(Settings.Secure.getInt(
                getContentResolver(),
                Settings.Secure.AUTO_BRIGHTNESS_MINIMUM_BACKLIGHT_LEVEL, 16));
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        return true;
    }

    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (GENERAL_NOTIF_ADB.equals(key)) {
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.DISPLAY_ADB_USB_DEBUGGING_NOTIFICATION,
                    mGeneralNotifADBPref.isChecked() ? 1 : 0);
        } else if (GENERAL_NOTIF_LED.equals(key)) {
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.DISPLAY_NOTIFICATION_LED_SCREEN_ON,
                    mGeneralNotifLEDPref.isChecked() ? 1 : 0);
        } else if (GENERAL_ROTARY_LOCK.equals(key)) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.USE_ROTARY_LOCKSCREEN,
                    mGeneralUseRotaryLockPref.isChecked() ? 1 : 0);
        } else if (GENERAL_AUTO_BRIGHT_MIN_LEVEL.equals(key)) {
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.AUTO_BRIGHTNESS_MINIMUM_BACKLIGHT_LEVEL,
                    mGeneralAutoBrightMinLevelPref.getValue());
        }
    }
}
