package com.angeloraso.plugins.callinprogressfloatingwidget;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "CallInProgressFloatingWidget")
public class CallInProgressFloatingWidgetPlugin extends Plugin {

    private CallInProgressFloatingWidget implementation = new CallInProgressFloatingWidget();

    @PluginMethod
    public void show(PluginCall call) {
        Integer timer = call.getInt("seconds", 0);
        if (timer == null) {
            call.reject("Call in progress floating widget plugin error: Must supply the call duration seconds");
            return;
        }
        if (getActivity().isFinishing()) {
            call.reject("Call in progress floating widget plugin error: App is finishing");
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            getActivity().startService(new Intent(getActivity(), FloatingViewService.class));
        } else if (Settings.canDrawOverlays(getContext())) {
            getActivity().startService(new Intent(getActivity(), FloatingViewService.class));
        } else {
            askPermission();
            call.reject("Call in progress floating widget plugin error: You need System Alert Window Permission to show floating widget");
        }

        implementation.show(timer);
    }

    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getContext().getPackageName()));
        getActivity().startActivity(intent);
    }


    @PluginMethod
    public void hide(PluginCall call) {
        implementation.hide();
    }
}
