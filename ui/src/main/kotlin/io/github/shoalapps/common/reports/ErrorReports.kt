package io.github.shoalapps.common.reports

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.sentry.Sentry
import io.sentry.android.core.SentryAndroid

/**
 * Error reports, only with your agreement. Crashes and errors go to Shoal's Sentry project so bugs
 * can be fixed: the error, the app and Android version and the phone model. No account details,
 * no names, no history, no screenshots, no IP address kept. Off unless you say yes.
 * See https://shoalapps.github.io/privacy.html
 *
 * The app supplies Sentry (io.sentry:sentry-android); this library only builds against it, so apps
 * without error reports don't carry it.
 */
class ErrorReports(private val config: Config) {
    /**
     * [tag] names the app in reports ("shoal", "swordfish"…). [translate] localises the consent
     * dialog's text, which the app passes in English so its catalogue extractor finds it.
     */
    class Config(
        val dsn: String,
        val tag: String,
        val versionName: String,
        val debug: Boolean,
        val translate: (String) -> String,
        val askTitle: String,
        val askMessage: String,
        val sendLabel: String,
        val declineLabel: String,
        val privacyLabel: String,
    )

    /** Whether reports are on (for the Settings switch). */
    var enabled by mutableStateOf(false)
        private set

    /** Reporting is only possible in builds made with Shoal's Sentry address. */
    val available get() = config.dsn.isNotBlank()

    private fun prefs(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun init(app: Application) {
        enabled = available && prefs(app).getBoolean("consent", false)
        if (enabled) start(app)
    }

    private fun start(app: Context) {
        SentryAndroid.init(app) { o ->
            o.dsn = config.dsn
            o.isSendDefaultPii = false
            o.isAttachScreenshot = false
            o.isAttachViewHierarchy = false
            o.isEnableUserInteractionBreadcrumbs = false
            o.isEnableUserInteractionTracing = false
            o.maxBreadcrumbs = 30
            o.release = config.tag + "@" + config.versionName
            o.environment = if (config.debug) "debug" else "release"
            o.setTag("app", config.tag)
            // Strip anything that could identify the person, and give a blank address so Sentry
            // can't guess a location from the connection.
            o.setBeforeSend { event, _ ->
                event.user = io.sentry.protocol.User().apply { ipAddress = "0.0.0.0" }
                event.serverName = null
                event
            }
        }
    }

    fun setEnabled(context: Context, on: Boolean) {
        prefs(context).edit().putBoolean("consent", on).putBoolean("asked", true).apply()
        enabled = on && available
        if (enabled) start(context.applicationContext) else Sentry.close()
    }

    /** A handled error worth knowing about (does nothing unless reports are on). */
    fun report(e: Throwable) { if (enabled) Sentry.captureException(e) }

    /** Asks once, on first launch: send error reports? */
    fun askOnce(activity: Activity) {
        if (!available || prefs(activity).getBoolean("asked", false)) return
        val t = config.translate
        android.app.AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog_Alert)
            .setTitle(t(config.askTitle))
            .setMessage(t(config.askMessage))
            .setPositiveButton(t(config.sendLabel)) { _, _ -> setEnabled(activity, true) }
            .setNegativeButton(t(config.declineLabel)) { _, _ -> setEnabled(activity, false) }
            .setNeutralButton(t(config.privacyLabel)) { _, _ ->
                runCatching { activity.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(PRIVACY_URL))) }
            }
            .setCancelable(false)
            .show()
    }

    companion object {
        /** Kept from the apps' own copies, so consent already given carries over. */
        const val PREFS = "telemetry"
        const val PRIVACY_URL = "https://shoalapps.github.io/privacy.html"
    }
}
