package com.patedit.android;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.acra.ErrorReporter;

import android.content.Context;
import android.provider.Settings;

public class Util {

	private Context context;
	
	public Util(Context context) {
		this.context = context;
	}
	
	public Boolean alarmExists() {
		Boolean result = false;

		String nextAlarm = Settings.System.getString(context.getContentResolver(),
				Settings.System.NEXT_ALARM_FORMATTED);
		if (nextAlarm.length() != 0) {
			result = true;
		}

		return result;
	}

	public String dayFormattedToDay(String day, String hour) {
		Calendar calendar = Calendar.getInstance();
		int todayDay = calendar.get(Calendar.DAY_OF_WEEK);
		//Gregorian. Should start on Monday
		todayDay--;
		if(todayDay == 0) {
			todayDay = 7;
		}

		String[] daysFormatted = context.getResources().getStringArray(R.array.daysFormatted);
		String[] days = context.getResources().getStringArray(R.array.days);

		for (int i = 0; i < daysFormatted.length; i++) {
			if (day.equals(daysFormatted[i])) {
				if ((todayDay == i) || (todayDay == 7 && i == 0)) {
					day = context.getString(R.string.tomorrow);
				} else if (todayDay == i + 1) {
					day = getDayFormattedWhenToday(hour, days[i]);
				} else {
					day = days[i];
				}
			} else {
				String dayNotFoundMsg = "Day not found (" + day + ")";
				Throwable dayNotFound = new Throwable(dayNotFoundMsg);
				ErrorReporter.getInstance().handleSilentException(dayNotFound);
			}
		}

		return day;
	}

	private String getDayFormattedWhenToday(String hour, String dayAlarm) {
		String day;
		Integer hourWithoutSemiColomn;
		Integer actualHourWithoutSemiColomn;
		Date actualDate = new Date();
		SimpleDateFormat formatDate;
		
		if(Settings.System.getString(context.getContentResolver(), android.provider.Settings.System.TIME_12_24) == "24") {
			formatDate = new SimpleDateFormat("HH:mm");
		} else {
			formatDate = new SimpleDateFormat("hh:mm");
		}
		String actualHour = formatDate.format(actualDate);
		
		String[] hourArrayWithoutSemiColomn = hour.split(":");
		String[] actualHourArrayWithoutSemiColomn = actualHour.split(":");
		
		hourWithoutSemiColomn = Integer.parseInt(hourArrayWithoutSemiColomn[0] + hourArrayWithoutSemiColomn[1]);
		actualHourWithoutSemiColomn = Integer.parseInt(actualHourArrayWithoutSemiColomn[0] + actualHourArrayWithoutSemiColomn[1]);
							
		hourWithoutSemiColomn = Integer.parseInt(hourArrayWithoutSemiColomn[0] + hourArrayWithoutSemiColomn[1]);
		actualHourWithoutSemiColomn = Integer.parseInt(actualHourArrayWithoutSemiColomn[0] + actualHourArrayWithoutSemiColomn[1]);
				
		if(actualHourWithoutSemiColomn <= hourWithoutSemiColomn) {
			day = context.getString(R.string.today);
		} else {
			day = dayAlarm;
		}
		
		return day;
	}
}
