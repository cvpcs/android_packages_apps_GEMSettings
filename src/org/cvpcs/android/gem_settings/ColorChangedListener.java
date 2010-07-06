package org.cvpcs.android.gem_settings;

import android.app.ColorPickerDialog;
import android.preference.PreferenceActivity;
import android.provider.Settings;

public class ColorChangedListener implements ColorPickerDialog.OnColorChangedListener {
    private PreferenceActivity mActivity;
    private String mSetting;

    public ColorChangedListener(PreferenceActivity activity, String setting) {
        mActivity = activity;
        mSetting = setting;
    }

    public void colorChanged(int color) {
        Settings.System.putInt(mActivity.getContentResolver(), mSetting, color);
    }
}
