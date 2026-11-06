package com.focus.launcher.data

/** Maps browser Custom Tab activities to the app that opened them. */
class ForegroundAttributor(private val isIgnored: (String) -> Boolean) {
    private var recentPackage: String? = null
    private val customTabOrigins = HashMap<String, String>()

    fun resumed(pkg: String, className: String?): String {
        if (!isCustomTab(pkg, className)) {
            if (pkg !in BROWSER_PACKAGES) recentPackage = pkg.takeUnless(isIgnored)
            return pkg
        }

        val attributed = recentPackage?.takeUnless { it == pkg } ?: pkg
        customTabOrigins[key(pkg, className)] = attributed
        return attributed
    }

    fun paused(pkg: String, className: String?): String =
        if (isCustomTab(pkg, className)) customTabOrigins.remove(key(pkg, className)) ?: pkg else pkg

    fun reset() {
        recentPackage = null
        customTabOrigins.clear()
    }

    private fun isCustomTab(pkg: String, className: String?): Boolean =
        pkg in BROWSER_PACKAGES && className?.contains(".customtabs.", ignoreCase = true) == true

    private fun key(pkg: String, className: String?) = "$pkg/$className"

    private companion object {
        val BROWSER_PACKAGES = setOf(
            "com.android.chrome", "org.mozilla.firefox", "com.brave.browser", "com.microsoft.emmx",
            "com.opera.browser", "com.duckduckgo.mobile.android", "com.sec.android.app.sbrowser",
        )
    }
}