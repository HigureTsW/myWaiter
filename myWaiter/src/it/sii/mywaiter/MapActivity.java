package it.sii.mywaiter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.*;

import it.sii.mywaiter.R;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.IntentSender;

public class MapActivity extends Activity implements 
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
LocationListener {

private final static int
CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

// Milliseconds per second
private static final int MILLISECONDS_PER_SECOND = 1000;
// Update frequency in seconds
public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
// Update frequency in milliseconds
private static final long UPDATE_INTERVAL =
   MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
// The fastest update frequency, in seconds
private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
// A fast frequency ceiling in milliseconds
private static final long FASTEST_INTERVAL =
   MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
// Define an object that holds accuracy and frequency parameters
LocationRequest mLocationRequest;	

MapView mMapView;
private static final LatLng POLITO = new LatLng(45.062958,7.660732);
GoogleMap mMap;
LocationClient mLocationClient;
Location mCurrentLocation;
MarkerOptions mo;
String nome;


////////////////////////////LETTURA DATABASE

//////////////////////////////////////////////////////////////////////////




@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.map_layout);
	
	
	 
	/*
	LatLng latla = new LatLng(45.0667, 7.7000);
	mo.position(latla);
	mo.title("Torino messaggio");
	Marker marker = mMap.addMarker(mo);
*/
	mMapView = (MapView) findViewById(R.id.mymapview);
	mMapView.onCreate(savedInstanceState);
	
	MapsInitializer.initialize(this);
	
	if (mMap == null) {
		 mMap = mMapView.getMap();
	     if (mMap != null) {

	    	 // Activate the My LOcation layer (not required)
	    	 mMap.setMyLocationEnabled(true);
	     
	     }
	}
	
	
	
	
	Thread t = new Thread(new Runnable() {
		public void run() {

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		/*
		nameValuePairs.add(new BasicNameValuePair("name","Juventus"));*/
		InputStream is;



		try
		{
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://fabiopettiti.altervista.org/ristoranti.php");
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();

		is = entity.getContent();

		BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) 
		{
		sb.append(line + "\n");
		}
		is.close();

		String result=sb.toString();


		JSONArray jArray = new JSONArray(result);
		MarkerOptions[] markerArray = new MarkerOptions[3];
		double j = 0;
		for(int i=0;i<jArray.length();i++){
			JSONObject json_data = jArray.getJSONObject(i);
			j = j + 0.1;
			//Piatto messaggio = new Piatto(json_data.getString("nome"), json_data.getDouble("prezzo"),json_data.getString("descrizione"), json_data.getString("ingrediente"), json_data.getDouble("rating"));
			//double lat = 45 + j;  //da modificare, provvisorio! TODO
			
			String addressToSearch = json_data.getString("indirizzo");
			addressToSearch = addressToSearch.replace(" ", "%20");
			JSONObject addressInfo = getAddressInfo(addressToSearch);
			
			double lng = ((JSONArray)addressInfo.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
	        double lat = ((JSONArray)addressInfo.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");                     
			
			MarkerOptions mo = new MarkerOptions()
				.position(new LatLng(lat, lng))
				.title(nome+" "+ lat + " " + jArray.length())
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.waiter_mini));
			
			markerArray[i]=mo;
		}
		
			Message msg = new Message();
			   
			msg.obj = markerArray;
			mHandler.sendMessage(msg);
		
		}
		catch(Exception e){
		Log.e("log_tag", "Error "+e.toString());
		}
		}
		});
		t.start();	
	
		
		
		
		/*LatLng latla = new LatLng(45.0667, 7.7000);
		mo.position(latla);
		mo.title("Torino messaggio");
		Marker marker = mMap.addMarker(mo);
		MarkerOptions mo = new MarkerOptions()
	 	.position(new LatLng(45.0667, 7.7000))
	 	.title(nome)
	 	.icon(BitmapDescriptorFactory.fromResource(R.drawable.waiter_mini));
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_polito));*/
	
		//Piatto messaggio = new Piatto(json_data.getString("nome"), json_data.getDouble("prezzo"),json_data.getString("descrizione"), json_data.getString("ingrediente"), json_data.getDouble("rating"));
	       
	       
		
		
		
		
		
	 
	 
	//mMap.addMarker(mo);
	/*
     * Create a new location client, using the enclosing class to
     * handle callbacks.
     */
    mLocationClient = new LocationClient(this, this, this);

    // Create the LocationRequest object
    mLocationRequest = LocationRequest.create();
    // Use high accuracy
    mLocationRequest.setPriority(
       LocationRequest.PRIORITY_HIGH_ACCURACY);
    // Set the update interval to 5 seconds
    mLocationRequest.setInterval(UPDATE_INTERVAL);
    // Set the fastest update interval to 1 second
    mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
}

Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
    	MarkerOptions[] mex= (MarkerOptions[]) msg.obj;    	
    	for (int i = 0; i< mex.length; i++){
    		mMap.addMarker(mex[i]);
    	}
    	
        
    }
};


