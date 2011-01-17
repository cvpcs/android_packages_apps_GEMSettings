package org.cvpcs.android.gem_settings.util;

import android.content.Context;
import android.provider.Settings;

import com.android.internal.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * THIS CLASS'S DATA MUST BE KEPT UP-TO-DATE WITH THE DATA IN
 * com.android.server.status.galaxyswidget.PowerManager AND
 * com.android.server.status.galaxyswidget.GalaxySWidget IN THE
 * services.jar PACKAGE.
 */
public class GalaxySWidgetUtil {
    public static final String BUTTON_AIRPLANE         = "airplane";
    public static final String BUTTON_AUTOROTATE       = "autorotate";
    public static final String BUTTON_BLUETOOTH        = "bluetooth";
    public static final String BUTTON_BRIGHTNESS       = "brightness";
    public static final String BUTTON_FLASHLIGHT       = "flashlight";
    public static final String BUTTON_GPS              = "gps";
    public static final String BUTTON_LOCKSCREEN       = "lockscreen";
    public static final String BUTTON_MOBILEDATA       = "mobiledata";
    public static final String BUTTON_NETWORKMODE      = "networkmode";
    public static final String BUTTON_SCREENTIMEOUT    = "screentimeout";
    public static final String BUTTON_SLEEP            = "sleep";
    public static final String BUTTON_SOUND            = "sound";
    public static final String BUTTON_SYNC             = "sync";
    public static final String BUTTON_WIFI             = "wifi";
    public static final String BUTTON_WIFIAP           = "wifiap";
    public static final String BUTTON_MEDIA_PLAY_PAUSE = "media_play_pause";
    public static final String BUTTON_MEDIA_PREVIOUS   = "media_previous";
    public static final String BUTTON_MEDIA_NEXT       = "media_next";
    public static final HashMap<String, ButtonInfo> BUTTONS = new HashMap<String, ButtonInfo>();
    static {
        BUTTONS.put(BUTTON_AIRPLANE, new GalaxySWidgetUtil.ButtonInfo(
                BUTTON_AIRPLANE, "Airplane mode", R.drawable.stat_airplane_on));
        BUTTONS.put(BUTTON_AUTOROTATE, new GalaxySWidgetUtil.ButtonInfo(
                BUTTON_AUTOROTATE, "Auto-rotate", R.drawable.stat_orientation_on));
        BUTTONS.put(BUTTON_BLUETOOTH, new GalaxySWidgetUtil.ButtonInfo(
                BUTTON_BLUETOOTH, "Bluetooth", R.drawable.stat_bluetooth_on));
        BUTTONS.put(BUTTON_BRIGHTNESS, new GalaxySWidgetUtil.ButtonInfo(
                BUTTON_BRIGHTNESS, "Brightness", R.drawable.stat_brightness_on));
        BUTTONS.put(BUTTON_FLASHLIGHT, new GalaxySWidgetUtil.ButtonInfo(
                BUTTON_FLASHLIGHT, "Flashlight", R.drawable.stat_flashlight_on));
        BUTTONS.put(BUTTON_GPS, new GalaxySWidgetUtil.ButtonInfo(
                BUTTON_GPS, "GPS", R.drawable.stat_gps_on));
        BUTTONS.put(BUTTON_LOCKSCREEN, new GalaxySWidgetUtil.ButtonInfo(
                BUTTON_LOCKSCREEN, "Lockscreen", R.drawable.stat_lock_screen_on));
        BUTTONS.put(BUTTON_MOBILEDATA, new GalaxySWidgetUtil.ButtonInfo(
                BUTTON_MOBILEDATA, "Mobile data", R.drawable.stat_data_on));
/*        BUTTONS.put(BUTTON_NETWORKMODE, new GalaxySWidgetUtil.ButtonInfo(
                BUTTON_NETWORKMODE, "Network mode", R.drawable.stat_2g3g_on));*/
        BUTTONS.put(BUTTON_SCREENTIMEOUT, new GalaxySWidgetUtil.ButtonInfo(
                BUTTON_SCREENTIMEOUT, "Screen timeout", R.drawable.stat_screen_timeout_on));
        BUTTONS.put(BUTTON_SLEEP, new GalaxySWidgetUtil.ButtonInfo(
                BUTTON_SLEEP, "Sleep", R.drawable.stat_sleep));
        BUTTONS.put(BUTTON_SOUND, new GalaxySWidgetUtil.ButtonInfo(
                BUTTON_SOUND, "Sound", R.drawable.stat_ring_on));
        BUTTONS.put(BUTTON_SYNC, new GalaxySWidgetUtil.ButtonInfo(
                BUTTON_SYNC, "Sync", R.drawable.stat_sync_on));
        BUTTONS.put(BUTTON_WIFI, new GalaxySWidgetUtil.ButtonInfo(
                BUTTON_WIFI, "Wireless", R.drawable.stat_wifi_on));
/*        BUTTONS.put(BUTTON_WIFIAP, new GalaxySWidgetUtil.ButtonInfo(
                BUTTON_WIFIAP, "Wireless access-point", R.drawable.stat_wifi_ap_on));*/
        BUTTONS.put(BUTTON_MEDIA_PREVIOUS, new GalaxySWidgetUtil.ButtonInfo(
                BUTTON_MEDIA_PREVIOUS, "Media: skip to previous", R.drawable.stat_media_previous));
        BUTTONS.put(BUTTON_MEDIA_PLAY_PAUSE, new GalaxySWidgetUtil.ButtonInfo(
                BUTTON_MEDIA_PLAY_PAUSE, "Media: play/pause", R.drawable.stat_media_play));
        BUTTONS.put(BUTTON_MEDIA_NEXT, new GalaxySWidgetUtil.ButtonInfo(
                BUTTON_MEDIA_NEXT, "Media: skip to next", R.drawable.stat_media_next));
    }

