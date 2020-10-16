package com.yaamani.battleshield.alpha.Game.Utilities;

public interface OnPermissionResult {

    void permissionGranted(int requestCode);

    void permissionDenied(int requestCode);

}
