package com.angeloraso.plugins.callinprogressfloatingwidget;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;

public class FloatingWidgetService extends Service {
  private WindowManager mWindowManager;
  private View mFloatingWidgetView, floatingWidget;
  private ImageView remove_image_view;
  private Point szWindow = new Point();
  private View removeFloatingWidgetView;
  private IBinder mLocalbinder = new LocalBinder();
  private CallBack mCallBack;
  private Chronometer callDuration;
  private Boolean floatingWidgetWasDestroyed = false;

  private int x_init_cord, y_init_cord, x_init_margin, y_init_margin;

  // Variable to check if the Floating widget view is on left side or in right side
  // initially we are displaying Floating widget view to Left side so set it to true
  private boolean isLeft = true;

  public FloatingWidgetService() {
  }

  @Override
  public IBinder onBind(Intent intent) {
    return mLocalbinder;
  }

  public class LocalBinder extends Binder {
    public FloatingWidgetService getService() {
      return FloatingWidgetService.this;
    }
  }

  public interface CallBack {
    void onClick();
    void onHide();
  }


  public void setCallBack(CallBack callBack) {
    mCallBack = callBack;
  }


  @Override
  public void onCreate() {
    super.onCreate();

    // Init WindowManager
    mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

    getWindowManagerDefaultDisplay();

    // Init LayoutInflater
    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

    addRemoveView(inflater);
    addFloatingWidgetView(inflater);
    implementTouchListenerToFloatingWidgetView();
  }

  public void setBackgroundColor(Integer color) {
    if (floatingWidget != null) {
      floatingWidget.setBackgroundColor(color);
    }
  }

  public void setStartTime(Integer startTime) {
    callDuration = floatingWidget.findViewById(R.id.call_duration_id);
    callDuration.setBase(SystemClock.elapsedRealtime() - (startTime * 1000));
    callDuration.start();
  }


