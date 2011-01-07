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
import org.cvpcs.android.gem_settings.util.GalaxySWidgetUtil;

import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GalaxySWidgetSelectButtons extends PreferenceActivity {
    private static final String TAG = "GEMSettings[GalaxySWidgetSelectButtons]";

    private static final String BUTTONS_CATEGORY = "galaxy_s_widget_select_buttons_list";
    private static final String SELECT_BUTTON_KEY_PREFIX = "galaxy_s_widget_select_button_";

    private HashMap<CheckBoxPreference, String> mCheckBoxPrefs = new HashMap<CheckBoxPreference, String>();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.galaxy_s_widget_select_buttons);

        final PreferenceScreen prefSet = getPreferenceScreen();

        // empty our preference category and set it to order as added
        prefSet.removeAll();
        prefSet.setOrderingAsAdded(true);

        // emtpy our checkbox map
        mCheckBoxPrefs.clear();

        // get our list of buttons
        String buttons = Settings.System.getString(getContentResolver(), Settings.System.GALAXY_S_WIDGET_BUTTONS);
        if(buttons == null) { buttons = GalaxySWidgetUtil.BUTTONS_DEFAULT; }
        ArrayList<String> buttonList = GalaxySWidgetUtil.getButtonListFromString(buttons);

        // fill that checkbox map!
        for(GalaxySWidgetUtil.ButtonInfo button : GalaxySWidgetUtil.BUTTONS.values()) {
            // create a checkbox
            CheckBoxPreference cb = new CheckBoxPreference(this);

            // set a dynamic key based on button id
            cb.setKey(SELECT_BUTTON_KEY_PREFIX + button.getId());

            // set vanity info
            cb.setTitle(button.getName());
            cb.setSummaryOn(getString(R.string.summary_on_galaxy_s_widget_select_button_format, button.getName()));
            cb.setSummaryOff(getString(R.string.summary_off_galaxy_s_widget_select_button_format, button.getName()));

            // set our checked state
            if(buttonList.contains(button.getId())) {
                cb.setChecked(true);
            } else {
                cb.setChecked(false);
            }

            // add to our prefs set
            mCheckBoxPrefs.put(cb, button.getId());

            // add to the category
            prefSet.addPreference(cb);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        // we only modify the button list if it was one of our checks that was clicked
        boolean buttonWasModified = false;
        ArrayList<String> buttonList = new ArrayList<String>();
        for(Map.Entry<CheckBoxPreference, String> entry : mCheckBoxPrefs.entrySet()) {
            if(entry.getKey().isChecked()) {
                buttonList.add(entry.getValue());
            }

            if(preference == entry.getKey()) {
                buttonWasModified = true;
            }
        }

        if(buttonWasModified) {
            // get our list of buttons
            String buttons = Settings.System.getString(getContentResolver(), Settings.System.GALAXY_S_WIDGET_BUTTONS);
            if(buttons == null) { buttons = GalaxySWidgetUtil.BUTTONS_DEFAULT; }

            // now we do some wizardry and reset the button list
            Settings.System.putString(getContentResolver(),
                    Settings.System.GALAXY_S_WIDGET_BUTTONS,
                    GalaxySWidgetUtil.mergeInNewButtonString(
                            buttons, GalaxySWidgetUtil.getButtonStringFromList(buttonList)));
            return true;
        }

        return false;
    }
}
