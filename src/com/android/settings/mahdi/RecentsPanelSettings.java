/*
 *  Copyright (C) 2013 The OmniROM Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.android.settings.mahdi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.WindowManagerGlobal;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import com.android.settings.mahdi.util.Helpers;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class RecentsPanelSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "RecentsPanelSettings";

    private static final String OMNISWITCH_SETTINGS = "omniswitch_settings";
    private static final String OMNISWITCH_PACKAGE_NAME = "org.omnirom.omniswitch";
    private static final String RECENT_MENU_CLEAR_ALL = "recent_menu_clear_all";
    private static final String RECENT_MENU_CLEAR_ALL_LOCATION = "recent_menu_clear_all_location";
    private static final String RECENT_CLEAR_ALL_APPS = "recent_clear_all_apps";
    private static final String CUSTOM_RECENT_MODE = "custom_recent_mode";
    private static final String RECENT_PANEL_SHOW_TOPMOST = "recent_panel_show_topmost";
    private static final String RECENT_PANEL_LEFTY_MODE = "recent_panel_lefty_mode";
    private static final String RECENT_PANEL_SCALE = "recent_panel_scale";
    private static final String RECENT_PANEL_EXPANDED_MODE = "recent_panel_expanded_mode";
    private static final String RECENT_PANEL_BG_COLOR = "recent_panel_bg_color";

    private PreferenceScreen mOmniSwitch;
    private CheckBoxPreference mRecentClearAll;
    private ListPreference mRecentClearAllPosition;
    private CheckBoxPreference mRecentClearAllApps;
    private CheckBoxPreference mRecentsCustom;
    private CheckBoxPreference mRecentsShowTopmost;
    private CheckBoxPreference mRecentPanelLeftyMode;
    private ListPreference mRecentPanelScale;
    private ListPreference mRecentPanelExpandedMode;
    private ColorPickerPreference mRecentPanelBgColor;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DEFAULT_BACKGROUND_COLOR = 0x00ffffff;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.recents_panel_settings);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

        mOmniSwitch = (PreferenceScreen) findPreference(OMNISWITCH_SETTINGS);
        if (!Utils.isPackageInstalled(getActivity(), OMNISWITCH_PACKAGE_NAME)) {
            prefSet.removePreference(mOmniSwitch);
        }

        mRecentClearAll = (CheckBoxPreference) prefSet.findPreference(RECENT_MENU_CLEAR_ALL);
        mRecentClearAll.setChecked(Settings.System.getInt(resolver,
                Settings.System.SHOW_CLEAR_RECENTS_BUTTON, 0) == 1);
        mRecentClearAll.setOnPreferenceChangeListener(this);

        mRecentClearAllPosition = (ListPreference) prefSet.findPreference(RECENT_MENU_CLEAR_ALL_LOCATION);
        String recentClearAllPosition = Settings.System.getString(resolver, Settings.System.CLEAR_RECENTS_BUTTON_LOCATION);
        if (recentClearAllPosition != null) {
            mRecentClearAllPosition.setValue(recentClearAllPosition);
            mRecentClearAllPosition.setSummary(mRecentClearAllPosition.getEntry());
        }
        mRecentClearAllPosition.setOnPreferenceChangeListener(this);

        mRecentClearAllApps = (CheckBoxPreference) prefSet.findPreference(RECENT_CLEAR_ALL_APPS);
        mRecentClearAllApps.setChecked(Settings.System.getInt(resolver,
                Settings.System.CLEAR_ALL_RECENT_APPS, 0) == 1);
        mRecentClearAllApps.setOnPreferenceChangeListener(this);

        boolean enableRecentsCustom = Settings.System.getBoolean(getActivity().getContentResolver(),
                                      Settings.System.CUSTOM_RECENT, false);
        mRecentsCustom = (CheckBoxPreference) findPreference(CUSTOM_RECENT_MODE);
        mRecentsCustom.setChecked(enableRecentsCustom);
        mRecentsCustom.setOnPreferenceChangeListener(this);

        mRecentsShowTopmost = (CheckBoxPreference) prefSet.findPreference(RECENT_PANEL_SHOW_TOPMOST);
        mRecentsShowTopmost.setChecked(Settings.System.getInt(resolver,
                Settings.System.RECENT_PANEL_SHOW_TOPMOST, 0) == 1);
        mRecentsShowTopmost.setOnPreferenceChangeListener(this);

        mRecentPanelLeftyMode = (CheckBoxPreference) findPreference(RECENT_PANEL_LEFTY_MODE);
        mRecentPanelLeftyMode.setOnPreferenceChangeListener(this);

        mRecentPanelScale = (ListPreference) findPreference(RECENT_PANEL_SCALE);
        String recentPanelScale = Settings.System.getString(resolver, Settings.System.RECENT_PANEL_SCALE_FACTOR);
        if (recentPanelScale != null) {
            mRecentPanelScale.setValue(recentPanelScale);
            mRecentPanelScale.setSummary(mRecentPanelScale.getEntry());
        }
        mRecentPanelScale.setOnPreferenceChangeListener(this);

        mRecentPanelExpandedMode = (ListPreference) findPreference(RECENT_PANEL_EXPANDED_MODE);
        String recentPanelExpandedMode = Settings.System.getString(resolver, Settings.System.RECENT_PANEL_EXPANDED_MODE);
        if (recentPanelExpandedMode != null) {
            mRecentPanelExpandedMode.setValue(recentPanelExpandedMode);
            mRecentPanelExpandedMode.setSummary(mRecentPanelExpandedMode.getEntry());
        }
        mRecentPanelExpandedMode.setOnPreferenceChangeListener(this);

        mRecentPanelBgColor = (ColorPickerPreference) findPreference(RECENT_PANEL_BG_COLOR);
        mRecentPanelBgColor.setOnPreferenceChangeListener(this);
        final int intColor = Settings.System.getInt(getContentResolver(),
                Settings.System.RECENT_PANEL_BG_COLOR, 0x00ffffff);
        String hexColor = String.format("#%08x", (0x00ffffff & intColor));
        if (hexColor.equals("#00ffffff")) {
            mRecentPanelBgColor.setSummary(R.string.default_string);
        } else {
            mRecentPanelBgColor.setSummary(hexColor);
        }
        mRecentPanelBgColor.setNewPreviewColor(intColor);
        setHasOptionsMenu(true);
        
        updatePreference();
    }

    private void updatePreference() {
        boolean customRecent = Settings.System.getBoolean(getActivity().getContentResolver(),
                Settings.System.CUSTOM_RECENT, false);

        if (customRecent == false) {
            mRecentsShowTopmost.setEnabled(false);
            mRecentPanelLeftyMode.setEnabled(false);
            mRecentPanelScale.setEnabled(false);
            mRecentPanelExpandedMode.setEnabled(false);
            mRecentPanelBgColor.setEnabled(false);
        } else {
            mRecentsShowTopmost.setEnabled(true);
            mRecentPanelLeftyMode.setEnabled(true);
            mRecentPanelScale.setEnabled(true);
            mRecentPanelExpandedMode.setEnabled(true);
            mRecentPanelBgColor.setEnabled(true);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
       return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mRecentClearAll) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver, Settings.System.SHOW_CLEAR_RECENTS_BUTTON, value ? 1 : 0);
            return true;
        } else if (preference == mRecentClearAllPosition) {
            String value = (String) newValue;
            Settings.System.putString(resolver, Settings.System.CLEAR_RECENTS_BUTTON_LOCATION, value);
            updateRecentClearAllPositionOptions(newValue);
            return true;
        } else if (preference == mRecentClearAllApps) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver, Settings.System.CLEAR_ALL_RECENT_APPS, value ? 1 : 0);
            return true;
        } else if (preference == mRecentsCustom) { // Enable||disable Slim Recent
            Settings.System.putBoolean(resolver,
                    Settings.System.CUSTOM_RECENT,
                    ((Boolean) newValue) ? true : false);
            updatePreference();
            Helpers.restartSystemUI();
            return true;
        } else if (preference == mRecentsShowTopmost) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver, Settings.System.RECENT_PANEL_SHOW_TOPMOST, value ? 1 : 0);
            return true;
        } else if (preference == mRecentPanelScale) {
            int value = Integer.parseInt((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.RECENT_PANEL_SCALE_FACTOR, value);
            updateRecentPanelScaleOptions(newValue);
            return true;
        } else if (preference == mRecentPanelExpandedMode) {
            int value = Integer.parseInt((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.RECENT_PANEL_EXPANDED_MODE, value);
            updateRecentPanelExpandedModeOptions(newValue);
            return true;
        } else if (preference == mRecentPanelLeftyMode) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.RECENT_PANEL_GRAVITY,
                    ((Boolean) newValue) ? Gravity.LEFT : Gravity.RIGHT);
            return true;
        } else if (preference == mRecentPanelBgColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#00ffffff")) {
                preference.setSummary(R.string.default_string);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.RECENT_PANEL_BG_COLOR,
                    intHex);
            return true;
        }
        return false;
    }

    private void updateRecentPanelScaleOptions(Object newValue) {
        int index = mRecentPanelScale.findIndexOfValue((String) newValue);
        int value = Integer.valueOf((String) newValue);
        Settings.Secure.putInt(getActivity().getContentResolver(),
                Settings.System.RECENT_PANEL_SCALE_FACTOR, value);
        mRecentPanelScale.setSummary(mRecentPanelScale.getEntries()[index]);
    }

    private void updateRecentPanelExpandedModeOptions(Object newValue) {
        int index = mRecentPanelExpandedMode.findIndexOfValue((String) newValue);
        int value = Integer.valueOf((String) newValue);
        Settings.Secure.putInt(getActivity().getContentResolver(),
                Settings.System.RECENT_PANEL_EXPANDED_MODE, value);
        mRecentPanelExpandedMode.setSummary(mRecentPanelExpandedMode.getEntries()[index]);
    }

    private void updateRecentClearAllPositionOptions(Object newValue) {
        int index = mRecentClearAllPosition.findIndexOfValue((String) newValue);
        int value = Integer.valueOf((String) newValue);
        Settings.Secure.putInt(getActivity().getContentResolver(),
                Settings.System.CLEAR_RECENTS_BUTTON_LOCATION, value);
        mRecentClearAllPosition.setSummary(mRecentClearAllPosition.getEntries()[index]);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_settings_backup)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                resetToDefault();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void resetToDefault() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.shortcut_action_reset);
        alertDialog.setMessage(R.string.qs_style_reset_message);
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                resetValues();
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, null);
        alertDialog.create().show();
    }

    private void resetValues() {
        Settings.System.putInt(getContentResolver(),
                Settings.System.RECENT_PANEL_BG_COLOR, DEFAULT_BACKGROUND_COLOR);
        mRecentPanelBgColor.setNewPreviewColor(DEFAULT_BACKGROUND_COLOR);
        mRecentPanelBgColor.setSummary(R.string.default_string);
    }
}
