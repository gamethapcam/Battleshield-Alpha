package com.yaamani.battleshield.alpha;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.yaamani.battleshield.alpha.Game.Utilities.AndroidPermissionHandler;
import com.yaamani.battleshield.alpha.Game.BattleshieldGame;
import com.yaamani.battleshield.alpha.Game.Utilities.OnPermissionResult;

public class AndroidLauncher extends AndroidApplication implements AndroidPermissionHandler {

	private OnPermissionResult onPermissionResult;
	private BattleshieldGame battleshieldGame;


	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;

		battleshieldGame = new BattleshieldGame(this);
		initialize(battleshieldGame, config);

		onPermissionResult = battleshieldGame;

	}

	@Override
	protected void onDestroy() {
		if (!battleshieldGame.isDestroyed())
			battleshieldGame.dispose();
		super.onDestroy();
	}

	@Override
	public boolean isWriteToExternalStoragePermissionGranted() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
		} else
			return true;
	}

	@Override
	public void requestWriteToExternalStoragePermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, AndroidPermissionHandler.REQUEST_WRITE_TO_EXTERNAL_STORAGE);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == AndroidPermissionHandler.REQUEST_WRITE_TO_EXTERNAL_STORAGE) {

			if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				onPermissionResult.permissionGranted(requestCode);
			else {
				Toast.makeText(this, "يخربيت البيض !!", Toast.LENGTH_LONG).show();
				onPermissionResult.permissionDenied(requestCode);
			}

		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
}
