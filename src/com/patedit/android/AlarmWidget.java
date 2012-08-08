package com.patedit.android;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmWidget extends AppWidgetProvider {

	@Override
	public void onReceive(Context ctxt, Intent intent) {
		if (intent.getAction()==null) {
			ctxt.startService(new Intent(ctxt, UpdateService.class));
			Toast.makeText(ctxt, R.string.updated, Toast.LENGTH_SHORT).show();
		}
		else {
			super.onReceive(ctxt, intent);
		}
	}
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
	     for (int i=0; i<appWidgetIds.length; i++) {
	         updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
	     }
	}
		

	public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		context.startService(new Intent(context, UpdateService.class));
	}
}