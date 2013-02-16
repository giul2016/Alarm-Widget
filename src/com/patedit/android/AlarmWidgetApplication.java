package com.patedit.android;

import android.app.Application;
import org.acra.*;
import org.acra.annotation.*;

@ReportsCrashes(formKey = "dFpKMVRvSzlvazM4ZXJQRGdmNVh5U0E6MQ",
        mode = ReportingInteractionMode.TOAST,
        forceCloseDialogAfterToast = false, 
        resToastText = R.string.crash_toast_text)
        
public class AlarmWidgetApplication extends Application {
	@Override
    public void onCreate() {
        ACRA.init(this);
        super.onCreate();
    }
}
