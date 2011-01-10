/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cvpcs.android.gem_settings.activity;

import org.cvpcs.android.gem_settings.R;
import org.cvpcs.android.gem_settings.util.GalaxySWidgetUtil;
import org.cvpcs.android.gem_settings.widget.TouchInterceptor;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class GalaxySWidgetOrderButtons extends ListActivity
{
    private static final String LOGTAG = "GalaxySWidgetOrderButtons";

    private ListView mButtonList;
    private ButtonAdapter mButtonAdapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.order_galaxy_s_buttons_activity);

        mButtonList = getListView();
        ((TouchInterceptor) mButtonList).setDropListener(mDropListener);
        mButtonAdapter = new ButtonAdapter(this);
        setListAdapter(mButtonAdapter);
    }

    @Override
    public void onDestroy() {
        ((TouchInterceptor) mButtonList).setDropListener(null);
        setListAdapter(null);
        super.onDestroy();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // reload our buttons and invalidate the views for redraw
        mButtonAdapter.reloadButtons();
        mButtonList.invalidateViews();
    }
    
    private TouchInterceptor.DropListener mDropListener = new TouchInterceptor.DropListener() {
            public void drop(int from, int to) {
                // get the current button list
                ArrayList<String> buttons = GalaxySWidgetUtil.getButtonListFromString(
                        GalaxySWidgetUtil.getCurrentButtons(GalaxySWidgetOrderButtons.this));

                // move the button
                if(from < buttons.size()) {
                    String button = buttons.remove(from);

                    if(to <= buttons.size()) {
                        buttons.add(to, button);

                        // save our buttons
                        GalaxySWidgetUtil.saveCurrentButtons(GalaxySWidgetOrderButtons.this,
                                GalaxySWidgetUtil.getButtonStringFromList(buttons));

                        // tell our adapter/listview to reload
                        mButtonAdapter.reloadButtons();
                        mButtonList.invalidateViews();
                    }
                }
            }
        };

    private class ButtonAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;
        private ArrayList<GalaxySWidgetUtil.ButtonInfo> mButtons;

        public ButtonAdapter(Context c) {
            mContext = c;
            mInflater = LayoutInflater.from(mContext);
            reloadButtons();
        }

        public void reloadButtons() {
            ArrayList<String> buttons = GalaxySWidgetUtil.getButtonListFromString(
                    GalaxySWidgetUtil.getCurrentButtons(mContext));

            mButtons = new ArrayList<GalaxySWidgetUtil.ButtonInfo>();
            for(String button : buttons) {
                if(GalaxySWidgetUtil.BUTTONS.containsKey(button)) {
                    mButtons.add(GalaxySWidgetUtil.BUTTONS.get(button));
                }
            }
        }

        public int getCount() {
            return mButtons.size();
        }

        public Object getItem(int position) {
            return mButtons.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final View v;
            if(convertView == null) {
                v = mInflater.inflate(R.layout.order_galaxy_s_button_list_item, null);
            } else {
                v = convertView;
            }

            GalaxySWidgetUtil.ButtonInfo button = mButtons.get(position);

            final TextView name = (TextView)v.findViewById(R.id.name);
            final ImageView icon = (ImageView)v.findViewById(R.id.icon);

            name.setText(button.getName());
            icon.setImageResource(button.getIcon());

            return v;
        }
    }
}

