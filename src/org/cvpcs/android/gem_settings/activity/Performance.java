/*
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
import org.cvpcs.android.gem_settings.util.CPUFreqStatus;
import org.cvpcs.android.gem_settings.widget.SeekBarStepPreference;

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
import android.widget.Toast;

import java.io.File;

public class Performance extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "GEMSettings[Performance]";

    private static final String SERVICE_COMPCACHE = "compcache";
    private static final String SERVICE_COMPCACHE_PROPERTY = "persist.gem.rzswap.on";

    private static final String CPUFREQ_ENABLE = "cpufreq_enable";
    private static final String CPUFREQ_ENABLE_PROPERTY    = "persist.gem.cpufreq.on";
    private static final String CPUFREQ_GOVERNOR = "cpufreq_governor";
    private static final String CPUFREQ_GOVERNOR_PROPERTY  = "persist.gem.cpufreq.gov";
    private static final String CPUFREQ_MINIMUM = "cpufreq_minimum";
    private static final String CPUFREQ_MINIMUM_PROPERTY   = "persist.gem.cpufreq.min";
    private static final String CPUFREQ_MAXIMUM = "cpufreq_maximum";
    private static final String CPUFREQ_MAXIMUM_PROPERTY   = "persist.gem.cpufreq.max";

    private CheckBoxPreference mServiceCompcachePref;

    private CheckBoxPreference mCPUFreqEnablePref;
    private ListPreference mCPUFreqGovernorPref;
    private SeekBarStepPreference mCPUFreqMinimumPref;
    private SeekBarStepPreference mCPUFreqMaximumPref;

    private static final File SWAPS_FILE = new File("/proc/swaps");
    private static final File RAMZSWAP_FILE = new File("/system/lib/modules/ramzswap.ko");
    private int mSwapAvailable = -1;
    private int mRamzSwapAvailable = -1;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.performance);

        final PreferenceScreen prefSet = getPreferenceScreen();

        mServiceCompcachePref = (CheckBoxPreference)prefSet.findPreference(SERVICE_COMPCACHE);
        mServiceCompcachePref.setChecked(SystemProperties.getBoolean(
                SERVICE_COMPCACHE_PROPERTY, false));
        if(!isCompcacheAvailable()) {
            // disable compcache but display a message so people know why it's not working
            mServiceCompcachePref.setEnabled(false);
            mServiceCompcachePref.setChecked(false);
            mServiceCompcachePref.setSummaryOn("Compcache is not supported in your kernel");
            mServiceCompcachePref.setSummaryOff("Compcache is not supported in your kernel");
            SystemProperties.set(SERVICE_COMPCACHE_PROPERTY,"0");
            Log.i(TAG, "Disabling compcache due to lack of swap support in kernel");
        }

        mCPUFreqEnablePref = (CheckBoxPreference)prefSet.findPreference(CPUFREQ_ENABLE);
        mCPUFreqEnablePref.setChecked(SystemProperties.getBoolean(
                CPUFREQ_ENABLE_PROPERTY, true));

        mCPUFreqGovernorPref = (ListPreference)prefSet.findPreference(CPUFREQ_GOVERNOR);
        mCPUFreqGovernorPref.setEntries(CPUFreqStatus.getGovernors());
        mCPUFreqGovernorPref.setEntryValues(CPUFreqStatus.getGovernors());
        mCPUFreqGovernorPref.setValue(SystemProperties.get(
                CPUFREQ_GOVERNOR_PROPERTY, CPUFreqStatus.getCurrentGovernor()));
        mCPUFreqGovernorPref.setOnPreferenceChangeListener(this);

        SeekBarStepPreference.DisplayValueConverter mdvc = new HzToMhzDisplayValueConverter();

        mCPUFreqMinimumPref = (SeekBarStepPreference)prefSet.findPreference(CPUFREQ_MINIMUM);
        mCPUFreqMinimumPref.setDisplayValueConverter(mdvc);
        mCPUFreqMinimumPref.setSteps(CPUFreqStatus.getSpeedSteps());
        mCPUFreqMinimumPref.setValue(SystemProperties.getInt(
                CPUFREQ_MINIMUM_PROPERTY, CPUFreqStatus.getCurrentMinimum()));
        mCPUFreqMinimumPref.setOnPreferenceChangeListener(this);

        mCPUFreqMaximumPref = (SeekBarStepPreference)prefSet.findPreference(CPUFREQ_MAXIMUM);
        mCPUFreqMaximumPref.setDisplayValueConverter(mdvc);
        mCPUFreqMaximumPref.setSteps(CPUFreqStatus.getSpeedSteps());
        mCPUFreqMaximumPref.setValue(SystemProperties.getInt(
                CPUFREQ_MAXIMUM_PROPERTY, CPUFreqStatus.getCurrentMaximum()));
        mCPUFreqMaximumPref.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if(preference == mServiceCompcachePref) {
            SystemProperties.set(SERVICE_COMPCACHE_PROPERTY,
                    mServiceCompcachePref.isChecked() ? "1" : "0");
            return true;
        } else if(preference == mCPUFreqEnablePref) {
            SystemProperties.set(CPUFREQ_ENABLE_PROPERTY,
                    mCPUFreqEnablePref.isChecked() ? "1" : "0");
            return true;
        }

        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        int newValue = Integer.valueOf((String)objValue);
        if(preference == mCPUFreqGovernorPref) {
            SystemProperties.set(CPUFREQ_GOVERNOR_PROPERTY,
                    Integer.toString(newValue));
            return true;
        }

        if(preference == mCPUFreqMinimumPref) {
            SystemProperties.set(CPUFREQ_MINIMUM_PROPERTY,
                    Integer.toString(newValue));
            checkCPUSpeeds();
            return true;
        }

        if(preference == mCPUFreqMaximumPref) {
            SystemProperties.set(CPUFREQ_MAXIMUM_PROPERTY,
                    Integer.toString(newValue));
            checkCPUSpeeds();
            return true;
        }

        return false;
    }

    private boolean isCompcacheAvailable() {
        if (mSwapAvailable < 0) {
            mSwapAvailable = SWAPS_FILE.exists() ? 1 : 0;
        }

        if (mRamzSwapAvailable < 0) {
            mRamzSwapAvailable = RAMZSWAP_FILE.exists() ? 1 : 0;
        }

        return (mSwapAvailable > 0) && (mRamzSwapAvailable > 0);
    }

    private void checkCPUSpeeds() {
        int min = mCPUFreqMinimumPref.getValue();
        int max = mCPUFreqMaximumPref.getValue();

        if (min > max) {
            Toast.makeText(this, "CPU Min > Max, Min will be leveled off at the max speed upon boot.", Toast.LENGTH_LONG).show();
        }
    }

    private class HzToMhzDisplayValueConverter
            implements SeekBarStepPreference.DisplayValueConverter {
        public int convertValueForDisplay(int value) {
            return (value / 1000);
        }
    }
}
