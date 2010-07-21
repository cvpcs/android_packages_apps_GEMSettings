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

import org.cvpcs.android.gem_settings.utils.ColorChangedListener;
import org.cvpcs.android.gem_settings.utils.SeekBarStepPreference;
import org.cvpcs.android.gem_settings.R;

import android.app.ColorPickerDialog;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

public class GEMSettings extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "GEMSettings";

    private static final String GENERAL_NOTIF_ADB = "display_adb_usb_debugging_notif";
    private static final String GENERAL_NOTIF_LED = "display_notification_led_screen_on";

    private static final String GENERAL_AUTO_BRIGHT_MIN_LEVEL = "auto_brightness_minimum_backlight_level";

    private static final String SERVICE_COMPCACHE = "compcache";
    private static final String SERVICE_COMPCACHE_PROPERTY = "persist.gem.svc.rzswap.enable";

    private static final String CPUFREQ_ENABLE = "cpufreq_enable";
    private static final String CPUFREQ_ENABLE_PROPERTY    = "persist.gem.cpufreq.enable";
    private static final String CPUFREQ_GOVERNOR = "cpufreq_governor";
    private static final String CPUFREQ_GOVERNOR_PROPERTY  = "persist.gem.cpufreq.governor";
    private static final String CPUFREQ_MINIMUM = "cpufreq_minimum";
    private static final String CPUFREQ_MINIMUM_PROPERTY   = "persist.gem.cpufreq.minimum";
    private static final String CPUFREQ_MAXIMUM = "cpufreq_maximum";
    private static final String CPUFREQ_MAXIMUM_PROPERTY   = "persist.gem.cpufreq.maximum";

    private static final String UI_COLOR_CLOCK = "color_clock";
    private static final String UI_COLOR_DATE = "color_date";
    private static final String UI_COLOR_LABEL_SPN = "color_label_spn";
    private static final String UI_COLOR_LABEL_PLMN = "color_label_plmn";
    private static final String UI_COLOR_BATTERY_PERCENTAGE = "color_battery_percentage";

    private static final String UI_COLOR_NOTIFICATION_TICKER_TEXT = "color_notification_ticker_text";
    private static final String UI_COLOR_NOTIFICATION_NONE = "color_notification_none";
    private static final String UI_COLOR_NOTIFICATION_LATEST = "color_notification_latest";
    private static final String UI_COLOR_NOTIFICATION_ONGOING = "color_notification_ongoing";
    private static final String UI_COLOR_NOTIFICATION_CLEAR_BUTTON = "color_notification_clear_button";

    private static final String UI_COLOR_NOTIFICATION_ITEM_TITLE = "color_notification_item_title";
    private static final String UI_COLOR_NOTIFICATION_ITEM_TEXT = "color_notification_item_text";
    private static final String UI_COLOR_NOTIFICATION_ITEM_PROGRESS_TEXT = "color_notification_item_progress_text";
    private static final String UI_COLOR_NOTIFICATION_ITEM_TIME = "color_notification_item_time";

    private static final String UI_DISPLAY_STATUS_BAR_CLOCK = "display_status_bar_clock";
    private static final String UI_DISPLAY_BATTERY_PERCENTAGE = "display_battery_percentage";

    private CheckBoxPreference mGeneralNotifADBPref;
    private CheckBoxPreference mGeneralNotifLEDPref;

    private SeekBarStepPreference mGeneralAutoBrightMinLevelPref;

    private CheckBoxPreference mServiceCompcachePref;

    private CheckBoxPreference mCPUFreqEnablePref;
    private ListPreference mCPUFreqGovernorPref;
    private SeekBarStepPreference mCPUFreqMinimumPref;
    private SeekBarStepPreference mCPUFreqMaximumPref;

    private Preference mColorClockPref;
    private Preference mColorDatePref;
    private Preference mColorLabelSPNPref;
    private Preference mColorLabelPLMNPref;
    private Preference mColorBatteryPercentagePref;

    private Preference mColorNotificationTickerTextPref;
    private Preference mColorNotificationNonePref;
    private Preference mColorNotificationLatestPref;
    private Preference mColorNotificationOngoingPref;
    private Preference mColorNotificationClearButtonPref;

    private Preference mColorNotificationItemTitlePref;
    private Preference mColorNotificationItemTextPref;
    private Preference mColorNotificationItemProgressTextPref;
    private Preference mColorNotificationItemTimePref;

    private CheckBoxPreference mDisplayStatusBarClockPref;
    private CheckBoxPreference mDisplayBatteryPercentagePref;

    private int mSwapAvailable = -1;

    private int[] mCPUFreqSpeeds = null;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.gem_settings);

        final PreferenceScreen prefSet = getPreferenceScreen();

        mGeneralNotifADBPref = (CheckBoxPreference)prefSet.findPreference(GENERAL_NOTIF_ADB);
        mGeneralNotifLEDPref = (CheckBoxPreference)prefSet.findPreference(GENERAL_NOTIF_LED);

        mGeneralAutoBrightMinLevelPref = (SeekBarStepPreference)prefSet.findPreference(GENERAL_AUTO_BRIGHT_MIN_LEVEL);

        mServiceCompcachePref = (CheckBoxPreference)prefSet.findPreference(SERVICE_COMPCACHE);
        if(!isSwapAvailable()) {
            // disable compcache but display a message so people know why it's not working
            mServiceCompcachePref.setEnabled(false);
            mServiceCompcachePref.setChecked(false);
            mServiceCompcachePref.setSummaryOn("Compcache is not supported in your kernel");
            mServiceCompcachePref.setSummaryOff("Compcache is not supported in your kernel");
            SystemProperties.set(SERVICE_COMPCACHE_PROPERTY,"0");
            Log.i(TAG, "Disabling compcache due to lack of swap support in kernel");
        }

        mCPUFreqMinimumPref = (SeekBarStepPreference)prefSet.findPreference(CPUFREQ_MINIMUM);
        mCPUFreqMinimumPref.setSteps(getCPUFreqSpeeds());
        mCPUFreqMaximumPref = (SeekBarStepPreference)prefSet.findPreference(CPUFREQ_MAXIMUM);
        mCPUFreqMaximumPref.setSteps(getCPUFreqSpeeds());

        mColorClockPref = prefSet.findPreference(UI_COLOR_CLOCK);
        mColorDatePref = prefSet.findPreference(UI_COLOR_DATE);
        mColorLabelSPNPref = prefSet.findPreference(UI_COLOR_LABEL_SPN);
        mColorLabelPLMNPref = prefSet.findPreference(UI_COLOR_LABEL_PLMN);
        mColorBatteryPercentagePref = prefSet.findPreference(UI_COLOR_BATTERY_PERCENTAGE);

        mColorNotificationTickerTextPref = prefSet.findPreference(UI_COLOR_NOTIFICATION_TICKER_TEXT);
        mColorNotificationNonePref = prefSet.findPreference(UI_COLOR_NOTIFICATION_NONE);
        mColorNotificationLatestPref = prefSet.findPreference(UI_COLOR_NOTIFICATION_LATEST);
        mColorNotificationOngoingPref = prefSet.findPreference(UI_COLOR_NOTIFICATION_ONGOING);
        mColorNotificationClearButtonPref = prefSet.findPreference(UI_COLOR_NOTIFICATION_CLEAR_BUTTON);

        mColorNotificationItemTitlePref = prefSet.findPreference(UI_COLOR_NOTIFICATION_ITEM_TITLE);
        mColorNotificationItemTextPref = prefSet.findPreference(UI_COLOR_NOTIFICATION_ITEM_TEXT);
        mColorNotificationItemProgressTextPref = prefSet.findPreference(UI_COLOR_NOTIFICATION_ITEM_PROGRESS_TEXT);
        mColorNotificationItemTimePref = prefSet.findPreference(UI_COLOR_NOTIFICATION_ITEM_TIME);

        mDisplayStatusBarClockPref = (CheckBoxPreference)prefSet.findPreference(UI_DISPLAY_STATUS_BAR_CLOCK);
        mDisplayBatteryPercentagePref = (CheckBoxPreference)prefSet.findPreference(UI_DISPLAY_BATTERY_PERCENTAGE);

        prefSet.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mColorClockPref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_CLOCK),
                    readColor(Settings.System.COLOR_CLOCK, -16777216));
            cp.show();
        }
        else if (preference == mColorDatePref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_DATE),
                    readColor(Settings.System.COLOR_DATE, -16777216));
            cp.show();
        }
        else if (preference == mColorLabelSPNPref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_LABEL_SPN),
                    readColor(Settings.System.COLOR_LABEL_SPN, -16777216));
            cp.show();
        }
        else if (preference == mColorLabelPLMNPref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_LABEL_PLMN),
                    readColor(Settings.System.COLOR_LABEL_PLMN, -16777216));
            cp.show();
        }
        else if (preference == mColorBatteryPercentagePref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_BATTERY_PERCENTAGE),
                    readColor(Settings.System.COLOR_BATTERY_PERCENTAGE, -1));
            cp.show();
        }
        else if (preference == mColorNotificationTickerTextPref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_NOTIFICATION_TICKER_TEXT),
                    readColor(Settings.System.COLOR_NOTIFICATION_TICKER_TEXT, -16777216));
            cp.show();
        }
        else if (preference == mColorNotificationNonePref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_NOTIFICATION_NONE),
                    readColor(Settings.System.COLOR_NOTIFICATION_NONE, -1));
            cp.show();
        }
        else if (preference == mColorNotificationLatestPref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_NOTIFICATION_LATEST),
                    readColor(Settings.System.COLOR_NOTIFICATION_LATEST, -1));
            cp.show();
        }
        else if (preference == mColorNotificationOngoingPref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_NOTIFICATION_ONGOING),
                    readColor(Settings.System.COLOR_NOTIFICATION_ONGOING, -1));
            cp.show();
        }
        else if (preference == mColorNotificationClearButtonPref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_NOTIFICATION_CLEAR_BUTTON),
                    readColor(Settings.System.COLOR_NOTIFICATION_CLEAR_BUTTON, -16777216));
            cp.show();
        }
        else if (preference == mColorNotificationItemTitlePref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_NOTIFICATION_ITEM_TITLE),
                    readColor(Settings.System.COLOR_NOTIFICATION_ITEM_TITLE, -16777216));
            cp.show();
        }
        else if (preference == mColorNotificationItemTextPref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_NOTIFICATION_ITEM_TEXT),
                    readColor(Settings.System.COLOR_NOTIFICATION_ITEM_TEXT, -16777216));
            cp.show();
        }
        else if (preference == mColorNotificationItemProgressTextPref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_NOTIFICATION_ITEM_PROGRESS_TEXT),
                    readColor(Settings.System.COLOR_NOTIFICATION_ITEM_PROGRESS_TEXT, -16777216));
            cp.show();
        }
        else if (preference == mColorNotificationItemTimePref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_NOTIFICATION_ITEM_TIME),
                    readColor(Settings.System.COLOR_NOTIFICATION_ITEM_TIME, -16777216));
            cp.show();
        }
        return false;
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
        } else if (GENERAL_AUTO_BRIGHT_MIN_LEVEL.equals(key)) {
            Settings.Secure.putInt(getContentResolver(),
                    Settings.Secure.AUTO_BRIGHTNESS_MINIMUM_BACKLIGHT_LEVEL,
                    mGeneralAutoBrightMinLevelPref.getValue());
        } else if (SERVICE_COMPCACHE.equals(key)) {
            SystemProperties.set(SERVICE_COMPCACHE_PROPERTY,
                    mServiceCompcachePref.isChecked() ? "1" : "0");
        } else if (UI_DISPLAY_STATUS_BAR_CLOCK.equals(key)) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.DISPLAY_STATUS_BAR_CLOCK,
                    mDisplayStatusBarClockPref.isChecked() ? 1 : 0);
        } else if (UI_DISPLAY_BATTERY_PERCENTAGE.equals(key)) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.DISPLAY_BATTERY_PERCENTAGE,
                    mDisplayBatteryPercentagePref.isChecked() ? 1 : 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        updateToggles();

        mGeneralAutoBrightMinLevelPref.setValue(Settings.Secure.getInt(
                getContentResolver(),
                Settings.Secure.AUTO_BRIGHTNESS_MINIMUM_BACKLIGHT_LEVEL, 16));
    }

    private int readColor(String setting, int def) {
        try {
            return Settings.System.getInt(getContentResolver(), setting);
        } catch (SettingNotFoundException e) {
            return def;
        }
    }

    private void updateToggles() {
        mGeneralNotifLEDPref.setChecked(Settings.Secure.getInt(
                getContentResolver(),
                Settings.Secure.DISPLAY_NOTIFICATION_LED_SCREEN_ON, 0) != 0);
        mGeneralNotifADBPref.setChecked(Settings.Secure.getInt(
                getContentResolver(),
                Settings.Secure.DISPLAY_ADB_USB_DEBUGGING_NOTIFICATION, 1) != 0);
        mServiceCompcachePref.setChecked(SystemProperties.getBoolean(
                SERVICE_COMPCACHE_PROPERTY, false));
        mDisplayStatusBarClockPref.setChecked(Settings.System.getInt(
                getContentResolver(),
                Settings.System.DISPLAY_STATUS_BAR_CLOCK, 1) != 0);
        mDisplayBatteryPercentagePref.setChecked(Settings.System.getInt(
                getContentResolver(),
                Settings.System.DISPLAY_BATTERY_PERCENTAGE, 1) != 0);
    }

    private boolean isSwapAvailable() {
        if (mSwapAvailable < 0) {
            mSwapAvailable = new File("/proc/swaps").exists() ? 1 : 0;
        }
        return mSwapAvailable > 0;
    }

    private int[] getCPUFreqSpeeds() {
        if (mCPUFreqSpeeds == null) {
            // catchall, this is overwritten if we can
            mCPUFreqSpeeds =  new int[] { 0, 25, 50, 75, 100 };

            File cpufreqs = new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies");

            if (cpufreqs.exists() && cpufreqs.canRead()) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(cpufreqs));

                    String freqs = "";
                    String buffer = null;

                    while((buffer = br.readLine()) != null) {
                        freqs += buffer + " ";
                    }

                    br.close();

                    String[] freqList = freqs.trim().split(" +");

                    mCPUFreqSpeeds = new int[freqList.length];

                    for (int i = 0; i < freqList.length; i++) {
                        mCPUFreqSpeeds[i] = Integer.parseInt(freqList[i]) / 1000;
                    }

                    Arrays.sort(mCPUFreqSpeeds);
                } catch (Exception e) {
                    Log.e(TAG, "Exception thrown when gathering CPU frequency data", e);
                }
            }
        }

        for (int i = 0; i < mCPUFreqSpeeds.length; i++) {
            Log.i(TAG, "CPUFreq[" + i + "] = " + mCPUFreqSpeeds[i]);
        }

        return mCPUFreqSpeeds;
    }
}
