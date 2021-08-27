package com.angeloraso.plugins.callinprogressfloatingwidget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

public class CallInProgressFloatingWidget implements FloatingWidgetService.CallBack {
    private Context context;
    private FloatingWidgetService floatingWidgetService;
    private Boolean mIsBound = false;
    private CallInProgressFloatingWidgetSettings settings;


    CallInProgressFloatingWidget(Context context) {
      this.context = context;
    }

    private ServiceConnection mConnection = new ServiceConnection() {
      public void onServiceConnected(ComponentName className, IBinder iBinder) {
        floatingWidgetService = ((FloatingWidgetService.LocalBinder)iBinder).getService();
        floatingWidgetService.setCallBack(CallInProgressFloatingWidget.this);
        floatingWidgetService.setStartTime(settings.getStartTime());
      }

      public void onServiceDisconnected(ComponentName className) {
        floatingWidgetService = null;
      }
    };

    void doBindService(Intent intent) {
      context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
      mIsBound = true;
    }

    void doUnbindService() {
      if (mIsBound) {
        // Detach our existing connection.
        context.unbindService(mConnection);
        mIsBound = false;
      }
    }


    /**
     * Show the call in progress floating widget
     *
     * @param settings Settings used to show the call in progress floating widget
     * @param listener A listener to handle user actions
     */
    public void show(final CallInProgressFloatingWidgetSettings settings, final CallInProgressFloatingWidgetListener listener) {
        Intent intent = new Intent("android.intent.action.FLOATING_WIDGET_SERVICE");
        intent.setPackage(context.getPackageName());
        this.settings = settings;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
          doBindService(intent);
        } else if (Settings.canDrawOverlays(context.getApplicationContext())) {
          doBindService(intent);
        }
    }

    @Override
    public void onClick() {
      openApp();
    }

    @Override
    public void onHide() {
      hide();
    }

    private void askPermission(final AppCompatActivity activity) {
      Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:" + activity.getPackageName()));
      activity.startActivity(intent);
    }


    public void hide() {
      doUnbindService();
    }

    public void openApp() {
      Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
      context.startActivity(intent);
      hide();
    }

}
