package com.yaamani.battleshield.alpha.Game.Utilities;

public interface AndroidPermissionHandler {

    int REQUEST_WRITE_TO_EXTERNAL_STORAGE = 1;

    boolean isWriteToExternalStoragePermissionGranted();

    void requestWriteToExternalStoragePermission();

}
