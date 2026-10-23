package com.gathin.tvhome;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WallpaperLabelTest {
    @Test
    public void numberAndExtensionAreDropped() {
        assertEquals("Moon lake", LauncherSettingsActivity.wallpaperLabel("03-moon-lake.jpg"));
        assertEquals("Glass navy", LauncherSettingsActivity.wallpaperLabel("glass-navy.jpg"));
        assertEquals("Above the clouds", LauncherSettingsActivity.wallpaperLabel("06_above_the_clouds.png"));
    }

    @Test
    public void aNameMadeOnlyOfDigitsKeepsTheFileName() {
        assertEquals("001.jpg", LauncherSettingsActivity.wallpaperLabel("001.jpg"));
    }
}