/*
 * Called by Location Services when the request to connect the
 * client finishes successfully. At this point, it is possible to
 * request the current location or start periodic updates
 */	
@Override
public void onConnected(Bundle dataBundle) {
	// Display the connection status
    Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
    mCurrentLocation = mLocationClient.getLastLocation();
	LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
	mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
}

/*
 * Called by Location Services if the connection to the
 * location client drops because of an error.
 */
@Override
public void onDisconnected() {
	// Display the connection status
    Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
}

/*
 * Called by Location Services if the attempt to
 * Location Services fails.
 */
@Override
public void onConnectionFailed(ConnectionResult connectionResult) {
    /*
     * Google Play services can resolve some errors it detects.
     * If the error has a resolution, try sending an Intent to
     * start a Google Play services activity that can resolve
     * error.
     */
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
        showErrorDialog(connectionResult.getErrorCode());
    }
}		

void showErrorDialog(int code) {
	  GooglePlayServicesUtil.getErrorDialog(code, this,CONNECTION_FAILURE_RESOLUTION_REQUEST).show();
}	

@Override
public void onPause() {
	super.onPause();
	mMapView.onPause();
}

@Override
public void onResume() {
		super.onResume();
		mMapView.onResume();
}

@Override
public void onDestroy() {
	super.onDestroy();
	mMapView.onDestroy();
}		

/*
 * Called when the Activity becomes visible.
 */
@Override
protected void onStart() {
    super.onStart();
    // Connect the client.
    mLocationClient.connect();
}	

/*
 * Called when the Activity is no longer visible.
 */
@Override
protected void onStop() {
	
	if (mLocationClient.isConnected()) {
        /*
         * Remove location updates for a listener.
         * The current Activity is the listener, so
         * the argument is "this".
         */
		
		mLocationClient.removeLocationUpdates(this);
    }    	
	
    // Disconnecting the client invalidates it.
    mLocationClient.disconnect();
    super.onStop();
}

@Override
public void onLocationChanged(Location arg0) {
	// TODO Auto-generated method stub
	
}    	


public static JSONObject getAddressInfo(String sAddress) {
    HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?address=" + sAddress + "&sensor=false");
    HttpClient client = new DefaultHttpClient();
    HttpResponse response;
    StringBuilder stringBuilder = new StringBuilder();

    try {
        response = client.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream stream = entity.getContent();
        int b;
        while ((b = stream.read()) != -1) {
            stringBuilder.append((char) b);
        }
    } catch (ClientProtocolException e) {
    } catch (IOException e) {
    }

    JSONObject jsonObject = new JSONObject();
    try {
        jsonObject = new JSONObject(stringBuilder.toString());
        Log.d("Google Geocoding Response", stringBuilder.toString());
    } catch (JSONException e) {
        e.printStackTrace();
    }
    return jsonObject;
}	

	

}
