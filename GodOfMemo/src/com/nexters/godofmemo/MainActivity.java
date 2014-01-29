package com.nexters.godofmemo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.nexters.godofmemo.view.MemoGLView;

public class MainActivity extends ActionBarActivity {
	/**
	 * Hold a reference to our GLSurfaceView
	 */
	private GLSurfaceView glSurfaceView;
	private boolean rendererSet = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Check if the system supports OpenGL ES 2.0.
		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		final ConfigurationInfo configurationInfo = activityManager
				.getDeviceConfigurationInfo();

		/*
		 * final boolean supportsEs2 = configurationInfo.reqGlEsVersion >=
		 * 0x20000;
		 */

		// Even though the latest emulator supports OpenGL ES 2.0,
		// it has a bug where it doesn't set the reqGlEsVersion so
		// the above check doesn't work. The below will detect if the
		// app is running on an emulator, and assume that it supports
		// OpenGL ES 2.0.
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000
				|| (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 && (Build.FINGERPRINT
						.startsWith("generic")
						|| Build.FINGERPRINT.startsWith("unknown")
						|| Build.MODEL.contains("google_sdk")
						|| Build.MODEL.contains("Emulator") || Build.MODEL
							.contains("Android SDK built for x86")));

		if (supportsEs2) {
			glSurfaceView = new MemoGLView(this);
			rendererSet = true;
		} else {
			/*
			 * This is where you could create an OpenGL ES 1.x compatible
			 * renderer if you wanted to support both ES 1 and ES 2. Since we're
			 * not doing anything, the app will crash if the device doesn't
			 * support OpenGL ES 2.0. If we publish on the market, we should
			 * also add the following to AndroidManifest.xml:
			 * 
			 * <uses-feature android:glEsVersion="0x00020000"
			 * android:required="true" />
			 * 
			 * This hides our app from those devices which don't support OpenGL
			 * ES 2.0.
			 */
			Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
					Toast.LENGTH_LONG).show();
			return;
		}

		setContentView(glSurfaceView);

	}

	@Override
	protected void onPause() {
		super.onPause();

		if (rendererSet) {
			glSurfaceView.onPause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (rendererSet) {
			glSurfaceView.onResume();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_search:
			Toast.makeText(getApplicationContext(), "search",
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.action_settings:
			Toast.makeText(getApplicationContext(), "setting",
					Toast.LENGTH_SHORT).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
