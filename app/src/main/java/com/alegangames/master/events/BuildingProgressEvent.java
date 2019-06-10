package com.alegangames.master.events;

public class BuildingProgressEvent {
    private int progress;

    public BuildingProgressEvent(int i) {
        this.progress = i;
    }

    public int getProgress() {
        return this.progress;
    }

    public void setProgress(int i) {
        this.progress = i;
    }
}
