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

// import needed packages from gem_settings
import org.cvpcs.android.gem_settings.R;
import org.cvpcs.android.gem_settings.util.ColorChangedListener;

// import some new stuffs
import android.app.ColorPickerDialog;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.os.Bundle;
import android.util.Log;

public class UITweaks extends PreferenceActivity {
    private static final String TAG = "GEMSettings[UITweaks]";

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

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setTitle(R.string.title_ui_tweaks);
        addPreferencesFromResource(R.xml.ui_tweaks);

        final PreferenceScreen prefSet = getPreferenceScreen();

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
        mDisplayStatusBarClockPref.setChecked(Settings.System.getInt(
                getContentResolver(),
                Settings.System.DISPLAY_STATUS_BAR_CLOCK, 1) != 0);

        mDisplayBatteryPercentagePref = (CheckBoxPreference)prefSet.findPreference(UI_DISPLAY_BATTERY_PERCENTAGE);
        mDisplayBatteryPercentagePref.setChecked(Settings.System.getInt(
                getContentResolver(),
                Settings.System.DISPLAY_BATTERY_PERCENTAGE, 1) != 0);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mColorClockPref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_CLOCK),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.COLOR_CLOCK,
                            getResources().getColor(com.android.internal.R.color.black)));
            cp.show();
            return true;
        } else if (preference == mColorDatePref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_DATE),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.COLOR_DATE,
                            getResources().getColor(com.android.internal.R.color.black)));
            cp.show();
            return true;
        } else if (preference == mColorLabelSPNPref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_LABEL_SPN),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.COLOR_LABEL_SPN,
                            getResources().getColor(com.android.internal.R.color.black)));
            cp.show();
            return true;
        } else if (preference == mColorLabelPLMNPref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_LABEL_PLMN),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.COLOR_LABEL_PLMN,
                            getResources().getColor(com.android.internal.R.color.black)));
            cp.show();
            return true;
        } else if (preference == mColorBatteryPercentagePref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_BATTERY_PERCENTAGE),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.COLOR_BATTERY_PERCENTAGE,
                            getResources().getColor(com.android.internal.R.color.white)));
            cp.show();
            return true;
        } else if (preference == mColorNotificationTickerTextPref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_NOTIFICATION_TICKER_TEXT),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.COLOR_NOTIFICATION_TICKER_TEXT,
                            getResources().getColor(com.android.internal.R.color.black)));
            cp.show();
            return true;
        } else if (preference == mColorNotificationNonePref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_NOTIFICATION_NONE),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.COLOR_NOTIFICATION_NONE,
                            getResources().getColor(com.android.internal.R.color.white)));
            cp.show();
            return true;
        } else if (preference == mColorNotificationLatestPref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_NOTIFICATION_LATEST),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.COLOR_NOTIFICATION_LATEST,
                            getResources().getColor(com.android.internal.R.color.white)));
            cp.show();
            return true;
        } else if (preference == mColorNotificationOngoingPref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_NOTIFICATION_ONGOING),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.COLOR_NOTIFICATION_ONGOING,
                            getResources().getColor(com.android.internal.R.color.white)));
            cp.show();
            return true;
        } else if (preference == mColorNotificationClearButtonPref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_NOTIFICATION_CLEAR_BUTTON),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.COLOR_NOTIFICATION_CLEAR_BUTTON,
                            getResources().getColor(com.android.internal.R.color.black)));
            cp.show();
            return true;
        } else if (preference == mColorNotificationItemTitlePref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_NOTIFICATION_ITEM_TITLE),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.COLOR_NOTIFICATION_ITEM_TITLE,
                            getResources().getColor(com.android.internal.R.color.black)));
            cp.show();
            return true;
        } else if (preference == mColorNotificationItemTextPref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_NOTIFICATION_ITEM_TEXT),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.COLOR_NOTIFICATION_ITEM_TEXT,
                            getResources().getColor(com.android.internal.R.color.black)));
            cp.show();
            return true;
        } else if (preference == mColorNotificationItemProgressTextPref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_NOTIFICATION_ITEM_PROGRESS_TEXT),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.COLOR_NOTIFICATION_ITEM_PROGRESS_TEXT,
                            getResources().getColor(com.android.internal.R.color.black)));
            cp.show();
            return true;
        } else if (preference == mColorNotificationItemTimePref) {
            ColorPickerDialog cp = new ColorPickerDialog(this,
                    new ColorChangedListener(this, Settings.System.COLOR_NOTIFICATION_ITEM_TIME),
                    Settings.System.getInt(getContentResolver(),
                            Settings.System.COLOR_NOTIFICATION_ITEM_TIME,
                            getResources().getColor(com.android.internal.R.color.black)));
            cp.show();
            return true;
        } else if (preference == mDisplayStatusBarClockPref) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.DISPLAY_STATUS_BAR_CLOCK,
                    mDisplayStatusBarClockPref.isChecked() ? 1 : 0);
            return true;
        } else if (preference == mDisplayBatteryPercentagePref) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.DISPLAY_BATTERY_PERCENTAGE,
                    mDisplayBatteryPercentagePref.isChecked() ? 1 : 0);
            return true;
        }

        return false;
    }
}
