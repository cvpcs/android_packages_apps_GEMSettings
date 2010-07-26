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

package org.cvpcs.android.gem_settings.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

public class CPUFreqStatus {
    private static final String TAG = "GEMSettings[CPUFreqStatus]";

    private static final String FILE_STEPS = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies";
    private static final String FILE_GOVERNORS = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors";
    private static final String FILE_CURRENT_GOVERNOR = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
    private static final String FILE_MAX_SPEED = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq";
    private static final String FILE_MIN_SPEED = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq";

    private static int[] SPEED_STEPS = null;
    private static String[] GOVERNORS = null;

    public static String[] getGovernors() {
        if (GOVERNORS == null) {
            String govs = getFileContents(FILE_GOVERNORS);

            if (govs != null) {
                String[] govList = govs.trim().split("[ \n]+");
                GOVERNORS = new String[govList.length];

                for (int i = 0; i < govList.length; i++) {
                    GOVERNORS[i] = govList[i].trim();
                }

                Arrays.sort(GOVERNORS);
            } else {
                // catchall, this is overwritten if we can
                GOVERNORS =  new String[] { "performance" };
            }

            for (int i = 0; i < GOVERNORS.length; i++) {
                Log.i(TAG, "Found governor: [" + i + "] = " + GOVERNORS[i]);
            }
        }

        return GOVERNORS;
    }

    public static int[] getSpeedSteps() {
        if (SPEED_STEPS == null) {
            String freqs = getFileContents(FILE_STEPS);

            if (freqs != null) {
                String[] freqList = freqs.trim().split("[ \n]+");
                SPEED_STEPS = new int[freqList.length];

                for (int i = 0; i < freqList.length; i++) {
                    SPEED_STEPS[i] = Integer.parseInt(freqList[i]);
                }

                Arrays.sort(SPEED_STEPS);
            } else {
                // catchall, this is overwritten if we can
                SPEED_STEPS =  new int[] { 125000, 400000, 500000, 600000, 800000, 900000, 1000000 };
            }

            for (int i = 0; i < SPEED_STEPS.length; i++) {
                Log.i(TAG, "Found speed step: [" + i + "] = " + SPEED_STEPS[i]);
            }
        }

        return SPEED_STEPS;
    }

    public static int getCurrentGovernor() {
        String gov = getFileContents(FILE_CURRENT_GOVERNOR);
        if (gov != null) {
            Log.i(TAG, "Found governor: [" + speed + "]");
            return gov.trim();
        }

        // bad things happened, so return null
        return null;
    }

    public static int getCurrentMaximum() {
        String speed = getFileContents(FILE_MAX_SPEED);
        if (speed != null) {
            Log.i(TAG, "Found maximum speed: [" + speed + "]");
            return Integer.parseInt(speed.trim());
        }

        // bad things happened, so return negative
        return -1
    }

    public static int getCurrentMinimum() {
        String speed = getFileContents(FILE_MIN_SPEED);
        if (speed != null) {
            Log.i(TAG, "Found minimum speed: [" + speed + "]");
            return Integer.parseInt(speed.trim());
        }

        // bad things happened, so return negative
        return -1
    }

    private static String getFileContents(String filename) {
        String s = "";
        File f = new File(filename);

        if (f.exists() && f.canRead()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));

                String buffer = null;

                while((buffer = br.readLine()) != null) {
                    s += buffer + "\n";
                }

                br.close();
            } catch (Exception e) {
                Log.e(TAG, "Exception thrown when reading file [" + filename + "]", e);
                s = null;
            }
        }

        return s;
    }
}
