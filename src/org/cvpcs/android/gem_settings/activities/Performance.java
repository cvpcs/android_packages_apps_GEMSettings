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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

public class Performance extends PreferenceActivity {
    private static final String TAG = "GEMSettings[Performance]";

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

        mCPUFreqMinimumPref = (SeekBarStepPreference)prefSet.findPreference(CPUFREQ_MINIMUM);
        mCPUFreqMinimumPref.setSteps(getCPUFreqSpeeds());
        mCPUFreqMaximumPref = (SeekBarStepPreference)prefSet.findPreference(CPUFREQ_MAXIMUM);
        mCPUFreqMaximumPref.setSteps(getCPUFreqSpeeds());
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mServiceCompcachePref) {
            SystemProperties.set(SERVICE_COMPCACHE_PROPERTY,
                    mServiceCompcachePref.isChecked() ? "1" : "0");
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        mServiceCompcachePref.setChecked(SystemProperties.getBoolean(
                SERVICE_COMPCACHE_PROPERTY, false));
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
