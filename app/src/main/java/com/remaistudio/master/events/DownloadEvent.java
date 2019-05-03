package com.remaistudio.master.events;

import androidx.annotation.NonNull;

import java.io.File;

public class DownloadEvent {
    public static final int FINISHED = 1;
    public static final int LOADED = 2;
    public static final int PROGRESS = 0;
    public File file;
    public String fullString;
    public String loadedString;
    public String percent;
    public int progress;
    public int requestCode;
    public int status;
    public int type = 0;

    public DownloadEvent(int i, String str, String str2, String str3, int i2) {
        this.progress = i;
        this.percent = str;
        this.fullString = str2;
        this.loadedString = str3;
        this.requestCode = i2;
    }

    public DownloadEvent(File file, int i, int i2) {
        this.file = file;
        this.status = i;
        this.requestCode = i2;
    }

    @NonNull
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("type: ");
        stringBuilder.append(this.type);
        stringBuilder.append("\nfile: ");
        stringBuilder.append(this.file.toString());
        stringBuilder.append("\nstatus: ");
        stringBuilder.append(this.status);
        stringBuilder.append("\nrequestCode: ");
        stringBuilder.append(this.requestCode);
        return stringBuilder.toString();
    }
}
