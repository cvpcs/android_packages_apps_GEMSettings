package org.cvpcs.android.gem_settings.util;

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
    public static final String BUTTON_WIFI      = "wifi";
    public static final String BUTTON_WIFI_NAME = "Wifi";
    public static final String BUTTON_GPS      = "gps";
    public static final String BUTTON_GPS_NAME = "GPS";
    public static final String BUTTON_BLUETOOTH      = "bluetooth";
    public static final String BUTTON_BLUETOOTH_NAME = "Bluetooth";
    public static final String BUTTON_BRIGHTNESS      = "brightness";
    public static final String BUTTON_BRIGHTNESS_NAME = "Screen Brightness";
    public static final String BUTTON_SOUND      = "sound";
    public static final String BUTTON_SOUND_NAME = "Sound";
    public static final String BUTTON_SYNC      = "sync";
    public static final String BUTTON_SYNC_NAME = "Sync";
    public static final String BUTTON_WIFIAP      = "wifiap";
    public static final String BUTTON_WIFIAP_NAME = "Wifi Access Point";
    public static final String BUTTON_SCREENTIMEOUT      = "screentimeout";
    public static final String BUTTON_SCREENTIMEOUT_NAME = "Screen Timeout";
    public static final String BUTTON_MOBILEDATA      = "mobiledata";
    public static final String BUTTON_MOBILEDATA_NAME = "Mobile Data";
    public static final String BUTTON_LOCKSCREEN      = "lockscreen";
    public static final String BUTTON_LOCKSCREEN_NAME = "Lockscreen";
    public static final String BUTTON_NETWORKMODE      = "networkmode";
    public static final String BUTTON_NETWORKMODE_NAME = "Network Mode";
    public static final String BUTTON_AUTOROTATE      = "autorotate";
    public static final String BUTTON_AUTOROTATE_NAME = "Auto-Rotate";
    public static final String BUTTON_AIRPLANE      = "airplane";
    public static final String BUTTON_AIRPLANE_NAME = "Airplane Mode";
    public static final String BUTTON_FLASHLIGHT      = "flashlight";
    public static final String BUTTON_FLASHLIGHT_NAME = "Flashlight";
    public static final String BUTTON_SLEEP      = "sleep";
    public static final String BUTTON_SLEEP_NAME = "Sleep";
    public static final String BUTTON_MEDIA_PLAY_PAUSE      = "media_play_pause";
    public static final String BUTTON_MEDIA_PLAY_PAUSE_NAME = "Play/Pause Media";
    public static final String BUTTON_MEDIA_PREVIOUS      = "media_previous";
    public static final String BUTTON_MEDIA_PREVIOUS_NAME = "Seek to Previous Media";
    public static final String BUTTON_MEDIA_NEXT      = "media_next";
    public static final String BUTTON_MEDIA_NEXT_NAME = "Seek to Next Media";
    public static final HashMap<String, ButtonInfo> BUTTONS = new HashMap<String, ButtonInfo>();
    static {
        BUTTONS.put(BUTTON_WIFI, new GalaxySWidgetUtil.ButtonInfo(BUTTON_WIFI, BUTTON_WIFI_NAME));
        BUTTONS.put(BUTTON_GPS, new GalaxySWidgetUtil.ButtonInfo(BUTTON_GPS, BUTTON_GPS_NAME));
        BUTTONS.put(BUTTON_BLUETOOTH, new GalaxySWidgetUtil.ButtonInfo(BUTTON_BLUETOOTH, BUTTON_BLUETOOTH_NAME));
        BUTTONS.put(BUTTON_BRIGHTNESS, new GalaxySWidgetUtil.ButtonInfo(BUTTON_BRIGHTNESS, BUTTON_BRIGHTNESS_NAME));
        BUTTONS.put(BUTTON_SOUND, new GalaxySWidgetUtil.ButtonInfo(BUTTON_SOUND, BUTTON_SOUND_NAME));
        BUTTONS.put(BUTTON_SYNC, new GalaxySWidgetUtil.ButtonInfo(BUTTON_SYNC, BUTTON_SYNC_NAME));
        BUTTONS.put(BUTTON_WIFIAP, new GalaxySWidgetUtil.ButtonInfo(BUTTON_WIFIAP, BUTTON_WIFIAP_NAME));
        BUTTONS.put(BUTTON_SCREENTIMEOUT, new GalaxySWidgetUtil.ButtonInfo(BUTTON_SCREENTIMEOUT, BUTTON_SCREENTIMEOUT_NAME));
        BUTTONS.put(BUTTON_MOBILEDATA, new GalaxySWidgetUtil.ButtonInfo(BUTTON_MOBILEDATA, BUTTON_MOBILEDATA_NAME));
        BUTTONS.put(BUTTON_LOCKSCREEN, new GalaxySWidgetUtil.ButtonInfo(BUTTON_LOCKSCREEN, BUTTON_LOCKSCREEN_NAME));
        BUTTONS.put(BUTTON_NETWORKMODE, new GalaxySWidgetUtil.ButtonInfo(BUTTON_NETWORKMODE, BUTTON_NETWORKMODE_NAME));
        BUTTONS.put(BUTTON_AUTOROTATE, new GalaxySWidgetUtil.ButtonInfo(BUTTON_AUTOROTATE, BUTTON_AUTOROTATE_NAME));
        BUTTONS.put(BUTTON_AIRPLANE, new GalaxySWidgetUtil.ButtonInfo(BUTTON_AIRPLANE, BUTTON_AIRPLANE_NAME));
        BUTTONS.put(BUTTON_FLASHLIGHT, new GalaxySWidgetUtil.ButtonInfo(BUTTON_FLASHLIGHT, BUTTON_FLASHLIGHT_NAME));
        BUTTONS.put(BUTTON_SLEEP, new GalaxySWidgetUtil.ButtonInfo(BUTTON_SLEEP, BUTTON_SLEEP_NAME));
        BUTTONS.put(BUTTON_MEDIA_PLAY_PAUSE, new GalaxySWidgetUtil.ButtonInfo(BUTTON_MEDIA_PLAY_PAUSE, BUTTON_MEDIA_PLAY_PAUSE_NAME));
        BUTTONS.put(BUTTON_MEDIA_PREVIOUS, new GalaxySWidgetUtil.ButtonInfo(BUTTON_MEDIA_PREVIOUS, BUTTON_MEDIA_PREVIOUS_NAME));
        BUTTONS.put(BUTTON_MEDIA_NEXT, new GalaxySWidgetUtil.ButtonInfo(BUTTON_MEDIA_NEXT, BUTTON_MEDIA_NEXT_NAME));
    }

    private static final String BUTTON_DELIMITER = "|";
    public static final String BUTTONS_DEFAULT = BUTTON_WIFI
                             + BUTTON_DELIMITER + BUTTON_BLUETOOTH
                             + BUTTON_DELIMITER + BUTTON_GPS
                             + BUTTON_DELIMITER + BUTTON_SOUND;

    public static String mergeInNewButtonString(String oldString, String newString) {
        ArrayList<String> oldList = getButtonListFromString(oldString);
        ArrayList<String> newList = getButtonListFromString(newString);

        // remove all items in old list that aren't in new list
        for(String button : oldList) {
            if(!newList.contains(button)) {
                oldList.remove(button);
            }
        }

        // append anything in newlist that isn't already in old list to the end of old list
        for(String button : newList) {
            if(!oldList.contains(button)) {
                oldList.add(button);
            }
        }

        // return modified old list
        return getButtonStringFromList(oldList);
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

        public ButtonInfo(String id, String name) {
            mId = id;
            mName = name;
        }

        public String getId() { return mId; }
        public String getName() { return mName; }
    }
}
