package mve.android.notify

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.appcelerator.titanium.TiApplication
import org.appcelerator.titanium.util.TiRHelper

class MveAlarmReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {

        Utils.log("Alarm received")

        if (intent == null) {
            Utils.log("No intent in alarm, so we cannot display a notification")
            return;
        }

        val requestCode = intent.getIntExtra(MveAndroidNotifyModule.NOTIFICATION_REQUEST_CODE, 0)
        val content = intent.getStringExtra(MveAndroidNotifyModule.NOTIFICATION_CONTENT)
        val title = intent.getStringExtra(MveAndroidNotifyModule.NOTIFICATION_TITLE)
        val icon = intent.getStringExtra(MveAndroidNotifyModule.NOTIFICATION_ICON)
        val isExact = intent.getBooleanExtra(MveAndroidNotifyModule.NOTIFICATION_EXACT, false)

        val builder = NotificationCompat.Builder(TiApplication.getInstance().applicationContext, channelId)
            .setSmallIcon(TiRHelper.getApplicationResource("drawable.$icon"))
            .setContentIntent(createIntent(requestCode))
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(
                TiApplication.getInstance().applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(TiApplication.getInstance()).notify(requestCode, builder.build())
            Utils.log("Notification displayed")
        } else {
            Utils.log("We have no permission to display a notification")
        }

        if (isExact) {
            val interval = intent.getStringExtra(MveAndroidNotifyModule.NOTIFICATION_INTERVAL);
            if (interval != MveAndroidNotifyModule.NOTIFICATION_INTERVAL_ONCE) {
                // setRepeating doesn't work exact, you have to schedule one exact with setExact() and then reschedule the next one in the alarm receiver.
                // https://stackoverflow.com/a/59473739/1294832
                val hour = intent.getIntExtra(MveAndroidNotifyModule.NOTIFICATION_HOUR, 0)
                val minute = intent.getIntExtra(MveAndroidNotifyModule.NOTIFICATION_MINUTE, 0)
                MveAndroidNotifyModule.scheduleNotification(requestCode, content!!, title!!, icon!!, interval!!, true, hour, minute, true)
            }
        }

    }

    private fun createIntent(requestCode: Int) : PendingIntent
    {
        val launchIntent = TiApplication.getInstance().applicationContext.packageManager
            .getLaunchIntentForPackage(TiApplication.getInstance().applicationContext.packageName)
        if (launchIntent != null) {
            launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            launchIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            launchIntent.action = Intent.ACTION_MAIN
        }
        return PendingIntent.getActivity(TiApplication.getInstance().applicationContext, requestCode,
            launchIntent!!, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE) as PendingIntent
    }

}