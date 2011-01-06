package org.cvpcs.android.gem_settings.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.LinearLayout;

public class SeekBarStepPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener
{
    private static final String androidns="http://schemas.android.com/apk/res/android";
    private static final String cvpcsns="http://schemas.cvpcs.org/apk/res/android";

    private SeekBar mSeekBar;

    private TextView mSplashText;
    private TextView mValueText;

    private Context mContext;

    private String mDialogMessage;
    private String mPrefix;
    private String mSuffix;

    private int mDefault = 0;
    private int[] mSteps;
    private int mValue = 0;
    private int mInitialValue = 0;
    private boolean mValueSet = false;

    private DisplayValueConverter mDisplayValueConverter = null;

    public SeekBarStepPreference(Context context, AttributeSet attrs) {
        super(context,attrs);

        mContext = context;

        mDialogMessage = attrs.getAttributeValue(cvpcsns, "dialogMessage");
        mPrefix = attrs.getAttributeValue(cvpcsns, "prefixText");
        mSuffix = attrs.getAttributeValue(cvpcsns, "suffixText");

        mSteps = new int[] {0, 25, 50, 75, 100};
        String stepResId = attrs.getAttributeValue(cvpcsns, "steps");
        if (stepResId != null) {
            Resources res = context.getResources();
            int id = res.getIdentifier(stepResId, null, "org.cvpcs.android.gem_settings");
            if (id > 0) {
                mSteps = res.getIntArray(id);
            }
        }

        mDefault = getNearestStep(
                attrs.getAttributeIntValue(cvpcsns, "defaultValue", 0)
                );
    }

    @Override
    protected View onCreateDialogView() {
        LinearLayout layout = new LinearLayout(mContext);

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(6,6,6,6);

        mSplashText = new TextView(mContext);
        if (mDialogMessage != null)
            mSplashText.setText(mDialogMessage);
        layout.addView(mSplashText);

        mValueText = new TextView(mContext);
        mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
        mValueText.setTextSize(32);

        LinearLayout.LayoutParams valueTextParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.FILL_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(mValueText, valueTextParams);

        mSeekBar = new SeekBar(mContext);
        mSeekBar.setOnSeekBarChangeListener(this);

        LinearLayout.LayoutParams seekBarParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.FILL_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(mSeekBar, seekBarParams);

        if (!mValueSet) {
            if (shouldPersist())
                mValue = getPersistedInt(mDefault);
            else
                mValue = mDefault;
        }

        // store this for later
        mInitialValue = mValue;

        String t = String.valueOf((mDisplayValueConverter == null) ?
                mValue : mDisplayValueConverter.convertValueForDisplay(mValue));

        if(mPrefix != null)
            t = mPrefix.concat(t);
        if(mSuffix != null)
            t = t.concat(mSuffix);

        mValueText.setText(t);

        mSeekBar.setMax(mSteps[mSteps.length - 1] - mSteps[0]);
        mSeekBar.setProgress(mValue - mSteps[0]);

        return layout;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            if (shouldPersist())
                persistInt(mValue);

            callChangeListener(new Integer(mValue));
        } else {
            // reset our value
            mValue = mInitialValue;
        }
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        mSeekBar.setMax(mSteps[mSteps.length - 1] - mSteps[0]);
        mSeekBar.setProgress(mValue - mSteps[0]);
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue)
    {
        super.onSetInitialValue(restore, defaultValue);

        if (!mValueSet) {
            if (restore)
                mValue = shouldPersist() ? getPersistedInt(mDefault) : mDefault;
            else
                mValue = (Integer)defaultValue;
        }

        mValue = getNearestStep(mValue);
    }

    public void onProgressChanged(SeekBar seek, int value, boolean fromTouch)
    {
        setValue(value + mSteps[0]);

        String t = String.valueOf((mDisplayValueConverter == null) ?
                mValue : mDisplayValueConverter.convertValueForDisplay(mValue));

        if(mPrefix != null)
            t = mPrefix.concat(t);
        if(mSuffix != null)
            t = t.concat(mSuffix);

        mValueText.setText(t);
    }

    public void onStartTrackingTouch(SeekBar seek) { }

    public void onStopTrackingTouch(SeekBar seek) { }

    public void setValue(int value) {
        mValue = getNearestStep(value);
        mValueSet = true;
        if (mSeekBar != null)
            mSeekBar.setProgress(mValue - mSteps[0]);
    }

    public int getValue() { return mValue; }

    public void setSteps(int[] steps) {
        mSteps = steps;
        mValue = getNearestStep(mValue);
        mDefault = getNearestStep(mDefault);
        if(mSeekBar != null) {
            mSeekBar.setMax(mSteps[mSteps.length - 1] - mSteps[0]);
            mSeekBar.setProgress(mValue - mSteps[0]);
        }
    }

    private int getNearestStep(int value) {
        for (int i = 0; i < mSteps.length - 1; i++) {
            if (mSteps[i] <= value && value <= mSteps[i + 1]) {
                int result = Math.round(
                        ((float)(value - mSteps[i])) /
                        ((float)(mSteps[i + 1] - mSteps[i]))
                        );

                if (result > 0) {
                    return mSteps[i + 1];
                } else {
                    return mSteps[i];
                }
            }
        }

        if (value <= mSteps[0]) {
            return mSteps[0];
        } else {
            return mSteps[mSteps.length - 1];
        }
    }

    public void setDisplayValueConverter(DisplayValueConverter dvc) {
        mDisplayValueConverter = dvc;
    }

    public interface DisplayValueConverter {
        public int convertValueForDisplay(int value);
    }
}
