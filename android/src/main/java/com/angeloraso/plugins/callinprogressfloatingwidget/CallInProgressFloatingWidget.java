package com.angeloraso.plugins.callinprogressfloatingwidget;

public class CallInProgressFloatingWidget {
    private Integer seconds;

    public void setTimer(Integer seconds) {
        this.seconds = seconds;
    }

    public void show(Integer timer) {
        this.setTimer(timer);
        return;
    }

    public void hide() {
        this.setTimer(0);
        return;
    }

}
