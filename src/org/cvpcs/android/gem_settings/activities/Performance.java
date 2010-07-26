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

import org.cvpcs.android.gem_settings.R;
import org.cvpcs.android.gem_settings.utils.CPUFreqStatus;
import org.cvpcs.android.gem_settings.utils.SeekBarStepPreference;

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

import java.io.File;

public class Performance extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "GEMSettings[Performance]";

    private static final String SERVICE_COMPCACHE = "compcache";
    private static final String SERVICE_COMPCACHE_PROPERTY = "persist.service.gem.rzswap.on";

    private static final String CPUFREQ_ENABLE = "cpufreq_enable";
    private static final String CPUFREQ_ENABLE_PROPERTY    = "persist.sys.gem.cpufreq.on";
    private static final String CPUFREQ_GOVERNOR = "cpufreq_governor";
    private static final String CPUFREQ_GOVERNOR_PROPERTY  = "persist.sys.gem.cpufreq.gov";
    private static final String CPUFREQ_MINIMUM = "cpufreq_minimum";
    private static final String CPUFREQ_MINIMUM_PROPERTY   = "persist.sys.gem.cpufreq.min";
    private static final String CPUFREQ_MAXIMUM = "cpufreq_maximum";
    private static final String CPUFREQ_MAXIMUM_PROPERTY   = "persist.sys.gem.cpufreq.max";

    private CheckBoxPreference mServiceCompcachePref;

    private CheckBoxPreference mCPUFreqEnablePref;
    private ListPreference mCPUFreqGovernorPref;
    private SeekBarStepPreference mCPUFreqMinimumPref;
    private SeekBarStepPreference mCPUFreqMaximumPref;

    private int mSwapAvailable = -1;

    private int[] mCPUFreqSpeeds = null;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.performance);

        final PreferenceScreen prefSet = getPreferenceScreen();

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

        mCPUFreqEnablePref = (CheckBoxPreference)prefSet.findPreference(CPUFREQ_ENABLE);
        mCPUFreqGovernorPref = (ListPreference)prefSet.findPreference(CPUFREQ_GOVERNOR);
        mCPUFreqGovernorPref.setEntries(CPUFreqStatus.getGovernors());
        mCPUFreqGovernorPref.setEntryValues(CPUFreqStatus.getGovernors());
        mCPUFreqGovernorPref.setOnPreferenceChangeListener(this);
        mCPUFreqMinimumPref = (SeekBarStepPreference)prefSet.findPreference(CPUFREQ_MINIMUM);
        mCPUFreqMinimumPref.setSteps(CPUFreqStatus.getSpeedSteps());
        mCPUFreqMinimumPref.setOnPreferenceChangeListener(this);
        mCPUFreqMaximumPref = (SeekBarStepPreference)prefSet.findPreference(CPUFREQ_MAXIMUM);
        mCPUFreqMaximumPref.setSteps(CPUFreqStatus.getSpeedSteps());
        mCPUFreqMaximumPref.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mServiceCompcachePref) {
            SystemProperties.set(SERVICE_COMPCACHE_PROPERTY,
                    mServiceCompcachePref.isChecked() ? "1" : "0");
            return true;
        } else if (preference == mCPUFreqEnablePref) {
            SystemProperties.set(CPUFREQ_ENABLE_PROPERTY,
                    mServiceCompcachePref.isChecked() ? "1" : "0");
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();

        mServiceCompcachePref.setChecked(SystemProperties.getBoolean(
                SERVICE_COMPCACHE_PROPERTY, false));

        mCPUFreqEnablePref.setValue(SystemProperties.getBoolean(
                CPUFREQ_ENABLE_PROPERTY, true));
        mCPUFreqGovernorPref.setValue(SystemProperties.get(
                CPUFREQ_GOVERNOR_PROPERTY, CPUFreqStatus.getCurrentGovernor()));
        mCPUFreqMinimumPref.setValue(SystemProperties.getInt(
                CPUFREQ_MINIMUM_PROPERTY, CPUFreqStatus.getCurrentMinimum()));
        mCPUFreqMaximumPref.setValue(SystemProperties.getInt(
                CPUFREQ_MAXIMUM_PROPERTY, CPUFreqStatus.getCurrentMaximum()));
    }

    private boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mCPUFreqGovernorPref) {
            SystemProperties.set(CPUFREQ_GOVERNOR_PROPERTY,
                    mCPUFreqGovernorPref.getValue());
            return true;
        } else if (preference == mCPUFreqMinimumPref) {
            SystemProperties.set(CPUFREQ_MINIMUM_PROPERTY,
                    mCPUFreqMinimumPref.getValue());
            return true;
        } else if (preference == mCPUFreqMaximumPref) {
            SystemProperties.set(CPUFREQ_MAXIMUM_PROPERTY,
                    mCPUFreqMaximumPref.getValue());
            return true;
        }
        return false;
    }

    private boolean isSwapAvailable() {
        if (mSwapAvailable < 0) {
            mSwapAvailable = new File("/proc/swaps").exists() ? 1 : 0;
        }
        return mSwapAvailable > 0;
    }
}
