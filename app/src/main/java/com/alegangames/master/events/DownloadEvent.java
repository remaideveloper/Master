package com.alegangames.master.events;

import java.io.File;

public class DownloadEvent {

    public static final int PROGRESS = 0;
    public static final int FINISHED = 1;
    public static final int CANCELED = 2;

    //Тип события - прогресс или окончание загрузки
    public int type;

    public int progress;
    public String percent;
    public String fullString;
    public String loadedString;

    public File file;
    public int status;
    public int requestCode;

    public DownloadEvent(int progress, String percent, String fullString, String loadedString, int requestCode) {
        this.progress = progress;
        this.percent = percent;
        this.fullString = fullString;
        this.loadedString = loadedString;
        this.requestCode = requestCode;
        type = PROGRESS;
    }

    public DownloadEvent(File file, int status, int requestCode) {
        this.file = file;
        this.status = status;
        this.requestCode = requestCode;
        type = FINISHED;
    }

    public DownloadEvent() {
        type = CANCELED;
    }
}
