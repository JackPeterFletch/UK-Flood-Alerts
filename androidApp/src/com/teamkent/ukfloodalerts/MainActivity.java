package com.teamkent.ukfloodalerts;

import java.io.IOException;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.content.IntentSender;
import android.view.Menu;

public class MainActivity extends Activity implements
LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {

	//Location Client
	LocationClient mLocationClient;

	//Current Location
	Location mLocation;

	//Geocoder object
	Geocoder geoloc;

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mLocationClient = new LocationClient(this, this, this);
		geoloc = new Geocoder(getBaseContext());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onStart(){
		super.onStart();
		mLocationClient.connect();
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onStop(){
		mLocationClient.disconnect();
		super.onStop();
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onPause(){
		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume(){
		//Check for Google Play Services, prompt for instal if not available.
		final int RQS_GooglePlayServices = 1;
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if(status == ConnectionResult.SUCCESS) {
			//We're good to continue - yay
		}else{
			GooglePlayServicesUtil.getErrorDialog(status, this, RQS_GooglePlayServices).show();
		}
		super.onResume();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(
						this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			/*
			 * If no resolution is available, display a dialog to the
			 * user with the error.
			 */
			showDialog(connectionResult.getErrorCode());
		}

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDisconnected() {
		//
	}

	@Override
	public void onLocationChanged(Location arg0) {
		mLocation = mLocationClient.getLastLocation();
	}

	/*
	 * Given a location return the double representation of its current geographical longitude
	 */
	public double getLong(Location location)
	{
		if(location!=null){
			return location.getLongitude();
		}else{
			return 0;
		}
	}

	/*
	 * Given a location return the double representation of its current geographical latitude
	 */
	public double getLat(Location location)
	{
		if(location!=null){
			return location.getLatitude();
		}else{
			return 0;
		}
	}

	/*
	 * Given a location object, return it's current human readable address.
	 */
	public String reGeocode(Location location) throws IOException
	{
		List<Address> list = geoloc.getFromLocation(getLat(location),getLong(location),1);
		if(list.get(0).getPostalCode() != null || list.get(0).getPostalCode() != ""){
			return list.get(0).getPostalCode();
		}else{
			return "";
		}
	}

	/*
	 * Given a postcode, return it's approx lat/long location
	 */
	public String geocode(String post) throws IOException
	{
		List<Address> list = geoloc.getFromLocationName(post, 1);
		String concat = "" + list.get(0).getLatitude() + ":" + list.get(0).getLongitude();
		return concat;
	}
}