    private static final String BUTTON_DELIMITER = "|";
    private static final String BUTTONS_DEFAULT = BUTTON_WIFI
                             + BUTTON_DELIMITER + BUTTON_BLUETOOTH
                             + BUTTON_DELIMITER + BUTTON_GPS
                             + BUTTON_DELIMITER + BUTTON_SOUND;

    public static String getCurrentButtons(Context context) {
        String buttons = Settings.System.getString(context.getContentResolver(), Settings.System.GALAXY_S_WIDGET_BUTTONS);
        if(buttons == null) { buttons = BUTTONS_DEFAULT; }
        return buttons;
    }

    public static void saveCurrentButtons(Context context, String buttons) {
        Settings.System.putString(context.getContentResolver(),
                Settings.System.GALAXY_S_WIDGET_BUTTONS, buttons);
    }

    public static String mergeInNewButtonString(String oldString, String newString) {
        ArrayList<String> oldList = getButtonListFromString(oldString);
        ArrayList<String> newList = getButtonListFromString(newString);
        ArrayList<String> mergedList = new ArrayList<String>();

        // add any items from oldlist that are in new list
        for(String button : oldList) {
            if(newList.contains(button)) {
                mergedList.add(button);
            }
        }

        // append anything in newlist that isn't already in the merged list to the end of the list
        for(String button : newList) {
            if(!mergedList.contains(button)) {
                mergedList.add(button);
            }
        }

        // return merged list
        return getButtonStringFromList(mergedList);
    }

    public static ArrayList<String> getButtonListFromString(String buttons) {
        return new ArrayList<String>(Arrays.asList(buttons.split("\\|")));
    }

    public static String getButtonStringFromList(ArrayList<String> buttons) {
        if(buttons == null || buttons.size() <= 0) {
            return "";
        } else {
            String s = buttons.get(0);
            for(int i = 1; i < buttons.size(); i++) {
                s += BUTTON_DELIMITER + buttons.get(i);
            }
            return s;
        }
    }

    public static class ButtonInfo {
        private String mId;
        private String mName;
        private int mIcon;

        public ButtonInfo(String id, String name, int icon) {
            mId = id;
            mName = name;
            mIcon = icon;
        }

        public String getId() { return mId; }
        public String getName() { return mName; }
        public int getIcon() { return mIcon; }
    }
}
