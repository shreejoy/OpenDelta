/*
 * Copyright (C) 2013-2014 Jorrit "Chainfire" Jongma
 * Copyright (C) 2013-2015 The OmniROM Project
 */
/*
 * This file is part of OpenDelta.
 *
 * OpenDelta is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenDelta is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenDelta. If not, see <http://www.gnu.org/licenses/>.
 */

package eu.chainfire.opendelta;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
import android.os.SystemProperties;

import androidx.preference.PreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Config {
    private static Config instance = null;

    public static Config getInstance(Context context) {
        if (instance == null) {
            instance = new Config(context.getApplicationContext());
        }
        return instance;
    }

    private final static String PREF_INFO_DISPLAYED = "info_displayed";
    private final static String PREF_AB_PERF_MODE_NAME = "ab_perf_mode";
    private final static String PREF_AB_WAKE_LOCK_NAME = "ab_wake_lock";
    private final static String PREF_AB_STREAM_NAME = "ab_stream_flashing";
    private final static String PROP_AB_DEVICE = "ro.build.ab_update";
    private final static String PREF_TEST_MODE_NAME = "test_mode_enabled";
    private final static String PREF_INCREMENTAL_UPDATES = "pref_incremental_updates";

    private final SharedPreferences prefs;

    private final String property_version;
    private final String property_device;
    private final String property_release;
    private final String filename_base;
    private final String path_base;
    private final String path_flash_after_update;
    private final boolean support_ab_perf_mode;
    private final boolean use_incremental_updates;
    private final boolean use_twrp;
    private final boolean property_test_mode;
    private final String filename_base_prefix;
    private final String url_base_json;
    private final String pixys_version;
    private final String test_url_base_json;
    private final String full_update_base;
    private final String incremental_update_base;
    private final String changelogs_base;
    private final String property_ziptype;

    private Config(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);

        Resources res = context.getResources();

        property_version = SystemProperties.get(
                res.getString(R.string.property_version));

        pixys_version = SystemProperties.get(
                res.getString(R.string.pixys_version));

        property_device = SystemProperties.get(
                res.getString(R.string.property_device));

        property_release = SystemProperties.get(
                res.getString(R.string.property_release));

        filename_base = String.format(Locale.ENGLISH,
                res.getString(R.string.filename_base), pixys_version);

        path_base = String.format(Locale.ENGLISH, "%s%s%s%s",
                Environment.getExternalStorageDirectory().getAbsolutePath(),
                File.separator, res.getString(R.string.path_base),
                File.separator);
        path_flash_after_update = String.format(Locale.ENGLISH, "%s%s%s",
                path_base, "FlashAfterUpdate", File.separator);
        support_ab_perf_mode = res.getBoolean(R.bool.support_ab_perf_mode);
        use_incremental_updates = res.getBoolean(R.bool.use_incremental_updates);
        use_twrp = res.getBoolean(R.bool.use_twrp);
        filename_base_prefix = String.format(Locale.ENGLISH,
                res.getString(R.string.filename_base), property_version);
        property_ziptype = SystemProperties.get(res.getString(R.string.property_ziptype));
        url_base_json = String.format(
                res.getString(R.string.url_base_json),
                property_device, property_release, property_ziptype.toLowerCase(Locale.ENGLISH));
        test_url_base_json = String.format(
                res.getString(R.string.test_url_base_json),
                property_device, property_release, property_ziptype.toLowerCase(Locale.ENGLISH));
        full_update_base = res.getString(R.string.full_update_base);
        incremental_update_base = res.getString(R.string.incremental_update_base);
        changelogs_base = res.getString(R.string.changelogs_base);
        property_test_mode = !SystemProperties.get(
                res.getString(R.string.property_test_mode)).isEmpty();

        Logger.d("property_version: %s", property_version);
        Logger.d("property_device: %s", property_device);
        Logger.d("filename_base: %s", filename_base);
        Logger.d("filename_base_prefix: %s", filename_base_prefix);
        Logger.d("path_base: %s", path_base);
        Logger.d("path_flash_after_update: %s", path_flash_after_update);
        Logger.d("property_release: %s", property_release);
        Logger.d("url_base_json: %s", url_base_json);
        Logger.d("changelogs_base: %s", changelogs_base);
        Logger.d("use_twrp: %d", use_twrp ? 1 : 0);
        Logger.d("property_ziptype: %s", property_ziptype);
        Logger.d("property_test_mode: %d", property_test_mode ? 1 : 0);
        Logger.d("use_incremental_update: %d", use_incremental_updates ? 1 : 0);
    }

    public String getFilenameBase() {
        return filename_base;
    }

    public String getPathBase() {
        return path_base;
    }

    public String getPathFlashAfterUpdate() {
        return path_flash_after_update;
    }

    public boolean getUseTWRP() {
        return use_twrp;
    }

    public String getPropertyRelease() {
        return property_release;
    }

    public String getChangelogsBase() {
        return changelogs_base;
    }

    public boolean getInfoDisplayed() {
        return prefs.getBoolean(PREF_INFO_DISPLAYED, false);
    }

    public void setInfoDisplayed() {
        // only need to set to true - once
        prefs.edit().putBoolean(PREF_INFO_DISPLAYED, true).apply();
    }

    public boolean getABPerfModeSupport() {
        return support_ab_perf_mode;
    }

    public boolean getABPerfModeCurrent() {
        return getABPerfModeSupport() && prefs.getBoolean(PREF_AB_PERF_MODE_NAME, true);
    }

    public void setABPerfModeCurrent(boolean enable) {
        prefs.edit().putBoolean(PREF_AB_PERF_MODE_NAME, enable).commit();
    }

    public boolean getABWakeLockCurrent() {
        return prefs.getBoolean(PREF_AB_WAKE_LOCK_NAME, true);
    }

    public void setABWakeLockCurrent(boolean enable) {
        prefs.edit().putBoolean(PREF_AB_WAKE_LOCK_NAME, enable).commit();
    }

    public boolean getABStreamCurrent() {
        return prefs.getBoolean(PREF_AB_STREAM_NAME, false);
    }

    public void setABStreamCurrent(boolean enable) {
        prefs.edit().putBoolean(PREF_AB_STREAM_NAME, enable).commit();
    }

    public boolean getSchedulerSleepEnabled() {
        return prefs.getBoolean(SettingsActivity.PREF_SCHEDULER_SLEEP, true);
    }

    public void setSchedulerSleepEnabled(boolean enable) {
        prefs.edit().putBoolean(SettingsActivity.PREF_SCHEDULER_SLEEP, enable).commit();
    }

    public List<String> getFlashAfterUpdateZIPs() {
        List<String> extras = new ArrayList<>();

        File[] files = (new File(getPathFlashAfterUpdate())).listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().toLowerCase(Locale.ENGLISH).endsWith(".zip")) {
                    String filename = f.getAbsolutePath();
                    if (filename.startsWith(getPathBase())) {
                        extras.add(filename);
                    }
                }
            }
            Collections.sort(extras);
        }

        return extras;
    }

    public String getDevice() {
        return property_device;
    }

    public String getVersion() {
        return property_version;
    }

    public String getFileBaseNamePrefix() {
        return filename_base_prefix;
    }

    public String getUrlBaseJson() {
        return url_base_json;
    }

    public String getTestUrlBaseJson() {
        return test_url_base_json;
    }

    public String getFullUpdateBase() {
        return full_update_base;
    }

    public String getIncrementalUpdateBase() {
        return incremental_update_base;
    }

    public static boolean isABDevice() {
        return SystemProperties.getBoolean(PROP_AB_DEVICE, false);
    }

    public String getZipType() {
        return property_ziptype;
    }

    public boolean isTestModeSupported() {
        return property_test_mode;
    }

    public boolean isTestModeEnabled() {
        return prefs.getBoolean(PREF_TEST_MODE_NAME, false);
    }

    public void setTestModeEnabled(boolean enable) {
        prefs.edit().putBoolean(PREF_TEST_MODE_NAME, enable).apply();
    }

    public boolean isIncrementalUpdatesEnabled() {
        return prefs.getBoolean(PREF_INCREMENTAL_UPDATES, false);
    }

    public void setIncrementalUpdatesEnabled(boolean enable) {
        prefs.edit().putBoolean(PREF_INCREMENTAL_UPDATES, enable).apply();
    }
}
