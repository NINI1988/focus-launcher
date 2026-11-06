package com.focus.launcher.data

import org.junit.Assert.assertEquals
import org.junit.Test

class ForegroundAttributorTest {
    private val attributor = ForegroundAttributor { it == "home" }

    @Test
    fun `custom tab time is attributed to the app that opened it`() {
        attributor.resumed("nunti", "com.nunti.NewsActivity")
        attributor.paused("nunti", "com.nunti.NewsActivity")

        val tab = "org.chromium.chrome.browser.customtabs.CustomTabActivity"
        assertEquals("nunti", attributor.resumed("com.brave.browser", tab))
        assertEquals("nunti", attributor.paused("com.brave.browser", tab))
    }

    @Test
    fun `translucent and full screen browser custom tabs are recognized`() {
        attributor.resumed("nunti", "com.nunti.NewsActivity")

        assertEquals(
            "nunti",
            attributor.resumed("com.android.chrome", "org.chromium.chrome.browser.customtabs.TranslucentCustomTabActivity"),
        )
        assertEquals(
            "nunti",
            attributor.resumed("com.brave.browser", "org.chromium.chrome.browser.customtabs.FullScreenCustomTabActivity"),
        )
    }

    @Test
    fun `ordinary browser activities keep their own attribution`() {
        assertEquals(
            "com.brave.browser",
            attributor.resumed("com.brave.browser", "org.chromium.chrome.browser.BrowserActivity"),
        )
    }

    @Test
    fun `a regular browser activity does not replace the originating app`() {
        attributor.resumed("nunti", "com.nunti.NewsActivity")
        attributor.resumed("com.brave.browser", "org.chromium.chrome.browser.BrowserActivity")

        assertEquals(
            "nunti",
            attributor.resumed("com.brave.browser", "org.chromium.chrome.browser.customtabs.CustomTabActivity"),
        )
    }

    @Test
    fun `a home screen clears the previous host candidate`() {
        attributor.resumed("nunti", "com.nunti.NewsActivity")
        attributor.resumed("home", "com.launcher.HomeActivity")

        assertEquals(
            "com.brave.browser",
            attributor.resumed("com.brave.browser", "org.chromium.chrome.browser.customtabs.CustomTabActivity"),
        )
    }

    @Test
    fun `custom tab-like names outside browsers are not treated as custom tabs`() {
        attributor.resumed("nunti", "com.nunti.NewsActivity")

        assertEquals("reader", attributor.resumed("reader", "com.reader.CustomTabActivity"))
    }

    @Test
    fun `browser activities in the customtabs namespace do not need a specific suffix`() {
        attributor.resumed("nunti", "com.nunti.NewsActivity")

        assertEquals(
            "nunti",
            attributor.resumed("com.brave.browser", "org.chromium.chrome.browser.customtabs.CustomTabSettingsActivity"),
        )
    }

    @Test
    fun `custom tab activity outside the customtabs namespace is ignored`() {
        attributor.resumed("nunti", "com.nunti.NewsActivity")

        assertEquals(
            "com.brave.browser",
            attributor.resumed("com.brave.browser", "org.chromium.chrome.browser.CustomTabActivity"),
        )
    }
}