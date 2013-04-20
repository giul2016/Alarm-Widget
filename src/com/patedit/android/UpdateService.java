package com.patedit.android;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.acra.ErrorReporter;

import com.patedit.android.R;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class UpdateService extends IntentService {

	private static RemoteViews remoteViews;
	private Util util;

	private static String nextAlarm;
	private static String day;
	private static String hour;
	private static String packageAlarm;
	private static String classAlarm;

	private static String clockImpls[][] = {
		{ "HTC Alarm Clock", "com.htc.android.worldclock",
				"com.htc.android.worldclock.WorldClockTabControl" },
		{ "LG Alarm Clock", "com.lge.clock",
				"com.lge.clock.AlarmClockActivity" },
		{ "Standar Alarm Clock", "com.android.deskclock",
				"com.android.deskclock.AlarmClock" },
		{ "Samsung Galaxy Nexus", "com.google.android.deskclock",
				"com.android.deskclock.AlarmClock" },
		{ "Moto Blur Alarm Clock", "com.motorola.blur.alarmclock",
				"com.motorola.blur.alarmclock.AlarmClock" },
		{ "Samsung Galaxy Clock", "com.sec.android.app.clockpackage",
				"com.sec.android.app.clockpackage.ClockPackage" },
		{ "Sony Ericsson", "com.sonyericsson.alarm",
				"com.sonyericsson.alarm.Alarm" },
		{ "Sony Ericsson Clock", "com.sonyericsson.alarm",
				"com.sonyericsson.alarm.AlarmClock" },
		{ "Sony Ericsson New", "com.sonyericsson.organizer",
				"com.sonyericsson.organizer.deskclock.DeskClock" },
		{ "Sony Ericsson ICS", "com.sonyericsson.organizer",
				"com.sonyericsson.organizer.icsdeskclock.ICSDeskClock" },
		{ "Standar Alarm Clock Froyo", "com.android.alarmclock",
				"com.android.alarmclock.AlarmClock" },
		{ "ZTE", "zte.com.cn.alarmclock",
				"zte.com.cn.alarmclock.AlarmClock" },
		{ "Froyo Nexus Alarm Clock", "com.google.android.deskclock",
				"com.android.deskclock.DeskClock" } };

	public UpdateService() {
		super("AlarmWidget");
		util = new Util(this);
	}

	@Override
	public void onHandleIntent(Intent intent) {
		ComponentName componentName = new ComponentName(this, AlarmWidget.class);
		AppWidgetManager alarmWidget = AppWidgetManager.getInstance(this);
		try {
			buildUpdate();
		} catch (NameNotFoundException e) {
		}
		alarmWidget.updateAppWidget(componentName, remoteViews);
	}

	private void buildUpdate() throws NameNotFoundException {
		remoteViews = new RemoteViews(this.getPackageName(), R.layout.main);
		manageEvents(this, remoteViews);

		if (util.alarmExists()) {
			loadAlarm();
		} else {
			dontLoadAlarm();
		}

		buildView();
	}

	private RemoteViews buildView() {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		int colorHour = sharedPrefs.getInt(ConstantsPrefs.colorHour,
				R.integer.COLOR_DEFAULT);
		int colorDay = sharedPrefs.getInt(ConstantsPrefs.colorDay,
				R.integer.COLOR_DEFAULT);

		remoteViews.setTextColor(R.id.hour, colorHour);
		
		remoteViews.setTextColor(R.id.day, colorDay);

		remoteViews.setTextViewText(R.id.hour, hour);
		remoteViews.setTextViewText(R.id.day, day);

		return remoteViews;
	}

	private void manageEvents(Context context, RemoteViews remoteViews)
			throws NameNotFoundException {
		if (findClockImpl()) {
			Intent alarmClockApp = new Intent();
			alarmClockApp.setClassName(packageAlarm, classAlarm);
			PendingIntent pendingApp = PendingIntent.getActivity(context, 0,
					alarmClockApp, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.alarmWidget, pendingApp);
		} else {
			String alarmNotFoundMsg = "Alarm not found (AlarmWidget v"
					+ this.getPackageManager().getPackageInfo(
							this.getPackageName(), 0).versionName + ") for: "
					+ android.os.Build.FINGERPRINT;
			Throwable alarmNotFound = new Throwable(alarmNotFoundMsg);
			ErrorReporter.getInstance().handleSilentException(alarmNotFound);
		}

		Intent alarmClockRefresh = new Intent(context, AlarmWidget.class);
		PendingIntent pendingRefresh = PendingIntent.getBroadcast(context, 0,
				alarmClockRefresh, 0);

		remoteViews.setOnClickPendingIntent(R.id.updateLayout, pendingRefresh);
	}

	private boolean findClockImpl() {
		Boolean foundClockImpl = false;

		for (int i = 0; i < clockImpls.length; i++) {
			if (foundClockImpl == false) {
				try {
					ComponentName componentName = new ComponentName(
							clockImpls[i][1], clockImpls[i][2]);
					ActivityInfo aInfo = this.getPackageManager()
							.getActivityInfo(componentName,
									PackageManager.GET_META_DATA);
					packageAlarm = clockImpls[i][1];
					classAlarm = clockImpls[i][2];
					foundClockImpl = true;
				} catch (NameNotFoundException e) {
				}
			}
		}
		return foundClockImpl;
	}

	private void loadAlarm() {
		remoteViews.setImageViewResource(R.id.alarm_image, R.drawable.icon);
		nextAlarm = Settings.System.getString(this.getContentResolver(),
				Settings.System.NEXT_ALARM_FORMATTED);
		Pattern hourPattern = Pattern.compile("([01]?[0-9]|2[0-3]):[0-5][0-9]");
		Pattern dayPatternEs = Pattern.compile("^[A-Za-z\u00e1]{2}");
		Pattern dayPatternEn = Pattern.compile("^[A-Za-z]{2}");
		Pattern dayPatternRu = Pattern.compile("^[\u0410-\u044f]{2}");
		Matcher hourMatcher = hourPattern.matcher(nextAlarm);
		Matcher dayMatcherEs = dayPatternEs.matcher(nextAlarm);
		Matcher dayMatcherEn = dayPatternEn.matcher(nextAlarm);
		Matcher dayMatcherRu = dayPatternRu.matcher(nextAlarm);
	

		if (hourMatcher.find()) {
			hour = hourMatcher.group();
		}

		if (dayMatcherEn.find()) {
			day = util.dayFormattedToDay(dayMatcherEn.group(), hour);
		} else if (dayMatcherRu.find()) {
			day = util.dayFormattedToDay(dayMatcherRu.group(), hour);
		} else if (dayMatcherEs.find()) {
			day = util.dayFormattedToDay(dayMatcherEs.group(), hour);
		} else {
			day = this.getString(R.string.withoutDay);
		}
		
	}

	private void dontLoadAlarm() {
		remoteViews.setImageViewResource(R.id.alarm_image, R.drawable.no_alarm);
		hour = this.getString(R.string.sleeping);
		day = this.getString(R.string.dontBeLazy);
	}
}
