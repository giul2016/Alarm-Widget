package com.patedit.android;

import java.util.ArrayList;
import java.util.List;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;

public class Prefs extends PreferenceActivity {
	
	private static Preference addWidget;
	private static CheckBoxPreference checkBox;
	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private static PreferenceCategory prefCategory;
	private List<Preference> preferences = new ArrayList<Preference>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    	prefCategory = (PreferenceCategory) findPreference(ConstantsPrefs.colorsPrefs);

        savePreferences();
                
        getWidgetId();

//TODO: Upload preload so the user can see the style
//        ((ColorPickerPreference)findPreference("colorHour")).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
//
//			@Override
//			public boolean onPreferenceChange(Preference preference, Object newValue) {
//				preference.setSummary(ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue))));
//				
//				return true;
//			}
//
//        });
//        ((ColorPickerPreference)findPreference("colorHour")).setAlphaSliderEnabled(true);
        
        asociateItems();
        manageEvents();
 
    }

	private void removePreferences() {
		prefCategory.removeAll();
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(Prefs.this);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putInt(ConstantsPrefs.colorHour, R.integer.COLOR_DEFAULT);
		editor.putInt(ConstantsPrefs.colorDay, R.integer.COLOR_DEFAULT);
		editor.commit();
		
	}
	
    private void savePreferences() {
		for(int i = 0; i < prefCategory.getPreferenceCount(); i++) {
			preferences.add(prefCategory.getPreference(i));
		}
	}

    private void restorePreferences() {
		for(int i = 0; i < preferences.size(); i++) {
			prefCategory.addPreference(preferences.get(i));
		}
	}
    
	private void getWidgetId() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
     
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }
    
    
    private void asociateItems() {
    	addWidget = findPreference(ConstantsPrefs.addWidget);
    	Preference checkBoxPref = findPreference(ConstantsPrefs.useDefaultColors);
    	checkBox = (CheckBoxPreference) checkBoxPref;
    	if(checkBox.isChecked()) {
    		removePreferences();
    	}
    }
    
    
    private void manageEvents() {
    	addWidget.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
	          	 AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(Prefs.this);
            	 AlarmWidget.updateAppWidget(Prefs.this, appWidgetManager, mAppWidgetId);
            	 Intent resultValue = new Intent();
            	 resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            	 setResult(RESULT_OK, resultValue);
            	 finish();				
				return true;
			}
		});
		
    	
        checkBox.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference pref, Object newValue) {
				if(Boolean.valueOf(String.valueOf(newValue))) {
					removePreferences();
				} else {
					restorePreferences();
				}
				return true;
			}
		});
    }

}
