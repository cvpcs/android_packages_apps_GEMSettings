package org.cvpcs.android.gem_settings.widget;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.LinearLayout;

public class SeekBarPreference extends DialogPreference
        implements SeekBar.OnSeekBarChangeListener {
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
    private int mMinimum = 0;
    private int mMaximum = 0;
    private int mValue = 0;

    private DisplayValueConverter mDisplayValueConverter = null;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context,attrs);

        mContext = context;

        mDialogMessage = attrs.getAttributeValue(cvpcsns,"dialogMessage");
        mPrefix = attrs.getAttributeValue(cvpcsns,"prefixText");
        mSuffix = attrs.getAttributeValue(cvpcsns,"suffixText");

        mDefault = attrs.getAttributeIntValue(cvpcsns,"defaultValue", 0);
        mMinimum = attrs.getAttributeIntValue(cvpcsns,"min", 0);
        mMaximum = attrs.getAttributeIntValue(cvpcsns,"max", 100);
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

        if (shouldPersist())
            mValue = getPersistedInt(mDefault);

        mSeekBar.setMax(mMaximum - mMinimum);
        mSeekBar.setProgress(mValue - mMinimum);

        return layout;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        mSeekBar.setMax(mMaximum - mMinimum);
        mSeekBar.setProgress(mValue - mMinimum);
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue)
    {
        super.onSetInitialValue(restore, defaultValue);

        if (restore)
            mValue = shouldPersist() ? getPersistedInt(mDefault) : mDefault;
        else
            mValue = (Integer)defaultValue;
    }

    public void onProgressChanged(SeekBar seek, int value, boolean fromTouch)
    {
        mValue = value + mMinimum;

        String t = String.valueOf((mDisplayValueConverter == null) ?
                mValue : mDisplayValueConverter.convertValueForDisplay(mValue));

        if(mPrefix != null)
            t = mPrefix.concat(t);
        if(mSuffix != null)
            t = t.concat(mSuffix);

        mValueText.setText(t);

        if (shouldPersist())
            persistInt(mValue);

        callChangeListener(new Integer(mValue));
    }

    public void onStartTrackingTouch(SeekBar seek) { }
    public void onStopTrackingTouch(SeekBar seek) { }

    public void setValue(int value) {
        mValue = value;
        if (mSeekBar != null)
            mSeekBar.setProgress(value - mMinimum);
    }

    public int getValue() { return mValue; }

    public void setDisplayValueConverter(DisplayValueConverter dvc) {
        mDisplayValueConverter = dvc;
    }

    public interface DisplayValueConverter {
        public int convertValueForDisplay(int value);
    }
}
