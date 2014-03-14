package com.example.wifiscanner3000;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Scanner extends Activity {

	WifiManager wifimgr;
	WifiScanReceiver wifiReciever;
	ListView wifiList;

	/**
	 * For the timer in the action bar
	 */
	private static TextView timerText;
	private static final long DURATION = 30000;	//30 seconds
	private static final long INTERVAL = 1000;
	private static int observationCount = 1;
	// private static boolean startedTimer = false;
	private static ActionTimer at = null;
	final int MENUITEMSTART = 0;
	final int MENUITEMSTOP = 1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scanner);
		wifiList = (ListView) findViewById(R.id.listView1);
		wifimgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (wifimgr.isWifiEnabled()) {
			Toast.makeText(this, "Disabling WiFi...", Toast.LENGTH_SHORT)
			.show();
			wifimgr.setWifiEnabled(false);
		}		
		Toast.makeText(this, "Enabling WiFi...", Toast.LENGTH_SHORT)
		.show();
		wifimgr.setWifiEnabled(true);
		Toast.makeText(this, "WiFi enabled. Starting Scan...", Toast.LENGTH_SHORT)
		.show();		
		wifiReciever = new WifiScanReceiver();
		wifimgr.startScan();
	}
	
	/**
	 * For action bar
	 * 
	 * @author Jesse
	 */
	@Override
	public boolean  onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.scanner, menu);

		// For timer
		MenuItem timerItem = menu.findItem(R.id.break_timer);
		timerText = (TextView) MenuItemCompat.getActionView(timerItem);
		timerText.setPadding(10, 0, 10, 0);
	    return true;
	}

	protected void onPause() {
		unregisterReceiver(wifiReciever);
		super.onPause();
	}

	protected void onResume() {
		registerReceiver(wifiReciever, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		super.onResume();
	}

	class WifiScanReceiver extends BroadcastReceiver {
		@SuppressLint("UseValueOf")
		public void onReceive(Context c, Intent intent) {
				
			List<ScanResult> wifi = wifimgr.getScanResults();
			Iterator<ScanResult> wifiResults = wifi.iterator();
			String wifiDetails[] = new String[wifi.size()];
			int i = 0;
			while (wifiResults.hasNext()) {
				ScanResult sr = wifiResults.next();
				wifiDetails[i++] = "" + new Date().getTime() + "," +
				sr.SSID + ", " + 
				sr.BSSID + ", " + sr.level + ", " + sr.frequency + 
				", " + sr.capabilities;
			}

			wifiList.setAdapter(new ArrayAdapter<String>(
					getApplicationContext(),
					android.R.layout.simple_list_item_1, wifiDetails));
		}
	}
	
	
	
	/**
	 * Timer for the ActionBar
	 * 
	 * @author Jesse
	 */
	private class ActionTimer {
		private CountDownTimer timer = null;

		public ActionTimer() {
			startTimer();
		}

		public void cancel() {
			if (null != this.timer) {
				timerText.setText("");
				this.timer.cancel();
				this.timer = null;
			}
		}

		private void startTimer() {
			if (null != timer) {
				cancel();
			}
			timer = new CountDownTimer(DURATION, INTERVAL) {

				/**
				 * When the countdown finishes: increment the observation
				 * counter - reset the interface - Restart the countdown
				 */
				@Override
				public void onFinish() {
					// dummyDataHandler();
					observationCount++;
					reset();
					this.start();
				}

				/**
				 * Update the actionbar text on each tick so the user knows how
				 * much time is remaining in current observation
				 */
				@Override
				public void onTick(long millisecondsLeft) {
					long numSec = millisecondsLeft / 1000;
					timerText.setText("" + numSec + " second"
							+ ((1 == numSec) ? "" : "s")
							+ " remaining for observation " + observationCount);

				}
			};

			timer.start();
		}

		/**
		 * Reset observer by clearing the selections from the places and tasks
		 * fragments
		 */
		private void reset() {
		}
	}

}