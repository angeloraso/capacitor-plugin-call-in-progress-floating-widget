package com.angeloraso.plugins.callinprogressfloatingwidget;

import com.getcapacitor.Logger;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.util.WebColor;

@CapacitorPlugin(name = "CallInProgressFloatingWidget")
public class CallInProgressFloatingWidgetPlugin extends Plugin {

    private CallInProgressFloatingWidget floatingWidget;

    public void load() {
      floatingWidget = new CallInProgressFloatingWidget(getContext());
    }

    @PluginMethod
    public void show(PluginCall call) {
        if (getActivity().isFinishing()) {
            call.reject("Call in progress floating widget plugin error: App is finishing");
            return;
        }

        floatingWidget.show(getSettings(call), new CallInProgressFloatingWidgetListener() {
          @Override
          public void tapWidget() {
            call.resolve();
          }
        });
    }

    @PluginMethod
    public void hide(PluginCall call) {
      floatingWidget.hide();
    }

    private CallInProgressFloatingWidgetSettings getSettings(PluginCall call) {
      CallInProgressFloatingWidgetSettings settings = new CallInProgressFloatingWidgetSettings();

      Integer startTime = call.getInt("startTime") ;
      if (startTime != null) {
        try {
          settings.setStartTime(startTime);
        } catch (IllegalArgumentException ex) {
          Logger.debug("startTime not applied");
        }
      }
      return settings;
    }
}
