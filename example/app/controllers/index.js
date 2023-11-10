var notify = require('mve.android.notify');

// Create a notification channel asap.
notify.createChannel();

// Schedule notification 1 minute from now
function schedule(id, content, title, icon, interval, exact) {
	askForPostPermission(function (hasPostPermission) {
		$.postPermission.text = hasPostPermission ? 'We have post permissions' : "We don't have post permissions";
		if (hasPostPermission) {
			if (exact) {
				askForExactPermission(function (hasExactPermission) {
					$.exactPermission.text = hasExactPermission ? 'We have exact permissions' : "We don't have exact permissions";
					if (hasExactPermission) {
						scheduleForReal(id, content, title, icon, interval, exact);
					}
				});
			} else {
				scheduleForReal(id, content, title, icon, interval, exact);
			}
		}
	});
}

function scheduleForReal(id, content, title, icon, interval, exact) {
	const now = new Date();
	notify.schedule({
		requestCode: id,
		content: content,
		title: title,
		icon: icon,
		interval: interval,
		exact: exact,
		hour: now.getHours(),
		minute: now.getMinutes() + 1
	});
}

function cancel(id) {
	notify.cancel(id);
}

function askForPostPermission(callback) {
	Ti.Android.requestPermissions(['android.permission.POST_NOTIFICATIONS'], function (e) {
		callback(e.success);
	});
}

function askForExactPermission(callback) {
	if (!notify.canScheduleExactAlarms()) {
		var intent = Ti.Android.createIntent({
			action: 'android.settings.REQUEST_SCHEDULE_EXACT_ALARM',
			data: 'package:' + Ti.App.id
		});
		Ti.Android.currentActivity.startActivityForResult(intent, function (e) {
			callback(notify.canScheduleExactAlarms());
		});
	} else {
		callback(notify.canScheduleExactAlarms());
	}
}

function scheduleOnceExact() {
	schedule(1, 'This is an exact one-time notification', 'Notification', 'ic_stat_capsules_solid', 'once', true);
}

function scheduleOnceInexact() {
	schedule(2, 'This is an inexact one-time notification', 'Notification', 'ic_stat_capsules_solid', 'once', false);
}

function scheduleHourlyExact() {
	schedule(3, 'This is an exact hourly notification', 'Notification', 'ic_stat_capsules_solid', 'hour', true);
}

function scheduleHourlyInexact() {
	schedule(4, 'This is an inexact hourly notification', 'Notification', 'ic_stat_capsules_solid', 'hour', false);
}

function scheduleDailyExact() {
	schedule(5, 'This is an exact daily notification', 'Notification', 'ic_stat_capsules_solid', 'day', true);
}

function scheduleDailyInexact() {
	schedule(6, 'This is an inexact daily notification', 'Notification', 'ic_stat_capsules_solid', 'day', false);
}

function cancelOnceExact() {
	cancel(1);
}

function cancelOnceInexact() {
	cancel(2);
}

function cancelHourlyExact() {
	cancel(3);
}

function cancelHourlyInexact() {
	cancel(4);
}

function cancelDailyExact() {
	cancel(5);
}

function cancelDailyInexact() {
	cancel(6);
}


$.index.open();
