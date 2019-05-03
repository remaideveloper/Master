package com.remaistudio.master.interfaces;


import com.remaistudio.master.events.DownloadEvent;

public interface InterfaceDownload {
    void onDownloadComplete(DownloadEvent downloadEvent);

    void onDownloadUpdate(DownloadEvent downloadEvent);
}
