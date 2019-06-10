package com.alegangames.master.interfaces;


import com.alegangames.master.events.DownloadEvent;

public interface InterfaceDownload {
    void onDownloadComplete(DownloadEvent downloadEvent);

    void onDownloadUpdate(DownloadEvent downloadEvent);
}