  /* Add Remove View to Window Manager */
  private View addRemoveView(LayoutInflater inflater) {
    //Inflate the removing view layout we created
    removeFloatingWidgetView = inflater.inflate(R.layout.remove_floating_widget_layout, null);

    //Add the view to the window.
    WindowManager.LayoutParams paramRemove;
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
      paramRemove = new WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_PHONE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT);
    }else{
      paramRemove = new WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT);
    }

    // Specify the view position
    paramRemove.gravity = Gravity.TOP | Gravity.LEFT;

    // Initially the Removing widget view is not visible, so set visibility to GONE
    removeFloatingWidgetView.setVisibility(View.GONE);
    remove_image_view = removeFloatingWidgetView.findViewById(R.id.remove_img);

    // Add the view to the window
    mWindowManager.addView(removeFloatingWidgetView, paramRemove);
    return remove_image_view;
  }

  /* Add Floating Widget View to Window Manager */
  private void addFloatingWidgetView(LayoutInflater inflater) {
    //Inflate the floating view layout we created
    mFloatingWidgetView = inflater.inflate(R.layout.floating_widget_layout, null);

    //Add the view to the window.
    WindowManager.LayoutParams params;
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
      params = new WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_PHONE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT);
    }else{
      params = new WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT);
    }


    // Specify the view position
    params.gravity = Gravity.TOP | Gravity.LEFT;

    // Initially view will be added to top-left corner
    params.x = 10;
    params.y = 10;

    // Add the view to the window
    mWindowManager.addView(mFloatingWidgetView, params);

    // Find id of floating widget
    floatingWidget = mFloatingWidgetView.findViewById(R.id.root_container);
  }

  private void getWindowManagerDefaultDisplay() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
      mWindowManager.getDefaultDisplay().getSize(szWindow);
    else {
      DisplayMetrics displaymetrics = new DisplayMetrics();
      mWindowManager.getDefaultDisplay().getRealMetrics(displaymetrics);
      int w = displaymetrics.widthPixels;
      int h = displaymetrics.heightPixels;
      szWindow.set(w, h);
    }
  }

  /* Implement Touch Listener to Floating Widget Root View */
  private void implementTouchListenerToFloatingWidgetView() {
    // Drag and move floating view using user's touch action.
    mFloatingWidgetView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {

      long time_start = 0, time_end = 0;

      boolean isLongClick = false; // Variable to judge if user click long press
      boolean inBounded = false; // Variable to judge if floating view is bounded to remove view
      int remove_img_width = 0, remove_img_height = 0;

      Handler handler_longClick = new Handler();
      Runnable runnable_longClick = new Runnable() {
        @Override
        public void run() {
          // On Floating Widget Long Click

          // Set isLongClick as true
          isLongClick = true;
        }
      };

      @Override
      public boolean onTouch(View v, MotionEvent event) {

        // Get Floating widget view params
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mFloatingWidgetView.getLayoutParams();

        // Get the touch location coordinates
        int x_cord = (int) event.getRawX();
        int y_cord = (int) event.getRawY();

        int x_cord_Destination, y_cord_Destination;

        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            time_start = System.currentTimeMillis();

            handler_longClick.postDelayed(runnable_longClick, 500);

            remove_img_width = remove_image_view.getLayoutParams().width;
            remove_img_height = remove_image_view.getLayoutParams().height;

            x_init_cord = x_cord;
            y_init_cord = y_cord;

            // Remember the initial position.
            x_init_margin = layoutParams.x;
            y_init_margin = layoutParams.y;

            return true;
          case MotionEvent.ACTION_UP:
            isLongClick = false;
            removeFloatingWidgetView.setVisibility(View.GONE);
            remove_image_view.getLayoutParams().height = remove_img_height;
            remove_image_view.getLayoutParams().width = remove_img_width;
            handler_longClick.removeCallbacks(runnable_longClick);

            // If user drag and drop the floating widget view into remove view
            if (inBounded) {
              inBounded = false;
              onHide();
              break;
            }


            // Get the difference between initial coordinate and current coordinate
            int x_diff = x_cord - x_init_cord;
            int y_diff = y_cord - y_init_cord;

            // The check for x_diff <5 && y_diff< 5 because sometime elements moves a little while clicking.
            // So that is click event.
            if (Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5) {
              time_end = System.currentTimeMillis();

              // Also check the difference between start time and end time should be less than 300ms
              if ((time_end - time_start) < 300){
                onClick();
              }
            }

            y_cord_Destination = y_init_margin + y_diff;

            int barHeight = getStatusBarHeight();
            if (y_cord_Destination < 0) {
              y_cord_Destination = 0;
            } else if (y_cord_Destination + (mFloatingWidgetView.getHeight() + barHeight) > szWindow.y) {
              y_cord_Destination = szWindow.y - (mFloatingWidgetView.getHeight() + barHeight);
            }

            layoutParams.y = y_cord_Destination;

            inBounded = false;

            // Reset position if user drags the floating view
            resetPosition(x_cord);

            return true;
          case MotionEvent.ACTION_MOVE:
            int x_diff_move = x_cord - x_init_cord;
            int y_diff_move = y_cord - y_init_cord;

            x_cord_Destination = x_init_margin + x_diff_move;
            y_cord_Destination = y_init_margin + y_diff_move;

            // Set the remove widget position
            int x_cord_remove = (int) ((szWindow.x - (remove_img_height * 1.5)) / 2);
            int y_cord_remove = (int) (szWindow.y - ((remove_img_width * 1.5) + getStatusBarHeight()));

            WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeFloatingWidgetView.getLayoutParams();
            param_remove.x = x_cord_remove;
            param_remove.y = y_cord_remove;

            mWindowManager.updateViewLayout(removeFloatingWidgetView, param_remove);

            // Set remove widget view visibility to VISIBLE
            removeFloatingWidgetView.setVisibility(View.VISIBLE);

            // If user long click the floating view, update remove view
            if (isLongClick) {
              int x_bound_left = szWindow.x / 2 - (int) (remove_img_width * 1.5);
              int x_bound_right = szWindow.x / 2 + (int) (remove_img_width * 1.5);
              int y_bound_top = szWindow.y - (int) (remove_img_height * 1.5);

              //If Floating view comes under Remove View update Window Manager
              if ((x_cord >= x_bound_left && x_cord <= x_bound_right) && y_cord >= y_bound_top) {
                inBounded = true;

                if (remove_image_view.getLayoutParams().height == remove_img_height) {
                  remove_image_view.getLayoutParams().height = (int) (remove_img_height * 1.5);
                  remove_image_view.getLayoutParams().width = (int) (remove_img_width * 1.5);
                }

                layoutParams.x = x_cord_remove + (Math.abs(removeFloatingWidgetView.getWidth() - mFloatingWidgetView.getWidth())) / 2;
                layoutParams.y = y_cord_remove + (Math.abs(removeFloatingWidgetView.getHeight() - mFloatingWidgetView.getHeight())) / 2;

                // Update the layout with new X & Y coordinate
                mWindowManager.updateViewLayout(mFloatingWidgetView, layoutParams);

                break;
              } else {
                // If Floating window gets out of the Remove view update Remove view again
                inBounded = false;
                remove_image_view.getLayoutParams().height = remove_img_height;
                remove_image_view.getLayoutParams().width = remove_img_width;
              }
            }

            layoutParams.x = x_cord_Destination;
            layoutParams.y = y_cord_Destination;

            // Update the layout with new X & Y coordinate
            mWindowManager.updateViewLayout(mFloatingWidgetView, layoutParams);
            return true;
        }
        return false;
      }
    });
  }


  /* Reset position of Floating Widget view on dragging */
  private void resetPosition(int x_cord_now) {
    if (x_cord_now <= szWindow.x / 2) {
      isLeft = true;
      moveToLeft(x_cord_now);
    } else {
      isLeft = false;
      moveToRight(x_cord_now);
    }
  }

  /* Method to move the Floating widget view to Left */
  private void moveToLeft(final int current_x_cord) {
    final int x = szWindow.x - current_x_cord;

    new CountDownTimer(500, 5) {
      // Get params of Floating Widget view
      WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) mFloatingWidgetView.getLayoutParams();

      public void onTick(long t) {
        long step = (500 - t) / 5;

        mParams.x = 0 - (int) (current_x_cord * current_x_cord * step);

        // Update window manager for Floating Widget
        if (!floatingWidgetWasDestroyed) {
          mWindowManager.updateViewLayout(mFloatingWidgetView, mParams);
        }
      }

      public void onFinish() {
        mParams.x = 0;

        // Update window manager for Floating Widget
        if (!floatingWidgetWasDestroyed) {
          mWindowManager.updateViewLayout(mFloatingWidgetView, mParams);
        }
      }
    }.start();
  }

  /* Method to move the Floating widget view to Right */
  private void moveToRight(final int current_x_cord) {

    new CountDownTimer(500, 5) {
      // Get params of Floating Widget view
      WindowManager.LayoutParams mParams = (WindowManager.LayoutParams) mFloatingWidgetView.getLayoutParams();

      public void onTick(long t) {
        long step = (500 - t) / 5;

        mParams.x = (int) (szWindow.x + (current_x_cord * current_x_cord * step) - mFloatingWidgetView.getWidth());

        // Update window manager for Floating Widget
        mWindowManager.updateViewLayout(mFloatingWidgetView, mParams);
      }

      public void onFinish() {
        mParams.x = szWindow.x - mFloatingWidgetView.getWidth();

        // Update window manager for Floating Widget
        mWindowManager.updateViewLayout(mFloatingWidgetView, mParams);
      }
    }.start();
  }

  /*  return status bar height on basis of device display metrics  */
  private int getStatusBarHeight() {
    return (int) Math.ceil(25 * getApplicationContext().getResources().getDisplayMetrics().density);
  }


  /*  Update Floating Widget view coordinates on Configuration change  */
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    getWindowManagerDefaultDisplay();

    WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mFloatingWidgetView.getLayoutParams();

    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {


      if (layoutParams.y + (mFloatingWidgetView.getHeight() + getStatusBarHeight()) > szWindow.y) {
        layoutParams.y = szWindow.y - (mFloatingWidgetView.getHeight() + getStatusBarHeight());
        mWindowManager.updateViewLayout(mFloatingWidgetView, layoutParams);
      }

      if (layoutParams.x != 0 && layoutParams.x < szWindow.x) {
        resetPosition(szWindow.x);
      }

    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

      if (layoutParams.x > szWindow.x) {
        resetPosition(szWindow.x);
      }

    }

  }

  private void onClick() {
    mCallBack.onClick();
  }

  private void onHide() {
    mCallBack.onHide();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    floatingWidgetWasDestroyed = true;

    callDuration.stop();

    /* On destroy remove both view from window manager */

    if (mFloatingWidgetView != null) {
      mWindowManager.removeView(mFloatingWidgetView);
    }

    if (removeFloatingWidgetView != null) {
      mWindowManager.removeView(removeFloatingWidgetView);
    }

  }
}
