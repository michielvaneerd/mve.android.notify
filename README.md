# Titanium Android Local Notification module

A Titanium Android module that can be used to schedule local notifications. See the example project for a full implementation.

## Concepts

- First create a notification channel. This should be called asap, for example immediately after requiring the module. It can be called multiple times without problems.
- Make sure to ask for the `POST_NOTIFICATIONS` permission before scheduling notifications.
- This module is using Android alarms to schedule the notifications. Android alarms are by default _inexact_, which means they won't fire exactly on time. If you want your notifications to be scheduled _exactly_ on time, make sure to request for `REQUEST_SCHEDULE_EXACT_ALARM` permission.
- Each notification can be scheduled once or in a repeated interval.
- Each notification has a `requestCode` - this is a number and works as an ID for the notification. This must be used to cancel a notification.

## Changes to tiapp.xml

Add the below lines to your tiapp.xml:

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<application>
  <receiver android:name="mve.android.notify.MveAlarmReceiver" />
</application>
```

See also the tiapp.xml of the example app.

## Code examples

### Require module

```js
var notify = require('mve.android.notify');
```

### Create notification channel

This is always needed and should be done asap.

```js
notify.createChannel();
```

### Ask for POST_NOTIFICATIONS permission

This is always needed.

```js
Ti.Android.requestPermissions(['android.permission.POST_NOTIFICATIONS'], function (e) {
  if (e.success) {
    // We have POST_NOTIFICATIONS permission
  }
});
```

The user will see the below screen:

![Asking for the POST_NOTIFICATIONS permission](./POST_NOTIFICATIONS_framed.png)

### Make a REQUEST_SCHEDULE_EXACT_ALARM request to ask for SCHEDULE_EXACT_ALARM permission

This is only needed if you want to schedule _exact_ notifications. Make also sure to add the `SCHEDULE_EXACT_ALARM` permission to your tipp.xml as can be seen above and in the example app.

Note that this is only needed when the user doesn't have this permission yet, so always first check if we already have this permission.

```js
if (!notify.canScheduleExactAlarms()) {
  var intent = Ti.Android.createIntent({
    action: 'android.settings.REQUEST_SCHEDULE_EXACT_ALARM',
    data: 'package:' + Ti.App.id
  });
  Ti.Android.currentActivity.startActivityForResult(intent, function (e) {
    if (notify.canScheduleExactAlarms()) {
      // We have REQUEST_SCHEDULE_EXACT_ALARM permission
    }
  });
}
```

The user will get sent to the following screen:

![Asking for the REQUEST_SCHEDULE_EXACT_ALARM permission](./REQUEST_SCHEDULE_EXACT_ALARM_framed.png)

### Schedule notification

Example of notification that should be displayed each day exactly on 08:30h.

```js
notify.schedule({
  requestCode: 1,
  content: 'This is the content of the notification',
  title: 'The title',
  icon: 'ic_stat_capsules_solid',
  interval: 'day',
  exact: true,
  hour: 8,
  minute: 30
});
```

__Note that the `icon` should be a PNG file that is located in: `app/platform/android/res/drawable` and that the extension `.png` should be omitted.__

### Cancel notification

Use the `requestCode` that you used to schedule the notification.

```js
notify.cancel(1);
```

## See scheduled notifications

You can use the below `adb` command to see a dump of scheduled notifications (alarms) for the attached Android device or emulator:

`adb shell dumpsys alarm > alarms.txt`

Then search for your app ID to see the alarms for your app.